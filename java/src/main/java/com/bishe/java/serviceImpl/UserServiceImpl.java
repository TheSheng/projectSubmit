package com.bishe.java.serviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bishe.java.pojo.User;
import com.bishe.java.mapper.UserMapper;
import com.bishe.java.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 席思诚123
 * @since 2020-01-13
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    UserMapper userMapper;

    @Override
    public Boolean login(User user){
        return userMapper.login(user)>0?true:false;
    }

    @Override
    public List<Map<String,String>> selectTeacher(String major) {
        return userMapper.selectTeacher(major);
    }
    @Override
    public List<Map<String,String>> selectAll() {
        return userMapper.selectAll();
    }
    @Override
    public List<Map<String,String>> selectMajorTeacher(String major) {
        return userMapper.selectMajorTeacher(major);
    }
    @Override
    public List<Map<String, String>> selectTheTeacher() {
        return   userMapper.selectSchoolTeacher();
    }

    @Override
    public Boolean hasRegister(User user) {
        return userMapper.hasRegister(user)>0?true:false;

    }

    @Override
    public User getByUserNameAndPass(User user) {

        return userMapper.getByUserName(user);
    }

    @Override
    public List<User> getRegissterUser() {
        return userMapper.getRigster();
    }

    @Override
    public User getByPhone(User ser) {
        return userMapper.getByPhone(ser);
    }
}
