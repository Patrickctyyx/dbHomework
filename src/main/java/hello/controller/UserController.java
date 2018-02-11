package hello.controller;

import com.alibaba.fastjson.JSONObject;
import hello.entity.ApplicationEntity;
import hello.entity.UserClubEntity;
import hello.entity.UserEntity;
import hello.service.ApplicationRepository;
import hello.service.ClubRepository;
import hello.service.UserRepository;
import hello.utils.CheckParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@RestController
public class UserController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ClubRepository clubRepository;
    @Autowired
    private ApplicationRepository applicationRepository;

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

        if (user.getName() == null) {
            user.setName(userinfoJSON.getString("name"));
        }
        user.setCollege(userinfoJSON.getString("college"));
        user.setGrade(userinfoJSON.getString("grade"));
        user.setMajor(userinfoJSON.getString("major"));

        if (userRepository.findFirstByEmail(userinfoJSON.getString("email")) != null &&
                !userRepository.findFirstByEmail(userinfoJSON.getString("email")).getId().equals(user.getId())
                ) {
            response.put("status", "error");
            response.put("message", "duplicate email!");
            return response;
        }
        if (!CheckParams.checkEmail(userinfoJSON.getString("email"))) {
            response.put("status", "error");
            response.put("message", "invalid email format!");
            return response;
        }
        user.setEmail(userinfoJSON.getString("email"));

        if (userRepository.findFirstByQq(userinfoJSON.getString("qq")) != null &&
                !userRepository.findFirstByQq(userinfoJSON.getString("qq")).getId().equals(user.getId())
                ) {
            response.put("status", "error");
            response.put("message", "duplicate qq!");
            return response;
        }
        user.setQq(userinfoJSON.getString("qq"));

        if (userRepository.findFirstByWechat(userinfoJSON.getString("wechat")) != null &&
                !userRepository.findFirstByWechat(userinfoJSON.getString("wechat")).getId().equals(user.getId())) {
            response.put("status", "error");
            response.put("message", "duplicate wechat!");
            return response;
        }
        user.setWechat(userinfoJSON.getString("wechat"));

        if (userRepository.findFirstByPhone(userinfoJSON.getString("phone")) != null &&
                !userRepository.findFirstByPhone(userinfoJSON.getString("phone")).getId().equals(user.getId())) {
            response.put("status", "error");
            response.put("message", "duplicate phone!");
            return response;
        }
        if (!CheckParams.checkPhone(userinfoJSON.getString("phone"))) {
            response.put("status", "error");
            response.put("message", "invalid phone format!");
            return response;
        }
        user.setPhone(userinfoJSON.getString("phone"));

        user.setIntroduction(userinfoJSON.getString("introduction"));

        userRepository.save(user);
        response.put("status", "success");
        response.put("id", user.getId());
        return response;
    }

    // 查看正在申请和没有通过的社团
    @GetMapping("/user/applied")
    private Map<String, Object> findAppliedClubs(@RequestParam("token") String token) {

        Map<String, Object> response = new LinkedHashMap<String, Object>();

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
        String phone = user.getPhone();

        List<ApplicationEntity> applications = applicationRepository.findByPhoneOrderByCredAtDesc(phone);
        if (applications == null) {
            return response;
        }
        List<Long> apply_ids = new LinkedList<Long>();
        for (ApplicationEntity application: applications) {
            if (!application.getStatus().equals("accepted")) {
                apply_ids.add(application.getClub().getId());
            }
        }
        response.put("applied_ids", apply_ids);
        return response;
    }

    // 查看申请通过的社团
    @GetMapping("/user/accepted")
    private Map<String, Object> findAcceptedClubs(@RequestParam("token") String token) {

        System.out.println(token);
        Map<String, Object> response = new LinkedHashMap<String, Object>();

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
        String phone = user.getPhone();

        List<ApplicationEntity> applications = applicationRepository.findByPhoneOrderByCredAtDesc(phone);
        if (applications == null) {
            return response;
        }
        List<Long> apply_ids = new LinkedList<Long>();
        for (ApplicationEntity application: applications) {
            if (application.getStatus().equals("accepted")) {
                apply_ids.add(application.getClub().getId());
            }
        }
        response.put("accepted_ids", apply_ids);
        return response;
    }

    @GetMapping("/user/applied/{phone}")
    private List<Map<String, Object>> findAppliedClubsByPhone(@PathVariable String phone) {

        List<Map<String, Object>> response = new LinkedList<Map<String, Object>>();

        List<ApplicationEntity> applications = applicationRepository.findByPhoneOrderByCredAtDesc(phone);
        if (applications == null) {
            return response;
        }
        List<Long> apply_ids = new LinkedList<Long>();
        for (ApplicationEntity application: applications) {
            Map<String, Object> statusResult = new LinkedHashMap<String, Object>();
            statusResult.put("club_id", application.getClub().getId());
            statusResult.put("status", application.getStatus());
            response.add(statusResult);
        }
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
