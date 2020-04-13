package com.bishe.java.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bishe.java.pojo.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface UserMapper extends BaseMapper<User> {
     int login(@Param("user") User user);
     List<Map<String,String>> selectTeacher(@Param("major")String major);
    List<Map<String,String>> selectAll();
    List<Map<String,String>> selectMajorTeacher(@Param("major")String major);
    List<Map<String,String>> selectSchoolTeacher();
    int hasRegister(@Param("user") User user);
     User getByUserName(@Param("user") User user);
     List<User> getRigster();
    User getByPhone(@Param("user") User user);


}
