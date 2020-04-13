package com.bishe.java.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @since 2020-01-13
 */
@TableName("project")
public class Project implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private String projectName;
    private String projectTime;
    private String projectUser;
    private String projectTeacher;
    private String majorTeacher;
    private String schoolTeacher;
    private String nextPeople;
    @TableField(exist = false)
    private String message;
    @TableField(exist = false)
    private String imgurl;
    //创建 待审核 审核不通过
    private String status;
    private  String projectUpdate;

    public String getMessage() {
        return message;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectTime() {
        return projectTime;
    }

    public void setProjectTime(String projectTime) {
        this.projectTime = projectTime;
    }

    public String getProjectUser() {
        return projectUser;
    }

    public void setProjectUser(String projectUser) {
        this.projectUser = projectUser;
    }

    public String getProjectTeacher() {
        return projectTeacher;
    }

    public void setProjectTeacher(String projectTeacher) {
        this.projectTeacher = projectTeacher;
    }

    public String getMajorTeacher() {
        return majorTeacher;
    }

    public void setMajorTeacher(String majorTeacher) {
        this.majorTeacher = majorTeacher;
    }

    public String getSchoolTeacher() {
        return schoolTeacher;
    }

    public void setSchoolTeacher(String schoolTeacher) {
        this.schoolTeacher = schoolTeacher;
    }

    public String getNextPeople() {
        return nextPeople;
    }

    public void setNextPeople(String nextPeople) {
        this.nextPeople = nextPeople;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProjectUpdate() {
        return projectUpdate;
    }

    public void setProjectUpdate(String projectUpdate) {
        this.projectUpdate = projectUpdate;
    }
}
