var myApp = angular.module('index', ['ngCookies'])

myApp.controller('indexController', ['$scope', '$http', '$templateCache', '$cookies', '$cookieStore', '$compile', function ($scope, $http, $templateCache, $cookies, $cookieStore, $compile) {
    var vm = $scope
    var cookie = $cookieStore
    vm.user=cookie.get("user")
    vm.updateObj={}
    vm.getLeftOrRiightClass=function(index){

        if((index+1)%2==0){
            return "timeline-inverted";
        }
        return "";
    }
    vm.getMessage=function(id){
        $http({
            method: 'GET',
            url: 'http://139.9.160.22:8080/project/getMessage?id='+id,

        }).then(function (response) {
            let rs = response.data;
            if (rs.code != 200) {
                swal("查找流程信息失败", rs.message, "error")
                return;
            }
            vm.messageList=rs.data
            console.log(vm.messageList)
        },function (response) {
        });
    }
    vm.update=function (x) {
        vm.updateObj= JSON.parse(JSON.stringify(x));
    }
    vm.deleteProject=function(obj){
        if(obj.status!=="已退回"){
            swal("只可以删除已退回的项目","","error")
            return;
        }
        $http({
            method: 'GET',
            url: 'http://139.9.160.22:8080/project/delete?id='+obj.id,

        }).then(function (response) {
            let rs = response.data;
            if (rs.code != 200) {
                swal("删除项目失败", rs.message, "error")
                return;
            }
            swal("删除项目成功","", "success")
            vm.getByPage(1)
        },function (response) {
        });
    }
    vm.updateProject=function () {
        if(vm.updateObj.status!=="已退回"){
            swal("只可以修改已退回的项目","","error")
            return;
        }
        if(null==vm.updateObj.projectName||""==vm.updateObj.projectName){
            swal("项目名称必须填写","", "error")
            return
        }
        if(vm.updateObj.projectName.length>20||vm.updateObj.projectName.length<5){
            swal("项目名称长度控制在5～20之内","", "error")
            return
        }
        if(null==vm.updateObj.majorTeacher||""==vm.updateObj.majorTeacher){
            swal("请选择复审教师","", "error")
            return
        }
        if(null==vm.updateObj.schoolTeacher||""==vm.updateObj.schoolTeacher){
            swal("请选择终审教师","", "error")
            return
        }
        if(null==vm.updateObj.message||""==vm.updateObj.message){
            swal("项目描述必须填写","", "error")
            return
        }
        if(vm.updateObj.message.length>100||vm.updateObj.message.length<5){
            swal("项目名称长度控制在5～100之内,这是教师评审的重要依据","", "error")
            return
        }
        vm.updateObj.projectTime=null
        vm.updateObj.status="修改后提交"
        vm.updateObj.nextPeople=vm.user.teacher;
        vm.updateObj.imgurl=vm.user.imgurl;
        $http({
            method: 'POST',
            url: 'http://139.9.160.22:8080/project/update',
            data:vm.updateObj
        }).then(function (response) {
            let rs = response.data;
            if (rs.code != 200) {
                swal("修改项目失败", rs.message, "error")
                return;
            }
            swal("修改项目成功","", "success")
            vm.project={}
            $("#closeCreateButton2").click()
            vm.getByPage(1)

        },function (response) {
        });
    }
    if(vm.user==null){
        swal("请先登录","","error")
        window.location.href="/login"
    }
    if(vm.user.type!=="学生"){
        swal("只有学生可以进行项目的创建","","error")
        setTimeout(function () {
            window.location.href="/"
        },1000)
    }
    $http({
        method: 'POST',
        url: 'http://139.9.160.22:8080/user/getByPhone',
        data:{
            username:vm.user.teacher
        }
    }).then(function (response) {
        let rs = response.data;
        if (rs.code != 200) {
            swal("查询指导老师失败", rs.message, "error")
            return;
        }
        rs=rs.data
        vm.teacherName=rs.name

    },function (response) {
    });
    vm.PageNum=1;
    vm.PageSize=8;
    vm.TotalPage=1;
    vm.getByPage=function(page){
        if(vm.TotalPage==0)vm.TotalPage=1
        if(page>vm.TotalPage){
            swal("已经是最后一页了","","error")
            return
        }
        if(page<1){
            swal("已经是第一页了","","error")
            return
        }
        $http({
            method: 'POST',
            url: 'http://139.9.160.22:8080/project/get',
            data:{
                pageNum:page,
                pageSize:vm.PageSize,
                if:vm.pageIf,
                user:vm.user.username
            }
        }).then(function (response) {
            let rs=response.data;
            if(rs.code!=200){
                swal("查询项目失败",rs.message,"error")
                return;
            }
            rs=rs.data
            vm.projectList=rs.list
            console.log(vm.projectList)
            vm.PageNum=rs.pageNum
            vm.TotalPage=rs.totalPage
        }, function (response) {
        });
    }
    vm.getByPage(1)
    $http({
        method: 'GET',
        url: 'http://139.9.160.22:8080/user/getMajorAndSchoolTeacher?major='+vm.user.major,
    }).then(function (response) {
        let rs = response.data;
        if (rs.code != 200) {
            swal("查询失败", rs.message, "error")
            return;
        }
        rs=rs.data
        vm.majorList=rs.major;
        vm.schoolList=rs.school;
        if(vm.majorList.length==0){
            swal("您的学院尚未有学院老师注册，暂不能使用相关功能","", "error")
            setTimeout(function () {
                window.location.href="/"
            },1000)
        }
        if(vm.schoolList.length==0){
            swal("您的学校尚未有学校老师注册，暂不能使用相关功能","", "error")
            setTimeout(function () {
                window.location.href="/"
            },1000)
        }
    },function (response) {
    });
    $http({
        method: 'GET',
        url: 'http://139.9.160.22:8080/user/selectAllNameAndUserName',
    }).then(function (response) {
        let rs = response.data;
        if (rs.code != 200) {
            swal("查询失败", rs.message, "error")
            return;
        }
        rs=rs.data
        vm.name=rs;

    },function (response) {
    });
    vm.getName=function(username){
        let rs=""
        vm.name.forEach(x=>{
            if(x.username==username){
                rs=x.name
            }
        })
        if(rs=="")return username
        return rs;
    }

    vm.getMajorTeacherName=function(username){
        let rs=""
        vm.majorList.forEach(x=>{
            if(x.username==username){
                rs=x.name
            }
        })
        if(rs=="")return username
        return rs;
    }
    vm.getSchoolTeacherName=function(username){
        let rs=""
        vm.schoolList.forEach(x=>{
            if(x.username==username){
                rs=x.name
            }
        })
        return rs;
    }
    vm.project={}
   vm.createProject=function(){
       if(null==vm.project.projectName||""==vm.project.projectName){
           swal("项目名称必须填写","", "error")
           return
       }
       if(vm.project.projectName.length>20||vm.project.projectName.length<5){
           swal("项目名称长度控制在5～20之内","", "error")
           return
       }
       if(null==vm.project.majorTeacher||""==vm.project.majorTeacher){
           swal("请选择复审教师","", "error")
           return
       }
       if(null==vm.project.schoolTeacher||""==vm.project.schoolTeacher){
           swal("请选择终审教师","", "error")
           return
       }
       if(null==vm.project.message||""==vm.project.message){
           swal("项目描述必须填写","", "error")
           return
       }
       if(vm.project.message.length>100||vm.project.message.length<5){
           swal("项目名称长度控制在5～100之内,这是教师评审的重要依据","", "error")
           return
       }
       vm.project.projectTeacher=vm.user.teacher
       vm.project.status="新提交"
       vm.project.projectUser=vm.user.username
       vm.project.nextPeople=vm.user.teacher
       vm.project.imgurl=vm.user.imgurl
       $http({
           method: 'POST',
           url: 'http://139.9.160.22:8080/project/create',
           data:vm.project
       }).then(function (response) {
           let rs = response.data;
           if (rs.code != 200) {
               swal("创建项目失败", rs.message, "error")
               return;
           }
           vm.project={}
           $("#closeCreateButton").click()
           vm.getByPage(1)

       },function (response) {
       });

   }
    let websocket = null;
    //判断当前浏览器是否支持WebSocket
    if ('WebSocket' in window) {
        websocket = new WebSocket("ws://139.9.160.22:8080/webSocketServer?username="+vm.user.username);
    }
    else {
        swal('错误','当前浏览器不支持websocket',"error")
    }
    //连接发生错误的回调方法
    websocket.onerror = function () {
        swal('错误','连接服务端失败',"error")
    };
    //
    //连接成功建立的回调方法
    websocket.onopen = function () {
        console.log("连接到服务器")
    };

    //接收到消息的回调方法
    websocket.onmessage = function (event) {
        console.log(event)
        $.notify({
            icon: 'flaticon-alarm-1',
            title: '请注意',
            message: event['data'],
        },{
            type: 'info',
            placement: {
                from: "top",
                align: "center"
            },
            time: 10000,
        });
    };

    //连接关闭的回调方法
    websocket.onclose = function () {
    };

    //监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常。
    window.onbeforeunload = function () {
        closeWebSocket();
    };
    //关闭WebSocket连接
    function closeWebSocket() {
        websocket.close();
    }
    $.notify({
        img:vm.user.imgurl,
        icon: 'flaticon-alarm-1',
        title: '欢迎您！'+vm.user.name,
        message: '又是美好的一天',
    },{
        type: 'info',
        placement: {
            from: "bottom",
            align: "right"
        },
        time: 1000,
    });
    $(".touImg").attr("src",vm.user.imgurl)


}]);


