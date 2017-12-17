package hello;

import java.util.concurrent.atomic.AtomicLong;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

// @RestController注解等价于@Controller+@ResponseBody的结合
// 使用这个注解的类里面的方法都以json格式输出
@RestController
public class GreetingController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    // 直接将 url 映射到函数
    // 并且制定了 HTTP 方法
    @GetMapping("/greeting")
    public Greeting greeting(@RequestParam(value = "name", defaultValue = "world") String name) {
        return new Greeting(counter.incrementAndGet(),
                            String.format(template, name));
    }

    // 分开指定方法与映射
    // 其中 method 不填的话默认支持所有方法
    @RequestMapping(value = "/hello", method = GET)
    public Greeting hello() {
        return new Greeting(counter.incrementAndGet(), "hello");
    }
}
