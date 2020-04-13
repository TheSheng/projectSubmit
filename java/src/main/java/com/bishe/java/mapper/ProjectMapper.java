package com.bishe.java.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bishe.java.pojo.Notice;
import com.bishe.java.pojo.Project;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ProjectMapper extends BaseMapper<Project> {
    List<Project>  selectByMyPage(@Param("map")Map map);
    List<Project>  selectByChushenMyPage(@Param("map")Map map);
    List<Project>  selectByFushenMyPage(@Param("map")Map map);
    List<Project>  selectByZhongshen(@Param("map")Map map);
    List<Project>  getAll(@Param("map")Map map);
    Integer getAllUser();
    Integer getAllProject();
    Integer getAllNotice();

}
