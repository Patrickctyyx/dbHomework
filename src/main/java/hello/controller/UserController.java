package hello.controller;

import com.alibaba.fastjson.JSONObject;
import hello.entity.ClubEntity;
import hello.entity.UserClubEntity;
import hello.entity.UserEntity;
import hello.service.ClubRepository;
import hello.service.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@RestController
public class UserController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ClubRepository clubRepository;

    @PostMapping("/new_user")
    public Map<String, Object> newUser(@RequestBody JSONObject userJSON) {
        Map<String, Object> response = new LinkedHashMap<String, Object>();

        UserEntity user = new UserEntity(
                userJSON.getString("name"),
                userJSON.getString("grade"),
                userJSON.getString("college"),
                userJSON.getString("major"),
                userJSON.getString("department"),
                userJSON.getString("phone"),
                userJSON.getString("qq"),
                userJSON.getString("wechat"),
                userJSON.getString("email")
        );
        // todo:注意重复数据的问题
        if(userRepository.findFirstByPhone(userJSON.getString("phone")) != null ||
        userRepository.findFirstByEmail(userJSON.getString("email")) != null) {
            response.put("status", "error");
            response.put("message", "information already exists!");
            return response;
        }
        userRepository.save(user);

        // user = userRepository.findFirstByPhone(userJSON.getString("phone"));


        response.put("status", "success");
        response.put("message", "Create successfully!");
        response.put("id", user.getId());
        return response;
    }

    @GetMapping("/userinfo/{id}")
    public Map<String, Object> getUserinfo(@PathVariable Long id) {
        Map<String, Object> response = new LinkedHashMap<String, Object>();

        UserEntity user = userRepository.findFirstById(id);
        if (user == null) {
            response.put("status", "error");
            response.put("message", "user does not exist!");
            return response;
        }

        response.put("id", id);
        response.put("name", user.getName());
        response.put("grade", user.getGrade());
        response.put("college", user.getCollege());
        response.put("major", user.getMajor());
        response.put("department", user.getDepartment());
        response.put("phone", user.getPhone());
        response.put("qq", user.getQq());
        response.put("wechat", user.getWechat());
        response.put("email", user.getEmail());
        response.put("introduction", user.getIntroduction());
        Set<UserClubEntity> clubs = user.getUserClubs();
        List<Map<String, Object>> clubInfo = new LinkedList<Map<String, Object>>();
        for (UserClubEntity club: clubs) {
            Map<String, Object> clubMap = new LinkedHashMap<String, Object>();
            clubMap.put("name", club.getClub().getName());
            clubMap.put("club_id", club.getClub().getId());
            clubInfo.add(clubMap);
        }
        response.put("joined_club", clubInfo);

        return response;
    }

    @PostMapping("/edit_userinfo")
    public Map<String, Object> editUserinfo(@RequestBody JSONObject userinfoJSON) {
        Map<String, Object> response = new LinkedHashMap<String, Object>();

        String token = userinfoJSON.getString("token");
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

        UserEntity user = userRepository.findFirstByWxID(wxID);
        user.setCollege(userinfoJSON.getString("college"));
        user.setDepartment(userinfoJSON.getString("college"));
        user.setGrade(userinfoJSON.getString("grade"));
        user.setMajor(userinfoJSON.getString("major"));
        if (userRepository.findFirstByEmail(userinfoJSON.getString("email")) != null) {
            response.put("status", "error");
            response.put("message", "duplicate email!");
            return response;
        }
        user.setEmail(userinfoJSON.getString("email"));
        if (userRepository.findFirstByQq(userinfoJSON.getString("qq")) != null) {
            response.put("status", "error");
            response.put("message", "duplicate qq!");
            return response;
        }
        user.setQq(userinfoJSON.getString("qq"));
        if (userRepository.findFirstByWechat(userinfoJSON.getString("wechat")) != null) {
            response.put("status", "error");
            response.put("message", "duplicate wechat!");
            return response;
        }
        user.setWechat(userinfoJSON.getString("wechat"));
        if (userRepository.findFirstByPhone(userinfoJSON.getString("phone")) != null) {
            response.put("status", "error");
            response.put("message", "duplicate phone!");
            return response;
        }
        user.setPhone(userinfoJSON.getString("phone"));
        user.setIntroduction(userinfoJSON.getString("introduction"));

        userRepository.save(user);
        response.put("status", "success");
        response.put("id", user.getId());
        return response;
    }

//    @PostMapping("/test_par")
//    public Map<String, Object> testPar(UserEntity user) {
//        // 这样的参数就只能识别 form-data，而其他的都不能识别了
//        System.out.println(user.getCollege());
//        System.out.println(user.getDepartment());
//        Map<String, Object> response = new LinkedHashMap<String, Object>();
//        response.put("message", "success!");
//        return response;
//    }
}
