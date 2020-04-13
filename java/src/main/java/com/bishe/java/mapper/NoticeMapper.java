package com.bishe.java.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bishe.java.pojo.Notice;
import com.bishe.java.pojo.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface NoticeMapper extends BaseMapper<Notice> {
    List<Notice> selectByPage(@Param("map")Map map);
}
