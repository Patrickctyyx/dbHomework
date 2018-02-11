package hello.controller;

import com.alibaba.fastjson.JSONObject;
import hello.entity.*;
import hello.service.*;
import hello.utils.CheckParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserClubRepository userClubRepository;
    @Autowired
    private ClubRepository clubRepository;
    @Autowired
    private ActivityRepository activityRepository;

    private Map<String, Object> isAdmin(JSONObject paramJSON) {
        Map<String, Object> response = new LinkedHashMap<String, Object>();

        String token = paramJSON.getString("token");
        if (token == null) {
            response.put("status", "error");
            response.put("message", "lacking token!");
            return response;
        }

        String wxID = UserEntity.checkAuthToken(token);
        if (wxID.length() == 0) {
            response.put("status", "error");
            response.put("message", "invalid token!");
            return response;
        }

        Long clubID = paramJSON.getLong("club_id");
        if (clubID == null) {
            response.put("status", "error");
            response.put("message", "club does not exist!");
            return response;
        }

        UserEntity user = userRepository.findFirstByWxID(wxID);
        ClubEntity club = clubRepository.findFirstById(clubID);
        if (user == null || club == null) {
            response.put("status", "error");
            response.put("message", "permission denied!");
            return response;
        }
        UserClubEntity userClub = userClubRepository.findFirstByUserAndClub(
                user, club
        );
        if (userClub.getUserIdentity().equals("officer")) {
            response.put("status", "error");
            response.put("message", "permission denied!");
            return response;
        }

        response.put("status", "success");
        return response;
    }

    @PostMapping("/revise_identity")
    public Map<String, Object> reviseIdentity(@RequestBody JSONObject reviseJSON) {
        Map<String, Object> response = isAdmin(reviseJSON);
        if (response.get("status").equals("error")) {
            return response;
        }

        Long id = reviseJSON.getLong("id");
        String newIdentity = reviseJSON.getString("new_identity");
        if (id == null || newIdentity == null) {
            response.put("status", "error");
            response.put("message", "lacking id or identity!");
            return response;
        }

        UserClubEntity revisedUserClub = userClubRepository.findFirstByUserAndClub(
                userRepository.findFirstById(id),
                clubRepository.findFirstById(reviseJSON.getLong("club_id"))
        );
        if (revisedUserClub == null) {
            response.put("status", "error");
            response.put("message", "user do not exist!");
            return response;
        }
        revisedUserClub.setUserIdentity(newIdentity);
        userClubRepository.save(revisedUserClub);
        response.put("status", "success");
        return response;
    }

    @PostMapping("/new_activity")
    public Map<String, Object> newActivity(@RequestBody JSONObject activityJSON) {
        Map<String, Object> response =  isAdmin(activityJSON);
        if (response.get("status").equals("error")) {
            return response;
        }

        String token = activityJSON.getString("token");
        String wxID = UserEntity.checkAuthToken(token);
        UserEntity user = userRepository.findFirstByWxID(wxID);
        Long clubID = activityJSON.getLong("club_id");
        ClubEntity club = clubRepository.findFirstById(clubID);

        ActivityEntity activity = new ActivityEntity();
        activity.setTheme(activityJSON.getString("theme"));
        activity.setContent(activityJSON.getString("content"));
        activity.setStart_time(activityJSON.getTimestamp("start_time"));
        activity.setTarget_dep(activityJSON.getString("target_dep"));
        activity.setClub(club);
        activity.setUser(user);

        activityRepository.save(activity);

        response.put("status", "success");
        response.put("id", activity.getId());
        return response;
    }

    @PostMapping("/edit_activity")
    public Map<String, Object> editActivity(@RequestBody JSONObject editJSON) {
        Map<String, Object> response = isAdmin(editJSON);
        if (response.get("status").equals("error")) {
            return response;
        }

        String token = editJSON.getString("token");
        String wxID = UserEntity.checkAuthToken(token);
        UserEntity user = userRepository.findFirstByWxID(wxID);
        Long activityID = editJSON.getLong("activity_id");
        ActivityEntity activity = activityRepository.findFirstById(activityID);

        if (activity == null) {
            response.put("status", "error");
            response.put("message", "activity does not exist!");
            return response;
        }

        if (activity.getUser() != user) {
            response.put("status", "error");
            response.put("message", "permission denied, not the same admin!");
            return response;
        }

        activity.setTheme(editJSON.getString("theme"));
        activity.setContent(editJSON.getString("content"));
        activity.setStart_time(editJSON.getTimestamp("start_time"));
        activity.setTarget_dep(editJSON.getString("target_dep"));

        activityRepository.save(activity);

        response.put("status", "success");
        response.put("id", activity.getId());
        return response;
    }

    // 管理员创建新用户并将其加到特定社团
    @PostMapping("/new_user")
    public Map<String, Object> newUser(@RequestBody JSONObject userJSON) {
        Map<String, Object> response = isAdmin(userJSON);
        if (response.get("status").equals("error")) {
            return response;
        }

        UserEntity user = userRepository.findFirstByEmail(userJSON.getString("email"));
        ClubEntity club = clubRepository.findFirstById(userJSON.getLong("club"));
        if (user != null) {
            if (userClubRepository.findFirstByUserAndClub(user, club) != null) {
                response.put("status", "error");
                response.put("message", "user is already in your club!");
                return response;
            }
        }

        else {
            if (!CheckParams.checkEmail(userJSON.getString("email"))) {
                response.put("status", "error");
                response.put("message", "invalid email format!");
                return response;
            }
            if (!CheckParams.checkPhone(userJSON.getString("phone"))) {
                response.put("status", "error");
                response.put("message", "invalid phone format!");
                return response;
            }

            user = new UserEntity(
                    userJSON.getString("name"),
                    userJSON.getString("grade"),
                    userJSON.getString("college"),
                    userJSON.getString("major"),
                    userJSON.getString("phone"),
                    userJSON.getString("qq"),
                    userJSON.getString("wechat"),
                    userJSON.getString("email")
            );
            if(userRepository.findFirstByPhone(userJSON.getString("phone")) != null ||
                    userRepository.findFirstByEmail(userJSON.getString("email")) != null) {
                response.put("status", "error");
                response.put("message", "information already exists!");
                return response;
            }
            userRepository.save(user);
        }

        UserClubEntity userClub = new UserClubEntity();
        userClub.setUser(user);
        userClub.setClub(club);
        userClubRepository.save(userClub);

        response.put("status", "success");
        response.put("message", "Create successfully!");
        response.put("id", user.getId());
        return response;
    }
}
