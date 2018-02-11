package hello.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import hello.entity.ApplicationEntity;
import hello.entity.ClubEntity;
import hello.entity.UserClubEntity;
import hello.entity.UserEntity;
import hello.service.*;
import hello.utils.CheckParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class ApplicationController {
    @Autowired
    private ApplicationRepository applicationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ClubRepository clubRepository;
    @Autowired
    private UserClubRepository userClubRepository;
    @Autowired
    private ApplicationPagingRepository applicationPagingRepository;

    @PostMapping("/new_apply")
    public Map<String, Object> newApply(@RequestBody JSONObject applyJSON) {
        // 使用 @RequestBody 后就只能接受 JSON 作为参数了，不能接受 form-data 的数据
        // Content-Type 要为 application/json
        Map<String, Object> response = new LinkedHashMap<String, Object>();
        String [] attrs = {
                "name",
                "grade",
                "college",
                "major",
                "club_name",
                "department",
                "phone",
                "email",
                "introduction"
        };
        // 检查是不是有除了介绍之外的信息为空
        for (String attr: attrs) {
            if (!attr.equals("introduction") && (applyJSON.getString(attr) == null || applyJSON.getString(attr).equals(""))) {
                response.put("status", "error");
                response.put("message", "lacking " + attr + "!");
                return response;
            }
        }

        ClubEntity club = clubRepository.findFirstByName(applyJSON.getString("club_name"));
        if (club == null) {
            response.put("status", "error");
            response.put("message", "club do not exists!");
            return response;
        }
        if (!CheckParams.checkEmail(applyJSON.getString("email"))) {
            response.put("status", "error");
            response.put("message", "invalid email format!");
            return response;
        }
        if (!CheckParams.checkPhone(applyJSON.getString("phone"))) {
            response.put("status", "error");
            response.put("message", "invalid phone format!");
            return response;
        }
        ApplicationEntity apply = new ApplicationEntity(
                applyJSON.getString("name"),
                applyJSON.getString("grade"),
                applyJSON.getString("college"),
                applyJSON.getString("major"),
                applyJSON.getString("department"),
                applyJSON.getString("phone"),
                applyJSON.getString("email"),
                applyJSON.getString("introduction"),
                club
        );
        ApplicationEntity oldApplyPhone = applicationRepository.findFirstByPhone(applyJSON.getString("phone"));
        ApplicationEntity oldApplyEmail = applicationRepository.findFirstByEmail(applyJSON.getString("email"));
        UserEntity alreadyUserPhone = userRepository.findFirstByPhone(applyJSON.getString("phone"));
        UserEntity alreadyUserEmail = userRepository.findFirstByEmail(applyJSON.getString("email"));


        // 已经发送过申请，且申请是对这个社团的，或者原本就是这个社团的不能再发送申请
        if ((oldApplyPhone != null && oldApplyPhone.getClub().getId().equals(club.getId())) ||
                (oldApplyEmail != null && oldApplyEmail.getClub().getId().equals(club.getId())) ||
                (alreadyUserPhone != null && userClubRepository.findFirstByUserAndClub(alreadyUserPhone, club) != null) ||
                (alreadyUserEmail != null && userClubRepository.findFirstByUserAndClub(alreadyUserEmail, club) != null)) {
            response.put("status", "error");
            response.put("message", "information already exists!");
            return response;
        }

        applicationRepository.save(apply);
        apply = applicationRepository.findFirstByPhone(applyJSON.getString("phone"));
        response.put("status", "success");
        response.put("id", apply.getId());
        return response;
    }


    @GetMapping("/applications/club/{club_id}")
    public List<Map<String, Object>> showApplies(@PathVariable Long club_id, @RequestParam(value = "page", defaultValue = "0") Integer page) {
        List<Map<String, Object>> resultList = new LinkedList<Map<String, Object>>();
        ClubEntity club = clubRepository.findFirstById(club_id);
        if (club == null) {
            return resultList;
        }
        Sort sort = new Sort(Sort.Direction.DESC, "credAt");
        Pageable pageable = new PageRequest(page, 10, sort);
        List<ApplicationEntity> applications = applicationPagingRepository.findAll(pageable).getContent();
        for (ApplicationEntity apply: applications) {
            Map<String, Object> applyMap = new LinkedHashMap<String, Object>();
            applyMap.put("id", apply.getId());
            applyMap.put("status", apply.getStatus());
            applyMap.put("name", apply.getName());
            applyMap.put("grade", apply.getGrade());
            applyMap.put("college", apply.getCollege());
            applyMap.put("major", apply.getMajor());
            applyMap.put("department", apply.getDepartment());
            applyMap.put("phone", apply.getPhone());
            applyMap.put("email", apply.getEmail());
            applyMap.put("introduction", apply.getIntroduction());
            applyMap.put("cred_at", apply.getCredAt());
            resultList.add(applyMap);
        }
        return resultList;
    }

    @GetMapping("/applications/id/{id}")
    public Map<String, Object> getApply(@PathVariable Long id) {
        ApplicationEntity apply = applicationRepository.findFirstById(id);
        Map<String, Object> applyMap = new LinkedHashMap<String, Object>();

        if (apply == null) {
            applyMap.put("status", "error");
            applyMap.put("message", "application not found!");
            return applyMap;
        }

        applyMap.put("id", apply.getId());
        applyMap.put("status", apply.getStatus());
        applyMap.put("name", apply.getName());
        applyMap.put("grade", apply.getGrade());
        applyMap.put("college", apply.getCollege());
        applyMap.put("major", apply.getMajor());
        applyMap.put("department", apply.getDepartment());
        applyMap.put("phone", apply.getPhone());
        applyMap.put("email", apply.getEmail());
        applyMap.put("introduction", apply.getIntroduction());
        applyMap.put("club_name", apply.getClub().getName());
        applyMap.put("cred_at", apply.getCredAt());
        return applyMap;
    }

    @PostMapping("/handle_apply")
    public Map<String, Object> handleApply(@RequestBody JSONObject handleResultJSON) {
        Map<String, Object> response = new LinkedHashMap<String, Object>();
        String token = handleResultJSON.getString("token");
        if (token == null) {
            response.put("status", "error");
            response.put("message", "lacking token!");
            return response;
        }

        Long clubID = handleResultJSON.getLong("club_id");
        if (clubID == null) {
            response.put("status", "error");
            response.put("message", "lacking club id!");
            return response;
        }

        String wxID = UserEntity.checkAuthToken(token);
        if (wxID.length() == 0) {
            response.put("status", "error");
            response.put("message", "invalid token!");
            return response;
        }

        UserEntity user = userRepository.findFirstByWxID(wxID);
        ClubEntity club = clubRepository.findFirstById(clubID);
        UserClubEntity userClub = userClubRepository.findFirstByUserAndClub(
                user, club
        );
        if (userClub != null && userClub.getUserIdentity().equals("officer")) {
            response.put("status", "error");
            response.put("message", "permission denied!");
            return response;
        }

        JSONArray resultArray = handleResultJSON.getJSONArray("handle_result");
        for (Iterator iterator = resultArray.iterator(); iterator.hasNext();) {
            JSONObject resultJSON = (JSONObject) iterator.next();
            Long id = resultJSON.getLong("id");
            ApplicationEntity apply = applicationRepository.findFirstById(id);
            if (!resultJSON.getBoolean("result")) {
                apply.setStatus("rejected");
            }
            else {
                apply.setStatus("accepted");

                UserEntity newUser = userRepository.findFirstByPhone(apply.getPhone());

                if (newUser == null) {
                    newUser = new UserEntity();
                    newUser.setName(apply.getName());
                    newUser.setGrade(apply.getGrade());
                    newUser.setCollege(apply.getCollege());
                    newUser.setMajor(apply.getMajor());
                    newUser.setPhone(apply.getPhone());
                    newUser.setEmail(apply.getEmail());
                    newUser.setQq(apply.getId().toString());
                    newUser.setWechat(apply.getId().toString());
                    newUser.setIntroduction(apply.getIntroduction());
                    userRepository.save(newUser);
                }

                UserClubEntity newUserClub = new UserClubEntity();
                newUserClub.setClub(club);
                newUserClub.setUser(newUser);
                userClubRepository.save(newUserClub);
            }
        }

        response.put("status", "success");
        return response;
    }
}
