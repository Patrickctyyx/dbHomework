package hello.controller;

import hello.entity.ClubEntity;
import hello.service.ClubRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@RestController
public class ClubController {
    @Autowired
    private ClubRepository clubRepository;

    // 社团的创建的话还是直接由 DBA 来进行创建吧

    @GetMapping("/clubs")
    public List<Map<String, Object>> showClubs() {
        List<Map<String, Object>> resultList = new LinkedList<Map<String, Object>>();

        List<ClubEntity> clubs = clubRepository.findAllByOrderByName();
        for (ClubEntity club: clubs) {
            Map<String, Object> clubMap = new LinkedHashMap<String, Object>();
            clubMap.put("id", club.getId());
            clubMap.put("name", club.getName());
            clubMap.put("introduction", club.getIntroduction());
            clubMap.put("type", club.getType());
            clubMap.put("image_url", club.getImageUrl());
            resultList.add(clubMap);
        }
        // todo: 有时间做一下数据分页
        return resultList;
    }

    @GetMapping("/clubs/{id}")
    public Map<String, Object> getClub(@PathVariable Long id) {
        ClubEntity club = clubRepository.findFirstById(id);
        Map<String, Object> clubMap = new LinkedHashMap<String, Object>();

        if (club == null) {
            clubMap.put("status", "error");
            clubMap.put("message", "application not found!");
            return clubMap;
        }

        clubMap.put("id", club.getId());
        clubMap.put("name", club.getName());
        clubMap.put("introduction", club.getIntroduction());
        clubMap.put("type", club.getType());
        clubMap.put("image_url", club.getImageUrl());
        return clubMap;
    }

    // todo: 查看某个社团中的所有活动/通知
}
