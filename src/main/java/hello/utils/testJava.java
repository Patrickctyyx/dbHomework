package hello.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;


public class testJava {
    public static void main(String[] args) throws Exception {
        JSONObject jsonResult;
        JSONArray jsonArray;


        // 发送 Get 请求
        try {
            CloseableHttpClient client = HttpClients.createDefault();
            HttpGet getRequest = new HttpGet("http://localhost:5000/api/index");
            HttpResponse response = client.execute(getRequest);

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String strResult = EntityUtils.toString(response.getEntity());
                jsonArray = JSONObject.parseArray(strResult);
                System.out.println(jsonArray.getString(0));
            }
        } catch (IOException e) {
        }


        // 发送 Post 请求
        String url = "https://api.weixin.qq.com/sns/jscode2session";
        try {
            // 建立客户端
            CloseableHttpClient client = HttpClients.createDefault();
            // 建立 Post 请求
            HttpPost postRequest = new HttpPost(url);

            // 构建 Post JSON 请求体
            Map<String, Object> param = new LinkedHashMap<String, Object>();
            param.put("appid", "wx68b0a38c1315fb10");
            param.put("secret", "546cc0182063d4f34befd281b3a3efe3");
            param.put("js_code", "021yF9Np0tke6p1qejLp0DC5Np0yF9Nc");
            param.put("grant_type", "uthorization_code");
            // 把 Map 转换为 JSON 字符串
            String JSONString = JSON.toJSONString(param);
            // 设置实体编码，防止中文乱码
            StringEntity entity = new StringEntity(JSONString, "utf-8");
            // 设置 HTTP 中的编码
            entity.setContentEncoding("UTF-8");
            entity.setContentType("application/json");
            // 把实体包含到 Post 请求中
            postRequest.setEntity(entity);
            HttpResponse response = client.execute(postRequest);

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String strResult = EntityUtils.toString(response.getEntity());
                jsonResult = JSONObject.parseObject(strResult);
                System.out.println(jsonResult);
            }
        } catch (IOException e) {
        }
    }
}
