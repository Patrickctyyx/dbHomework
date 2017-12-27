package hello.controller;

import com.alibaba.fastjson.JSONObject;
import hello.entity.*;
import hello.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class AdminController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserClubRepository userClubRepository;
    @Autowired
    private ClubRepository clubRepository;
    @Autowired
    private ActivityRepository activityRepository;

    private Integer isAdmin(JSONObject paramJSON) {
        String token = paramJSON.getString("token");
        if (token == null) {
            return 2;
        }

        String wxID = UserEntity.checkAuthToken(token);
        if (wxID.length() == 0) {
            return 3;
        }

        Long clubID = paramJSON.getLong("club_id");
        if (clubID == null) {
            return 5;
        }

        UserEntity user = userRepository.findFirstByWxID(wxID);
        ClubEntity club = clubRepository.findFirstById(clubID);
        if (user == null || club == null) {
            return 4;
        }
        UserClubEntity userClub = userClubRepository.findFirstByUserAndClub(
                user, club
        );
        if (userClub.getUserIdentity().equals("officer")) {
            return 4;
        }

        return 1;
    }

    @PostMapping("/revise_identity")
    public Map<String, Object> reviseIdentity(@RequestBody JSONObject reviseJSON) {
        Map<String, Object> response = new LinkedHashMap<String, Object>();

        Integer status = isAdmin(reviseJSON);

        if (status == 2) {
            response.put("status", "error");
            response.put("message", "lacking token!");
            return response;
        }

        if (status == 3) {
            response.put("status", "error");
            response.put("message", "invalid token!");
            return response;
        }

        if (status == 4) {
            response.put("status", "error");
            response.put("message", "permission denied!");
            return response;
        }

        if (status == 5) {
            response.put("status", "error");
            response.put("message", "club does not exist!");
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
        response.put("status", "ok");
        return response;
    }

    @PostMapping("/new_activity")
    public Map<String, Object> newActivity(@RequestBody JSONObject activityJSON) {
        Map<String, Object> response = new LinkedHashMap<String, Object>();

        Integer status = isAdmin(activityJSON);

        if (status == 2) {
            response.put("status", "error");
            response.put("message", "lacking token!");
            return response;
        }

        if (status == 3) {
            response.put("status", "error");
            response.put("message", "invalid token!");
            return response;
        }

        if (status == 4) {
            response.put("status", "error");
            response.put("message", "permission denied!");
            return response;
        }

        if (status == 5) {
            response.put("status", "error");
            response.put("message", "club does not exist!");
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

    // todo: 管理员修改活动
}
