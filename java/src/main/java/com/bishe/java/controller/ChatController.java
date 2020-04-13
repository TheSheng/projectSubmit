package com.bishe.java.controller;

import com.alibaba.fastjson.JSON;
import com.bishe.java.mapper.NoticeMapper;
import com.bishe.java.mapper.UserMapper;
import com.bishe.java.pojo.Notice;
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
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @ClassName： ChatController
 * @description:
 * @author: lisheng
 * @create: 2020-04-07 15:14
 **/
@RestController
@RequestMapping("/chat")
@CrossOrigin(value = "*",maxAge = 3600)
public class ChatController {
    @Autowired
    StringRedisTemplate redisTemplate;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @Autowired
    UserMapper userMapper;
    @Autowired
    NoticeMapper noticeMapper;
  @PostMapping("/getLeft")
  public ResponseEntity getLeft(@RequestBody String user){
      try {
          Set<String> messages = redisTemplate.opsForZSet().reverseRange(user + "chat", 0, 6);
          return ResponseOk.create(messages);
      }catch (Exception e){
          e.printStackTrace();
          return ResponseError.create(e.getMessage());
      }
  }
  @GetMapping("/deleteNotice")
  public  ResponseEntity deleteNotice(@RequestParam("noticeId") Integer noticeId){
      try {
          int i = noticeMapper.deleteById(noticeId);
          return ResponseOk.create(noticeId);
      }catch (Exception e){
          return ResponseError.create(e.getMessage());
      }
  }
    @PostMapping("/updateNotice")
    public  ResponseEntity updateNotice(@RequestBody Notice notice){
        try {
            String format = sdf.format(new Date());
            notice.setTime(format);
            noticeMapper.updateById(notice);
            return ResponseOk.create("修改成功");
        }catch (Exception e){
            return ResponseError.create(e.getMessage());
        }
    }
    @PostMapping("/chatWith")
    public ResponseEntity chatWith(@RequestBody Map map){
        String from=MapUtils.getString(map,"from");
        String to=MapUtils.getString(map,"to");
        Object o = redisTemplate.opsForHash().get(from + "last", to);
        if(o==null){
            User user=new User();
            user.setUsername(to);
            User byPhone = userMapper.getByPhone(user);
            return ResponseOk.create(byPhone);
        }
        return ResponseOk.create(o);
    }

  class LastMessage{
      private String message;
      private String time;
      private  String img;
      private String to;

      public  LastMessage(String message,String time,String img,String to){
          this.message=message;
          this.time=time;
          this.img=img;
          this.to=to;
      }
      public String getMessage() {
          return message;
      }

      public String getTo() {
          return to;
      }

      public void setTo(String to) {
          this.to = to;
      }

      public void setMessage(String message) {
          this.message = message;
      }

      public String getTime() {
          return time;
      }

      public void setTime(String time) {
          this.time = time;
      }

      public String getImg() {
          return img;
      }

      public void setImg(String img) {
          this.img = img;
      }
  }
    @PostMapping("/chat")
    public void chat(@RequestBody Map map) {

        String from = MapUtils.getString(map, "From");
        String to = MapUtils.getString(map, "To");
        String message = MapUtils.getString(map, "message");
        String total = MapUtils.getString(map, "total");
        String time = MapUtils.getString(map,"time");
        String img = MapUtils.getString(map,"img");
        redisTemplate.opsForZSet().add(from+"to"+to,message,Double.valueOf(time));
        message= message.replace("bubble me", "bubble you");
        redisTemplate.opsForZSet().add(to+"to"+from,message,Double.valueOf(time));
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("HH:MM:SS");
        time=simpleDateFormat.format(new Date(Long.valueOf(time)));
        redisTemplate.opsForHash().put(from+"last",to, JSON.toJSONString(new LastMessage(total,time,img,to)));
        redisTemplate.opsForHash().put(to+"last",from,JSON.toJSONString(new LastMessage(total,time,img,to)));
    }
    @PostMapping("/sendMsg")
    public boolean sendMsg(String msg){
        System.out.println("全体广播消息 ["+msg+"]");
        TextMessage textMessage = new TextMessage(msg);
        try{
            WebSocketPushHandler.sendMessagesToUsers(textMessage);
        }catch (Exception e){
            return false;
        }
        return true;
    }
    @PostMapping("/createNotice")
    public ResponseEntity create(@RequestBody Notice notice){
      try {
          String format = sdf.format(new Date());
          notice.setTime(format);
          noticeMapper.insert(notice);
          sendMsg("您有一条新的公告");
          return ResponseOk.create("发布公告成功");
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
    @PostMapping("/getNotice")
    public ResponseEntity getByPage(@RequestBody Map map){
      try {
          PageHelper.startPage(MapUtils.getInteger(map,"pageNum"),MapUtils.getInteger(map,"pageSize"));
          List<Notice> notices = noticeMapper.selectByPage(map);
          PageInfo<Notice> noticePageInfo = new PageInfo<>(notices);
          notices.forEach(x->{
              x.setTime(CalculateTime(x.getTime()));
          });
          noticePageInfo.setList(notices);
          return ResponseOk.create(new PageInfo<>(notices));
      }catch (Exception e){
          e.printStackTrace();
          return ResponseError.create(e.getMessage());
      }

    }
    @PostMapping("/sendMsgByUser")
    public boolean sendMsgByUser(@RequestBody Map map){
        String name = MapUtils.getString(map, "name");
        String message = MapUtils.getString(map, "message");
        String to=MapUtils.getString(map,"to");
        String img=MapUtils.getString(map,"imgurl");
        TextMessage textMessage = new TextMessage(name+"给您发送了一条简讯:"+message);
        try{
            boolean b = WebSocketPushHandler.sendMessageToUser(to, textMessage);
            redisTemplate.opsForZSet().add(to+"chat",JSON.toJSONString(map),System.currentTimeMillis());
            return b;

        }catch (Exception e){
            return false;
        }
    }

}
