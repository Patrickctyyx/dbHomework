package hello.controller;

import com.alibaba.fastjson.JSONObject;
import hello.entity.ApplicationEntity;
import hello.entity.UserEntity;
import hello.service.ApplicationRepository;
import hello.service.UserRepository;
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

    private Integer isAdmin(JSONObject paramJSON) {
        String token = paramJSON.getString("token");
        if (token == null) {
            return 2;
        }

        String wxID = UserEntity.checkAuthToken(token);
        if (wxID.length() == 0) {
            return 3;
        }

        UserEntity user = userRepository.findFirstByWxID(wxID);
        if (user.getUserIdentity().equals("officer")) {
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

        Long id = reviseJSON.getLong("id");
        String newIdentity = reviseJSON.getString("new_identity");
        if (id == null || newIdentity == null) {
            response.put("status", "error");
            response.put("message", "lacking id or identity!");
            return response;
        }

        UserEntity revisedUser = userRepository.findFirstById(id);
        if (revisedUser == null) {
            response.put("status", "error");
            response.put("message", "user do not exist!");
            return response;
        }
        revisedUser.setUserIdentity(newIdentity);
        userRepository.save(revisedUser);
        response.put("status", "ok");
        return response;
    }
}
