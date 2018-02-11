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
public class UserControllerTest {

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
    @Transactional
    public void editUserInfoSuccess() throws Exception {

        UserEntity user = userRepository.findFirstById(Long.parseLong(String.valueOf(1)));
        String token = user.generateAuthToken(user.getWxID());

        Map<String, String> jsonMap = new LinkedHashMap<String, String>();
        jsonMap.put("token", token);
        jsonMap.put("name", "小杰");
        jsonMap.put("grade", "大一");
        jsonMap.put("college", "信息学院");
        jsonMap.put("major", "计科");
        jsonMap.put("department", "技术组");
        jsonMap.put("phone", "18674020881");
        jsonMap.put("qq", "873948100");
        jsonMap.put("wechat", "873948100");
        jsonMap.put("email", "goneKilua@263.net");
        jsonMap.put("introduction", "My name is Gong");
        String json = JSON.toJSONString(jsonMap);

        mvc.perform(MockMvcRequestBuilders.post("/edit_userinfo")
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
    public void editUserDuplicateEmail() throws Exception {

        UserEntity user = userRepository.findFirstById(Long.parseLong(String.valueOf(2)));
        String token = user.generateAuthToken(user.getWxID());

        Map<String, String> jsonMap = new LinkedHashMap<String, String>();
        jsonMap.put("token", token);
        jsonMap.put("email", "ex1c4d@aol.com");
        String json = JSON.toJSONString(jsonMap);

        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/edit_userinfo")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(json.getBytes())
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("error"))
                .andReturn();

        String resultString = result.getResponse().getContentAsString();
        assertThat(resultString, containsString("duplicate"));
    }

    @Test
    @Transactional
    public void editUserInvalidPhoneFormat() throws Exception {

        UserEntity user = userRepository.findFirstById(Long.parseLong(String.valueOf(2)));
        String token = user.generateAuthToken(user.getWxID());

        Map<String, String> jsonMap = new LinkedHashMap<String, String>();
        jsonMap.put("token", token);
        jsonMap.put("email", "goneKilua@263.net");
        jsonMap.put("phone", "ex1c4d@aol.com");
        String json = JSON.toJSONString(jsonMap);

        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/edit_userinfo")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(json.getBytes())
                .session(session)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("error"))
                .andReturn();

        String resultString = result.getResponse().getContentAsString();
        assertThat(resultString, containsString("invalid"));
    }
}