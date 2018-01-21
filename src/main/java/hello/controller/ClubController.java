package hello.controller;

import hello.entity.ActivityEntity;
import hello.entity.ClubEntity;
import hello.service.ActivityPagingRepository;
import hello.service.ActivityRepository;
import hello.service.ClubPagingRepository;
import hello.service.ClubRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@RestController
public class ClubController {
    @Autowired
    private ClubRepository clubRepository;
    @Autowired
    private ActivityRepository activityRepository;
    @Autowired
    private ClubPagingRepository clubPagingRepository;
    @Autowired
    private ActivityPagingRepository activityPagingRepository;

    // 社团的创建的话还是直接由 DBA 来进行创建吧

    @GetMapping("/clubs")
    public List<Map<String, Object>> showClubs(@RequestParam(name = "page", defaultValue = "0") Integer page) {
        List<Map<String, Object>> resultList = new LinkedList<Map<String, Object>>();

        Sort sort = new Sort(Sort.Direction.ASC, "name");
        Pageable pageable = new PageRequest(page, 10, sort);
        List<ClubEntity> clubs = clubPagingRepository.findAll(pageable).getContent();
        for (ClubEntity club: clubs) {
            Map<String, Object> clubMap = new LinkedHashMap<String, Object>();
            clubMap.put("id", club.getId());
            clubMap.put("name", club.getName());
            clubMap.put("introduction", club.getIntroduction());
            clubMap.put("type", club.getType());
            clubMap.put("image_url", club.getImageUrl());
            resultList.add(clubMap);
        }
        return resultList;
    }

    @GetMapping("/clubs/{id}")
    public Map<String, Object> getClub(@PathVariable Long id) {
        ClubEntity club = clubRepository.findFirstById(id);
        Map<String, Object> clubMap = new LinkedHashMap<String, Object>();

        if (club == null) {
            clubMap.put("status", "error");
            clubMap.put("message", "club not found!");
            return clubMap;
        }

        clubMap.put("id", club.getId());
        clubMap.put("name", club.getName());
        clubMap.put("introduction", club.getIntroduction());
        clubMap.put("type", club.getType());
        clubMap.put("image_url", club.getImageUrl());
        return clubMap;
    }

    @GetMapping("/activities/{id}")
    public List<Map<String, Object>> showActivities(@PathVariable Long id, @RequestParam(name ="page", defaultValue = "0") Integer page) {
        List<Map<String, Object>> resultList = new LinkedList<Map<String, Object>>();

        ClubEntity club = clubRepository.findFirstById(id);
        if (club == null) {
            Map<String, Object> response = new LinkedHashMap<String, Object>();
            response.put("status", "error");
            response.put("message", "club does not exist!");
            resultList.add(response);
            return resultList;
        }

        Sort sort = new Sort(Sort.Direction.DESC, "lastModified");
        Pageable pageable = new PageRequest(page, 10, sort);
        List<ActivityEntity> activities = activityPagingRepository.findByClub(club, pageable).getContent();
        for (ActivityEntity activity: activities) {
            Map<String, Object> activityMap = new LinkedHashMap<String, Object>();
            activityMap.put("id", activity.getId());
            activityMap.put("theme", activity.getTheme());
            activityMap.put("content", activity.getContent());
            activityMap.put("start_time", activity.getStart_time());
            activityMap.put("creator_id", activity.getUser().getId());
            activityMap.put("club_id", activity.getClub().getId());
            activityMap.put("last_modified", activity.getLastModified());
            resultList.add(activityMap);
        }
        return resultList;
    }

    @GetMapping("/activities/single/{activity_id}")
    public Map<String, Object> showTheActivity(@PathVariable Long activity_id) {
        Map<String, Object> activityMap = new LinkedHashMap<String, Object>();

        ActivityEntity activity = activityRepository.findFirstById(activity_id);
        if (activity == null) {
            return activityMap;
        }

        activityMap.put("id", activity.getId());
        activityMap.put("theme", activity.getTheme());
        activityMap.put("content", activity.getContent());
        activityMap.put("start_time", activity.getStart_time());
        activityMap.put("creator_id", activity.getUser().getId());
        activityMap.put("club_id", activity.getClub().getId());
        activityMap.put("last_modified", activity.getLastModified());

        return activityMap;
    }
}
