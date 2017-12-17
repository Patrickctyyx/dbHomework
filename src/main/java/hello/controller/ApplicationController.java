package hello.controller;

import com.alibaba.fastjson.JSONObject;
import hello.entity.ApplicationEntity;
import hello.service.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class ApplicationController {
    @Autowired
    private ApplicationRepository applicationRepository;

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
                "department",
                "phone",
                "email",
                "introduction"
        };
        // 检查是不是有除了介绍之外的信息为空
        for (String attr: attrs) {
            if (!attr.equals("introduction") && applyJSON.getString(attr) == null) {
                response.put("status", "error");
                response.put("message", "lacking " + attr + "!");
                return response;
            }
        }
        // todo:对传入进来的参数进行类型检查
        ApplicationEntity apply = new ApplicationEntity(
                applyJSON.getString("name"),
                applyJSON.getString("grade"),
                applyJSON.getString("college"),
                applyJSON.getString("major"),
                applyJSON.getString("department"),
                applyJSON.getString("phone"),
                applyJSON.getString("email"),
                applyJSON.getString("introduction")
        );
        if (applicationRepository.findFirstByPhone(applyJSON.getString("phone")) != null ||
                applicationRepository.findFirstByEmail(applyJSON.getString("Email")) != null) {
            response.put("status", "error");
            response.put("message", "Information already exists!");
            return response;
        }

        applicationRepository.save(apply);
        apply = applicationRepository.findFirstByPhone(applyJSON.getString("phone"));
        System.out.println(apply);
        response.put("status", "successful");
        response.put("message", "Applied successfully!");
        response.put("id", apply.getId());
        return response;
    }
}
