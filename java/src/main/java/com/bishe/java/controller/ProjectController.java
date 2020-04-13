package com.bishe.java.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bishe.java.mapper.NoticeMapper;
import com.bishe.java.mapper.ProjectMapper;
import com.bishe.java.mapper.UserMapper;
import com.bishe.java.pojo.Notice;
import com.bishe.java.pojo.Project;
import com.bishe.java.pojo.User;
import com.bishe.java.util.PageInfo;
import com.bishe.java.util.ResponseError;
import com.bishe.java.util.ResponseOk;
import com.bishe.java.util.WebSocketPushHandler;
import com.github.pagehelper.PageHelper;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.TextMessage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName： ChatController
 * @description:
 * @author: lisheng
 * @create: 2020-04-07 15:14
 **/
@RestController
@RequestMapping("/project")
@CrossOrigin(value = "*",maxAge = 3600)
public class ProjectController {
    @Autowired
    StringRedisTemplate redisTemplate;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @Autowired
    ProjectMapper projectMapper;

  @PostMapping("/create")
  public ResponseEntity getLeft(@RequestBody Project project){
      try {
          Date date = new Date();
          project.setProjectTime(sdf.format(date));
          project.setProjectUpdate(sdf.format(date));
          projectMapper.insert(project);
          JSONObject jsonObject=new JSONObject();
          jsonObject.put("user",project.getProjectUser());
          jsonObject.put("imgUrl",project.getImgurl());
          jsonObject.put("action","创建了项目");
          jsonObject.put("message",project.getMessage());
          jsonObject.put("time",sdf.format(date));
          redisTemplate.opsForZSet().add("project:"+project.getId(),jsonObject.toJSONString(),date.getTime());
          WebSocketPushHandler.sendMessageToUser(project.getNextPeople().toString(),new TextMessage("您有一个项目进度需要关注"));
          return ResponseOk.create("提交成功");
      }catch (Exception e){
          e.printStackTrace();
          return ResponseError.create(e.getMessage());
      }
  }
  @GetMapping("/delete")
  public  ResponseEntity delete(@RequestParam("id")Integer projectId){
      try {
          projectMapper.deleteById(projectId);
          redisTemplate.delete("project:"+projectId);
          return ResponseOk.create("删除成功");
      }catch (Exception e){
          return ResponseOk.create(e.getMessage());
      }
  }
  @GetMapping("getMessage")
  public ResponseEntity getMessage(@RequestParam("id")Integer projectId){
      try {
          Set<String> strings = redisTemplate.opsForZSet().range("project:" + projectId, 0, -1);
          Set<JSONObject> rs=new HashSet<>();
          strings.forEach(x->{
              rs.add(JSON.parseObject(x));
          });
          Set<JSONObject> newrs = rs.stream().sorted(Comparator.comparing(o -> o.get("time").toString())).collect(Collectors.toCollection(LinkedHashSet::new));
          return ResponseOk.create(newrs);
      }catch (Exception e){
          e.printStackTrace();
          return ResponseError.create(e.getMessage());
      }
  }

    @PostMapping("/update")
    public ResponseEntity update(@RequestBody Project project){
        try {
            Date date = new Date();
            project.setProjectUpdate(sdf.format(date));
            projectMapper.updateById(project);
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("user",project.getProjectUser());
            jsonObject.put("imgUrl",project.getImgurl());
            jsonObject.put("action","修改了项目");
            jsonObject.put("message",project.getMessage());
            jsonObject.put("time",sdf.format(date));
            redisTemplate.opsForZSet().add("project:"+project.getId(),jsonObject.toJSONString(),date.getTime());
            WebSocketPushHandler.sendMessageToUser(project.getNextPeople().toString(),new TextMessage("您有一个项目进度需要关注"));
            return ResponseOk.create("提交成功");
        }catch (Exception e){
            e.printStackTrace();
            return ResponseError.create(e.getMessage());
        }
    }
    @PostMapping("/shenpi")
    public ResponseEntity shenpi(@RequestBody Project project){
        try {
            Date date = new Date();
            Project update=new Project();
            update.setId(project.getId());
            update.setStatus(project.getStatus());
            update.setNextPeople(project.getNextPeople());
            update.setProjectUpdate(sdf.format(date));
            projectMapper.updateById(update);
            JSONObject jsonObject=new JSONObject();
            //审批人的工号
            jsonObject.put("user",project.getProjectUser());
            //审批人的头像
            jsonObject.put("imgUrl",project.getImgurl());
            //用name暂存行为消息
            jsonObject.put("action",project.getProjectName());
            jsonObject.put("message",project.getMessage());
            jsonObject.put("time",sdf.format(date));
            redisTemplate.opsForZSet().add("project:"+project.getId(),jsonObject.toJSONString(),date.getTime());
            WebSocketPushHandler.sendMessageToUser(project.getNextPeople().toString(),new TextMessage("您有一个项目进度需要关注"));
            return ResponseOk.create("提交成功");
        }catch (Exception e){
            e.printStackTrace();
            return ResponseError.create(e.getMessage());
        }
    }
  @PostMapping("/get")
  public ResponseEntity getByPage(@RequestBody Map map){
      try {
          PageHelper.startPage(MapUtils.getInteger(map, "pageNum"), MapUtils.getInteger(map, "pageSize"));
          List<Project> projects = projectMapper.selectByMyPage(map);
          PageInfo<Project> projectPageInfo = new PageInfo<>(projects);
          projects.forEach(x -> {
              x.setProjectUpdate(CalculateTime(x.getProjectUpdate()));
              x.setProjectTime(CalculateTime(x.getProjectTime()));
          });
          projectPageInfo.setList(projects);
          return ResponseOk.create(projectPageInfo);
      }catch (Exception e){
          e.printStackTrace();
          return ResponseError.create(e.getMessage());
      }
  }
    @PostMapping("/getAll")
    public ResponseEntity getAll(@RequestBody Map map){
        try {
            PageHelper.startPage(MapUtils.getInteger(map, "pageNum"), MapUtils.getInteger(map, "pageSize"));
            List<Project> projects = projectMapper.getAll(map);
            PageInfo<Project> projectPageInfo = new PageInfo<>(projects);
            projects.forEach(x -> {
                x.setProjectUpdate(CalculateTime(x.getProjectUpdate()));
                x.setProjectTime(CalculateTime(x.getProjectTime()));
            });
            projectPageInfo.setList(projects);
            return ResponseOk.create(projectPageInfo);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseError.create(e.getMessage());
        }
    }
    @PostMapping("/getChushen")
    public ResponseEntity getByChushenPage(@RequestBody Map map){
        try {
            PageHelper.startPage(MapUtils.getInteger(map, "pageNum"), MapUtils.getInteger(map, "pageSize"));
            List<Project> projects = projectMapper.selectByChushenMyPage(map);
            PageInfo<Project> projectPageInfo = new PageInfo<>(projects);
            projects.forEach(x -> {
                x.setProjectUpdate(CalculateTime(x.getProjectUpdate()));
                x.setProjectTime(CalculateTime(x.getProjectTime()));
            });
            projectPageInfo.setList(projects);
            return ResponseOk.create(projectPageInfo);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseError.create(e.getMessage());
        }
    }
    @PostMapping("/getFushen")
    public ResponseEntity getFushen(@RequestBody Map map){
        try {
            PageHelper.startPage(MapUtils.getInteger(map, "pageNum"), MapUtils.getInteger(map, "pageSize"));
            List<Project> projects = projectMapper.selectByFushenMyPage(map);
            PageInfo<Project> projectPageInfo = new PageInfo<>(projects);
            projects.forEach(x -> {
                x.setProjectUpdate(CalculateTime(x.getProjectUpdate()));
                x.setProjectTime(CalculateTime(x.getProjectTime()));
            });
            projectPageInfo.setList(projects);
            return ResponseOk.create(projectPageInfo);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseError.create(e.getMessage());
        }
    }
    @PostMapping("/getzhongshen")
    public ResponseEntity zhongshen(@RequestBody Map map){
        try {
            PageHelper.startPage(MapUtils.getInteger(map, "pageNum"), MapUtils.getInteger(map, "pageSize"));
            List<Project> projects = projectMapper.selectByZhongshen(map);
            PageInfo<Project> projectPageInfo = new PageInfo<>(projects);
            projects.forEach(x -> {
                x.setProjectUpdate(CalculateTime(x.getProjectUpdate()));
                x.setProjectTime(CalculateTime(x.getProjectTime()));
            });
            projectPageInfo.setList(projects);
            return ResponseOk.create(projectPageInfo);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseError.create(e.getMessage());
        }
    }
    public  String CalculateTime(String time) {
        long nowTime = System.currentTimeMillis(); // 获取当前时间的毫秒数
        String msg = "刚刚";
        Date setTime = null; // 指定时间
        try {
            setTime = sdf.parse(time); // 将字符串转换为指定的时间格式
        } catch (ParseException e) {

            e.printStackTrace();
        }
        long reset = setTime.getTime(); // 获取指定时间的毫秒数
        long dateDiff = nowTime - reset;
        if (dateDiff < 0) {
            msg = "输入的时间不对";
        } else {
            long dateTemp1 = dateDiff / 1000; // 秒
            long dateTemp2 = dateTemp1 / 60; // 分钟
            long dateTemp3 = dateTemp2 / 60; // 小时
            long dateTemp4 = dateTemp3 / 24; // 天数
            long dateTemp5 = dateTemp4 / 30; // 月数
            long dateTemp6 = dateTemp5 / 12; // 年数
            if (dateTemp6 > 0) {
                msg = dateTemp6 + "年前";
            } else if (dateTemp5 > 0) {
                msg = dateTemp5 + "个月前";
            } else if (dateTemp4 > 0) {
                msg = dateTemp4 + "天前";
            } else if (dateTemp3 > 0) {
                msg = dateTemp3 + "小时前";
            } else if (dateTemp2 > 0) {
                msg = dateTemp2 + "分钟前";
            } else if (dateTemp1 > 0) {
                msg = "刚刚";
            }
        }
        return msg;
    }


}
