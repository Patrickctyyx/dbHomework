package hello.controller;

import hello.entity.ApplicationEntity;
import hello.entity.ClubEntity;
import hello.entity.UserEntity;
import hello.service.ApplicationRepository;
import hello.service.ClubRepository;
import hello.service.UserRepository;
import hello.utils.Greeting;
import hello.utils.RandomValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
public class TestController {
    @Autowired
    private ApplicationRepository applicationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ClubRepository clubRepository;

//    @RequestMapping("/testA")
//    public Greeting testA() {
//        ApplicationEntity apply = applicationRepository.findFirstByPhone("15521164491");
//        List<ApplicationEntity> name = applicationRepository.findByName("cty");
//        System.out.println(apply.getId());
//        System.out.println(name.get(0).getEmail());
//        System.out.println(name.size());
//        return new Greeting(1, "hello");
//    }

    @GetMapping("/init")
    public Greeting initData() {

        for (int i = 0; i < 20; i++) {
            Map<String, Object> valueMap = RandomValue.getAddress();
            UserEntity user = new UserEntity();
            user.setName(valueMap.get("name").toString());
            if (i % 7 == 0) {
                user.setGrade("大二");
                user.setUserIdentity("minister");
            }
            else {
                user.setGrade("大一");
            }
            user.setCollege(valueMap.get("college").toString());
            user.setMajor(valueMap.get("major").toString());
            user.setDepartment(valueMap.get("department").toString());
            user.setPhone(valueMap.get("tel").toString());
            user.setQq(valueMap.get("tel").toString());
            user.setWechat(valueMap.get("tel").toString());
            user.setWxID(valueMap.get("tel").toString());
            user.setEmail(valueMap.get("email").toString());
            user.setIntroduction("我是来自" + valueMap.get("college").toString() + valueMap.get("major").toString() + "的" + valueMap.get("name").toString() + "。");
            userRepository.save(user);
        }

        ClubEntity club1 = new ClubEntity();
        club1.setName("网络技术研讨会");
        club1.setIntroduction("一个世界中有你，一个世界中没有你，让两者的DIFFERENCE最大，这就是你一生的意义。");
        club1.setType("学术类");
        club1.setImageUrl("https://okdkbnczs.qnssl.com/image/club/jnujmm.png");
        clubRepository.save(club1);

        ClubEntity club2 = new ClubEntity();
        club2.setName("猎狐无线电协会");
        club2.setIntroduction("在你欣赏着光立方的美妙绝伦时，你会沉醉在那些美丽的灯光中，你注定不会错过精彩绝伦的插灯比赛，而奖品，是你们想都不敢想的表白神器——摇摇棒。没参加过的小鲜肉是不是后悔了？不要担心，只要你加入猎狐，我们会把摇摇棒的做法传授给你。～(￣▽￣～)(～￣▽￣)～");
        club2.setType("学术类");
        club2.setImageUrl("http://mmbiz.qpic.cn/mmbiz/S3UYT2icXZwhBmtJL1SBUhaUOvWJZIxPKQuzCc4YMtxticH26ZlAmvynDjkcWZPDdZquRlMaQtM5HHibkDhyaCicHA/640?wx_fmt=jpeg&tp=webp&wxfrom=5");
        clubRepository.save(club2);

        ClubEntity club3 = new ClubEntity();
        club3.setName("吉他协会");
        club3.setIntroduction("暨南大学吉他协会成立于1986年，至今已有28年历史，吉协是暨大规模最大、发展最成熟的社团之一，分会遍及暨大珠海校区、石牌校区及南校区，历届协会成员总计以数万人计算，且来自全球包括美国、日本、韩国、新加坡、马来西亚、香港、澳门、台湾等地。");
        club3.setType("兴趣类");
        club3.setImageUrl("http://mmbiz.qpic.cn/mmbiz/HnQ2scyOyl2cevRUdRbpeA95PFkG3vmUibKt0pGAicprBwHqbiaDSyxmqvagF3ialW3icHduaCNEiatJGdLUolicYR8tQ/640?wx_fmt=jpeg&tp=webp&wxfrom=5&wx_lazy=1");
        clubRepository.save(club3);

        ClubEntity club4 = new ClubEntity();
        club4.setName("网球协会");
        club4.setIntroduction("你热爱网球吗？我们为之痴迷！为之疯狂！如果碰巧你也是，那就加入我们吧！");
        club4.setType("兴趣类");
        club4.setImageUrl("https://okdkbnczs.qnssl.com/image/club/网球.png");
        clubRepository.save(club4);

        ClubEntity club5 = new ClubEntity();
        club5.setName("南枝汉服协会");
        club5.setIntroduction("作为新兴社团，南枝汉服社一直致力于弘扬中华民族的优秀传统文化，积极开展汉服与发簪的制作、盘发教学和传统节日出游等活动，丰富了学生的文化生活。");
        club5.setType("实践类");
        club5.setImageUrl("https://okdkbnczs.qnssl.com/image/club/汉服.png");
        clubRepository.save(club5);

        return new Greeting(1, "created successfully!");
    }
}
