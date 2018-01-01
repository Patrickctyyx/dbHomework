package hello.controller;

import com.alibaba.fastjson.JSONObject;
import hello.entity.UserEntity;
import hello.service.UserRepository;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class WeChatController {
    @Autowired
    private UserRepository userRepository;

    @Value("${cty.appid}")
    private String appid;
    @Value("${cty.appSecret}")
    private String appSecret;

    @PostMapping("/wxlogin")
    private Map<String, Object> wxLogin(@RequestBody JSONObject wxJSON) {

        Map<String, Object> errorResponse = new LinkedHashMap<String, Object>();
        errorResponse.put("status", "error");
        JSONObject jsonResult;

        String url = "https://api.weixin.qq.com/sns/jscode2session";
        url += "?appid=" + appid + "&secret=" + appSecret +
                "&js_code=" + wxJSON.getString("js_code") +
                "&grant_type=" + "uthorization_code";

        try {
            // 建立 HTTP 客户端
            CloseableHttpClient client = HttpClients.createDefault();
            // 建立 Get 请求
            HttpGet getRequest = new HttpGet(url);
            // 发送 Get 请求并且保存返回结果
            HttpResponse response = client.execute(getRequest);

            // SC_OK 即为 200
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                // 解析返回结果到字符串
                String strResult = EntityUtils.toString(response.getEntity());
                // 字符串再解析为 JSON
                jsonResult = JSONObject.parseObject(strResult);
                if (jsonResult.getString("errmsg") != null) {
                    errorResponse.put("message", jsonResult.getString("errmsg"));
                    return errorResponse;
                }
            }
            else {
                errorResponse.put("message", "http error!");
                return errorResponse;
            }
        } catch (IOException e) {
            errorResponse.put("message", "e");
            return errorResponse;
        }

        UserEntity user = userRepository.findFirstByWxID(jsonResult.getString("openid"));
        if (user == null) {
            user = new UserEntity();
            user.setWxID(jsonResult.getString("openid"));
            userRepository.save(user);
        }
        String token = user.generateAuthToken(jsonResult.getString("openid"));
        Map<String, Object> response = new LinkedHashMap<String, Object>();
        response.put("status", "success");
        response.put("token", token);
        return response;
    }

    @PostMapping("/bind_account")
    private Map<String, Object> bindAccount (@RequestBody JSONObject infoJSON) {
        Map<String, Object> response = new LinkedHashMap<String, Object>();

        String token = infoJSON.getString("token");
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

        UserEntity curUser = userRepository.findFirstByWxID(wxID);

        UserEntity user;
        if (infoJSON.getString("phone") != null) {
            user = userRepository.findFirstByPhone(infoJSON.getString("phone"));
            if (user == null) {
                response.put("status", "error");
                response.put("message", "invalid phone!");
                return response;
            }
        }
        else if (infoJSON.getString("email") != null) {
            user = userRepository.findFirstByEmail(infoJSON.getString("email"));
            if (user == null) {
                response.put("status", "error");
                response.put("message", "invalid email!");
                return response;
            }
        }
        else {
            response.put("status", "error");
            response.put("message", "lacking information!");
            return response;
        }
        if (user.getWxID().equals(curUser.getWxID())) {
            response.put("status", "error");
            response.put("message", "account have already been bound!");
            return response;
        }

        curUser.setWxID(wxID);
        userRepository.delete(curUser);
        userRepository.save(user);

        response.put("status", "success");
        return response;
    }
}
