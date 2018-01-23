package hello.controller;

import com.alibaba.fastjson.JSON;
import hello.entity.UserEntity;
import hello.service.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.transaction.Transactional;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WeChatControllerTest {
    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private UserRepository userRepository;

    private MockMvc mvc;
    private MockHttpSession session;

    @Before
    public void setUpMockMvc() {
        mvc = MockMvcBuilders.webAppContextSetup(wac).build(); // 初始化 MockMvc 对象
        session = new MockHttpSession();
    }

    @Test
    public void wxLoginInvalidCode() throws Exception {
        Map<String, String> jsonMap = new LinkedHashMap<String, String>();
        jsonMap.put("js_code", "061jo1Sz0wEokh1GSKQz0W9ORz0jo1SA");
        String json = JSON.toJSONString(jsonMap);
        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/wxlogin")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(json.getBytes())
                .session(session)
        )
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("error"))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
        String resultString = result.getResponse().getContentAsString();
        assertThat(resultString, containsString("invalid"));
    }

    @Test
    @Transactional  // 不会造成垃圾数据
    public void bindAccountSuccessPhone() throws Exception {
        UserEntity user = new UserEntity();
        user.setWxID("wx_01");
        userRepository.save(user);
        String token = user.generateAuthToken("wx_01");

        UserEntity user2 = new UserEntity();
        user2.setPhone("15511111111");
        userRepository.save(user2);

        Map<String, String> jsonMap = new LinkedHashMap<String, String>();
        jsonMap.put("token", token);
        jsonMap.put("phone", "15511111111");
        String json = JSON.toJSONString(jsonMap);

        mvc.perform(MockMvcRequestBuilders.post("/bind_account")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(json.getBytes())
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("success"))
                .andReturn();
    }

    @Test
    @Transactional  // 不会造成垃圾数据
    public void bindAccountSuccessEmail() throws Exception {
        UserEntity user = new UserEntity();
        user.setWxID("wx_01");
        userRepository.save(user);
        String token = user.generateAuthToken("wx_01");

        UserEntity user2 = new UserEntity();
        user2.setEmail("123@patrick.com");
        userRepository.save(user2);

        Map<String, String> jsonMap = new LinkedHashMap<String, String>();
        jsonMap.put("token", token);
        jsonMap.put("email", "123@patrick.com");
        String json = JSON.toJSONString(jsonMap);

        mvc.perform(MockMvcRequestBuilders.post("/bind_account")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(json.getBytes())
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("success"))
                .andReturn();
    }

    @Test
    @Transactional
    public void bindAccountInvalidToken() throws Exception {

        Map<String, String> jsonMap = new LinkedHashMap<String, String>();
        jsonMap.put("token", "invalid token");
        jsonMap.put("phone", "15511111111");
        String json = JSON.toJSONString(jsonMap);

        mvc.perform(MockMvcRequestBuilders.post("/bind_account")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(json.getBytes())
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("error"))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("invalid token!"))
                .andReturn();
    }

    @Test
    @Transactional
    public void bindAccountLackingToken() throws Exception {

        Map<String, String> jsonMap = new LinkedHashMap<String, String>();
        jsonMap.put("phone", "15511111111");
        String json = JSON.toJSONString(jsonMap);

        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/bind_account")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(json.getBytes())
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("error"))
                .andReturn();
        String resultString = result.getResponse().getContentAsString();
        assertThat(resultString, containsString("lacking"));
    }

    @Test
    @Transactional
    public void bindAccountLackingInformation() throws Exception {
        UserEntity user = new UserEntity();
        user.setWxID("wx_01");
        userRepository.save(user);
        String token = user.generateAuthToken("wx_01");

        UserEntity user2 = new UserEntity();
        user2.setPhone("15511111111");
        userRepository.save(user2);

        Map<String, String> jsonMap = new LinkedHashMap<String, String>();
        jsonMap.put("token", token);
        String json = JSON.toJSONString(jsonMap);

        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/bind_account")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(json.getBytes())
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("error"))
                .andReturn();
        String resultString = result.getResponse().getContentAsString();
        assertThat(resultString, containsString("lacking information"));
    }

    @Test
    @Transactional
    public void bindAccountInvalidPhone() throws Exception {
        UserEntity user = new UserEntity();
        user.setWxID("wx_01");
        userRepository.save(user);
        String token = user.generateAuthToken("wx_01");

        UserEntity user2 = new UserEntity();
        user2.setPhone("15511111111");
        userRepository.save(user2);

        Map<String, String> jsonMap = new LinkedHashMap<String, String>();
        jsonMap.put("token", token);
        jsonMap.put("phone", "123");
        String json = JSON.toJSONString(jsonMap);

        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/bind_account")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(json.getBytes())
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("error"))
                .andReturn();
        String resultString = result.getResponse().getContentAsString();
        assertThat(resultString, containsString("invalid phone"));
    }

    @Test
    @Transactional
    public void bindAccountInvalidEmail() throws Exception {
        UserEntity user = new UserEntity();
        user.setWxID("wx_01");
        userRepository.save(user);
        String token = user.generateAuthToken("wx_01");

        UserEntity user2 = new UserEntity();
        user2.setEmail("123@patrick.com");
        userRepository.save(user2);

        Map<String, String> jsonMap = new LinkedHashMap<String, String>();
        jsonMap.put("token", token);
        jsonMap.put("email", "123");
        String json = JSON.toJSONString(jsonMap);

        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/bind_account")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(json.getBytes())
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("error"))
                .andReturn();
        String resultString = result.getResponse().getContentAsString();
        assertThat(resultString, containsString("invalid email"));
    }

    @Test
    @Transactional
    public void bindAccountAccountHaveBound() throws Exception {
        UserEntity user = new UserEntity();
        user.setWxID("wx_01");
        user.setEmail("123@patrick.com");
        userRepository.save(user);
        String token = user.generateAuthToken("wx_01");

        Map<String, String> jsonMap = new LinkedHashMap<String, String>();
        jsonMap.put("token", token);
        jsonMap.put("email", "123@patrick.com");
        String json = JSON.toJSONString(jsonMap);

        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/bind_account")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(json.getBytes())
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("error"))
                .andReturn();
        String resultString = result.getResponse().getContentAsString();
        assertThat(resultString, containsString("bound"));
    }
}