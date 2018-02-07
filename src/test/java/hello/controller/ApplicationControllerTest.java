package hello.controller;

import com.alibaba.fastjson.JSON;
import hello.entity.ClubEntity;
import hello.entity.UserClubEntity;
import hello.entity.UserEntity;
import hello.service.ClubRepository;
import hello.service.UserClubRepository;
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
public class ApplicationControllerTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ClubRepository clubRepository;
    @Autowired
    private UserClubRepository userClubRepository;

    private MockMvc mvc;
    private MockHttpSession session;

    @Before
    public void setUpMockMvc() {
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();
        session = new MockHttpSession();
    }

    @Test
    @Transactional
    public void newApplySuccess() throws Exception {
        Map<String, String> jsonMap = new LinkedHashMap<String, String>();
        jsonMap.put("name", "小杰");
        jsonMap.put("grade", "大一");
        jsonMap.put("college", "信息学院");
        jsonMap.put("major", "计科");
        jsonMap.put("club_name", "网络技术研讨会");
        jsonMap.put("department", "技术组");
        jsonMap.put("phone", "18674022222");
        jsonMap.put("email", "gong@qq.com");
        String json = JSON.toJSONString(jsonMap);
        mvc.perform(MockMvcRequestBuilders.post("/new_apply")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(json.getBytes())
                .session(session)
        )
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("success"))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

    @Test
    @Transactional
    public void newApplyLackingInfo() throws Exception {
        Map<String, String> jsonMap = new LinkedHashMap<String, String>();
        jsonMap.put("name", "小杰");
        jsonMap.put("grade", "大一");
        jsonMap.put("college", "信息学院");
        jsonMap.put("major", "计科");
        jsonMap.put("club_name", "网络技术研讨会");
        jsonMap.put("department", "技术组");
        jsonMap.put("phone", "18674022222");
        String json = JSON.toJSONString(jsonMap);
        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/new_apply")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(json.getBytes())
                .session(session)
        )
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("error"))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
        String resultString = result.getResponse().getContentAsString();
        assertThat(resultString, containsString("lacking"));
    }

    @Test
    @Transactional
    public void newApplyClubNotExist() throws Exception {
        Map<String, String> jsonMap = new LinkedHashMap<String, String>();
        jsonMap.put("name", "小杰");
        jsonMap.put("grade", "大一");
        jsonMap.put("college", "信息学院");
        jsonMap.put("major", "计科");
        jsonMap.put("club_name", "网络技术研讨");
        jsonMap.put("department", "技术组");
        jsonMap.put("phone", "18674022222");
        jsonMap.put("email", "gong@qq.com");
        String json = JSON.toJSONString(jsonMap);
        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/new_apply")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(json.getBytes())
                .session(session)
        )
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("error"))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
        String resultString = result.getResponse().getContentAsString();
        assertThat(resultString, containsString("club"));
    }

    @Test
    @Transactional
    public void newApplyInvalidEmail() throws Exception {
        Map<String, String> jsonMap = new LinkedHashMap<String, String>();
        jsonMap.put("name", "小杰");
        jsonMap.put("grade", "大一");
        jsonMap.put("college", "信息学院");
        jsonMap.put("major", "计科");
        jsonMap.put("club_name", "网络技术研讨会");
        jsonMap.put("department", "技术组");
        jsonMap.put("phone", "18674022222");
        jsonMap.put("email", "gong@qq");
        String json = JSON.toJSONString(jsonMap);
        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/new_apply")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(json.getBytes())
                .session(session)
        )
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("error"))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
        String resultString = result.getResponse().getContentAsString();
        assertThat(resultString, containsString("email"));
    }

    @Test
    @Transactional
    public void newApplyInvalidPhone() throws Exception {
        Map<String, String> jsonMap = new LinkedHashMap<String, String>();
        jsonMap.put("name", "小杰");
        jsonMap.put("grade", "大一");
        jsonMap.put("college", "信息学院");
        jsonMap.put("major", "计科");
        jsonMap.put("club_name", "网络技术研讨会");
        jsonMap.put("department", "技术组");
        jsonMap.put("phone", "23474022222");
        jsonMap.put("email", "gong@qq.com");
        String json = JSON.toJSONString(jsonMap);
        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/new_apply")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(json.getBytes())
                .session(session)
        )
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("error"))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
        String resultString = result.getResponse().getContentAsString();
        assertThat(resultString, containsString("phone"));
    }

    @Test
    @Transactional
    public void newApplyHadEntered() throws Exception {
        Map<String, String> jsonMap = new LinkedHashMap<String, String>();
        jsonMap.put("name", "小杰");
        jsonMap.put("grade", "大一");
        jsonMap.put("college", "信息学院");
        jsonMap.put("major", "计科");
        jsonMap.put("club_name", "网络技术研讨会");
        jsonMap.put("department", "技术组");
        jsonMap.put("phone", "18674022222");
        jsonMap.put("email", "e34ow84@263.net");
        String json = JSON.toJSONString(jsonMap);
        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/new_apply")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(json.getBytes())
                .session(session)
        )
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("error"))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
        String resultString = result.getResponse().getContentAsString();
        assertThat(resultString, containsString("information already exists!"));
    }

    @Test
    @Transactional
    public void newApplyHadApplied() throws Exception {
        Map<String, String> jsonMap = new LinkedHashMap<String, String>();
        jsonMap.put("name", "小杰");
        jsonMap.put("grade", "大一");
        jsonMap.put("college", "信息学院");
        jsonMap.put("major", "计科");
        jsonMap.put("club_name", "网络技术研讨会");
        jsonMap.put("department", "技术组");
        jsonMap.put("phone", "13100224112");
        jsonMap.put("email", "gone@263.net");
        String json = JSON.toJSONString(jsonMap);
        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/new_apply")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(json.getBytes())
                .session(session)
        )
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("error"))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
        String resultString = result.getResponse().getContentAsString();
        assertThat(resultString, containsString("information already exists!"));
    }
}