package com.bishe.java.controller;


import com.bishe.java.mapper.ProjectMapper;
import com.bishe.java.pojo.User;
import com.bishe.java.service.UserService;
import com.bishe.java.util.ResponseError;
import com.bishe.java.util.ResponseOk;
import com.bishe.java.util.WebSocketPushHandler;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.ibatis.annotations.Param;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Wrapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @since 2020-01-13
 */
@RestController
@RequestMapping("/user")
@CrossOrigin(value = "*",maxAge = 3600)
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    ProjectMapper projectMapper;
    private String path=System.getProperty("user.dir");
    @PostMapping("/uploadImg")
    public ResponseEntity upload(@RequestParam("img") MultipartFile img,@RequestParam("username")String username){
        try {
            String contentType = img.getContentType();
            contentType = contentType.substring(contentType.lastIndexOf("/")+1,contentType.length());
//            FTPClient ftpClient = new FTPClient();
//            // 2. 创建 ftp 连接
//            ftpClient.connect("139.9.160.22", 21);
//            System.out.println(ftpClient.getReplyString());
//            // 3. 登录 ftp 服务器
//            boolean root = ftpClient.login("root", "lsljx521..");
//            System.out.println(ftpClient.getReplyString());
//            if(!root){
//                return ResponseError.create("登录ftp失败");
//            }
//            // 4. 读取本地文件
//            // 5. 设置上传的路径
//            ftpClient.changeWorkingDirectory("/usr/image");
//            // 6. 修改上传文件的格式为二进制
//            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
//            // 7. 服务器存储文件，第一个参数是存储在服务器的文件名，第二个参数是文件流
//            InputStream inputStream = img.getInputStream();
            String fileName = username + "." + contentType;
            img.transferTo(Paths.get("/usr/image/"+fileName));
//            boolean b = ftpClient.storeFile(fileName, inputStream);
//            System.out.println(ftpClient.getReplyString());
//            if(!b){
//                return ResponseError.create("上传错误");
//            }
//            System.out.println(b);
//            // 8. 关闭连接
//            ftpClient.logout();
            return ResponseOk.create("http://139.9.160.22/image/"+fileName);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseError.create(e.getMessage());
        }
    }
    @PostMapping("/register")
    public ResponseEntity register(@RequestBody User user){

        try {
            Boolean hasRegister = userService.hasRegister(user);
            if(hasRegister){
                return  ResponseError.create("注册失败，该学号或工号已被注册");
            }
            userService.save(user);
            return ResponseOk.create(user);
        }catch (Exception e){
            e.printStackTrace();
            return  ResponseError.create("注册失败："+e.getMessage());
        }
    }
    @PostMapping("/update")
    public ResponseEntity update(@RequestBody User user){
        try {
            userService.updateById(user);
            return  ResponseOk.create("修改成功");
        }catch (Exception e){
            return  ResponseError.create("修改失败:"+e.getMessage());
        }
    }
    @GetMapping("/selectTeacher")
    public ResponseEntity selectTeacher(@Param("major")String major){
        try {
            List<Map<String, String>> majorTeachers = userService.selectTeacher(major);
            return ResponseOk.create(majorTeachers);
        }catch (Exception e){
            return  ResponseError.create("查询失败："+e.getMessage());
        }
    }
    @GetMapping("/selectAllNameAndUserName")
    public ResponseEntity selectAllNameAndUserName(){
        try {
            List<Map<String, String>> majorTeachers = userService.selectAll();
            return ResponseOk.create(majorTeachers);
        }catch (Exception e){
            return  ResponseError.create("查询失败："+e.getMessage());
        }
    }
    @GetMapping("/getMajorAndSchoolTeacher")
    public ResponseEntity selectTheTeacher(@Param("major")String major){
        try {
            List<Map<String, String>> majorTeachers = userService.selectMajorTeacher(major);
            List<Map<String, String>> schoolTeachers = userService.selectTheTeacher();
            Map<String,List> map=new HashMap();
            map.put("major",majorTeachers);
            map.put("school",schoolTeachers);
            return ResponseOk.create(map);
        }catch (Exception e){
            return  ResponseError.create("查询失败："+e.getMessage());
        }
    }
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody User user){
        try {
            Boolean login = userService.login(user);
            if(login){
                User byUserNameAndPass = userService.getByUserNameAndPass(user);
                return ResponseOk.create(byUserNameAndPass);
            }
            return  ResponseError.create("登录失败，请检查账号密码");
        }catch (Exception e){
            return  ResponseError.create("登录失败："+e.getMessage());
        }
    }
    @PostMapping("/getByPhone")
    public ResponseEntity getByPhone(@RequestBody User user){
        try {

            return ResponseOk.create(userService.getByPhone(user));
        }catch (Exception e){
            return  ResponseError.create("登录失败："+e.getMessage());
        }
    }
    @GetMapping("/getContext")
    public ResponseEntity getContext(){
        try {
            Map<String,Integer> map=new HashMap<>();
            map.put("curr", WebSocketPushHandler.getCurrentUser());
            map.put("projectNum",projectMapper.getAllProject());
            map.put("userNum",projectMapper.getAllUser());
            map.put("noticeNum",projectMapper.getAllNotice());
            return ResponseOk.create(map);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseError.create(e.getMessage());
        }
    }
    @GetMapping("/getTeacherSuggest")
    public  ResponseEntity getSuggest(){
        try {
            return ResponseOk.create(userService.getRegissterUser());
        }catch (Exception e){
            return  ResponseError.create("查询失败"+e.getMessage());
        }
    }


}

