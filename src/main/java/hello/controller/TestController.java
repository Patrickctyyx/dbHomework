package hello.controller;

import hello.entity.ApplicationEntity;
import hello.service.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Controller
public class TestController {
    @Autowired
    private ApplicationRepository applicationRepository;

    @RequestMapping("/test")
    public String test() {
        ApplicationEntity apply = applicationRepository.findFirstByPhone("15521164491");
        List<ApplicationEntity> name = applicationRepository.findByName("cty");
        System.out.println(apply.getId());
        System.out.println(name.get(0).getEmail());
        System.out.println(name.size());
        return "Hello";
    }
}
