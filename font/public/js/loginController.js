var myApp = angular.module('login', ['ngCookies'])

myApp.controller('loginController', ['$scope', '$http', '$templateCache', '$cookies', '$cookieStore', '$compile', function ($scope, $http, $templateCache, $cookies, $cookieStore, $compile) {
    var vm = $scope
    var cookie = $cookieStore
    vm.isStudent=false
    vm.check=false
    vm.teacher=null
    vm.remeberMe=false
    var lastUser=cookie.get("lastUser")
    if(lastUser!=null){
        vm.loginUsername=parseInt(lastUser.username)
        vm.loginPassword=lastUser.password
        let url=decodeURI(lastUser.imgurl)
        $("#loginImg").attr("src",url)
    }
    vm.isImg=function () {
        var path = $("#imgUrl").val();
        if (path.length == 0) {
            swal("错误","请上传正确的图片","error")
            return false;
        } else {
            var extStart = path.lastIndexOf('.'),
                ext = path.substring(extStart, path.length).toUpperCase();
            if (ext !== '.PNG' && ext !== '.JPG' && ext !== '.JPEG' && ext !== '.GIF') {
                swal("错误","请检查是否为图片","error")
                return false;
            }
        }
    }
    function  checkImg() {
        var path = $("#imgUrl").val();
        if (path.length == 0) {
            swal("错误","请上传正确的图片","error")
            return false;
        } else {
            var extStart = path.lastIndexOf('.'),
                ext = path.substring(extStart, path.length).toUpperCase();
            if (ext !== '.PNG' && ext !== '.JPG' && ext !== '.JPEG' && ext !== '.GIF') {
                swal("错误","请检查是否为图片","error")
                return false;
            }
        }
        return  true

    }
    vm.canReg=true
    $(document).on('change', '#imgUrl', function () { //PictureUrl为input file 的id
        if(!checkImg())return;
        //console.log(this.files[0]);
        function getObjectURL(file) {
            var url = null;
            if (window.createObjcectURL != undefined) {
                url = window.createOjcectURL(file);
            } else if (window.URL != undefined) {
                url = window.URL.createObjectURL(file);
            } else if (window.webkitURL != undefined) {
                url = window.webkitURL.createObjectURL(file);
            }
            return url;
        }
        var objURL = getObjectURL(this.files[0]);//这里的objURL就是input file的真实路径
        vm.imgFile=this.files[0];
        $('#regImg').attr("src",objURL)
    });
    vm.majorChange=function () {
           $http({
            method: 'GET',
            url: 'http://139.9.160.22:8080/user/selectTeacher?major='+vm.major,
        }).then(function (response) {
            vm.majorTeacher=response.data.data;
            if(vm.majorTeacher.length==0){
                swal(vm.major+"尚未有注册教师","","error")
            }

           }, function (response) {

        });
    }
    vm.login=function () {
        $http({
            method: 'POST',
            url: 'http://139.9.160.22:8080/user/login',
            data:{
                username: vm.loginUsername,
                password:vm.loginPassword
            }
        }).then(function (response) {
            let rs=response.data;
            console.log(rs)
            if(rs.code!=200){
                swal("登录失败",rs.message,"error")
                return;
            }
            cookie.put("user",rs.data)
            if(vm.remeberMe){
                cookie.put("lastUser",rs.data)
            }
            window.location.href="/"

        }, function (response) {

        });
    }
    function check(){
        　　 var reg = new RegExp("^[a-z0-9]+([._\\-]*[a-z0-9])*@([a-z0-9]+[-a-z0-9]*[a-z0-9]+.){1,63}[a-z0-9]+$"); //正则表达式
              if(vm.email==null||vm.email == ""){ //输入不能为空
                swal("邮箱不能为空","","error");
            　　　return false;
            　}else if(!reg.test(vm.email)){ //正则验证不通过，格式不对
                  swal("邮箱格式不正确!","","error");
            　　　return false;
            　}else{
            　　　return true;
            　}
        }

    vm.register=function () {
        vm.canReg=false
        if(!checkImg()){
            vm.canReg=true
            return;
        }
        if(vm.username==null||vm.username==""){
            swal("学号或工号不能为空","","error");
            vm.canReg=true
            return;
        }
        if(vm.username.toString().length!=10){
            swal("中北的学号是10位的","","error");
            vm.canReg=true
            return;
        }
        if(vm.name==null||vm.name==""){
            swal("真实姓名不能为空","","error");
            vm.canReg=true
            return;
        }
        if(!check()){
            vm.canReg=true
            return;
        }
        if(vm.password==null||vm.password==""){
            swal("密码不能为空","","error");
            vm.canReg=true
            return;
        }
        if(vm.password==null||vm.password==""){
            swal("密码不能为空","","error");
            vm.canReg=true
            return;
        }
        if(vm.confirmpassword==null||vm.confirmpassword==""){
            swal("请再次确认密码","","error");
            vm.canReg=true
            return;
        }
        if(vm.password!=vm.confirmpassword){
            swal("两次输入的密码不一致","","error");
            vm.canReg=true
            return;
        }
        if(vm.major==null||vm.major==""){
            swal("请选择学院","","error");
            vm.canReg=true
            return;
        }
        var type=vm.isStudent?"学生":"指导老师";
        if(vm.isStudent){
            if(vm.teacher==null||vm.teacher==""){
                swal("学生必须选择指导老师","","error");
                vm.canReg=true
                return;
            }
        }else {
            vm.teacher=null;
        }
        if(!vm.check){
            swal("请勾选承诺保证","","error")
            vm.canReg=true
            return;
        }
        var data={
            username:vm.username,
            name:vm.name,
            password:vm.password,
            major:vm.major,
            type:type,
            teacher:vm.teacher,
            email:vm.email,
        }
        var formData=new FormData();
        formData.append("img",vm.imgFile)
        formData.append("username",vm.username)
        swal("正在上传图片到云服务器，请稍微等待");
        $.ajax({
            type: 'POST',
            url: 'http://139.9.160.22:8080/user/uploadImg',
            dataType:'json',
            processData: false, //因为data值是FormData对象，不需要对数据做处理。
            contentType: false,
            data:formData,
            success:function(response) {
                console.log(response)
                if (response.code !== 200) {
                    swal("上传图片失败", response.message, "error");
                    vm.canReg=true
                    return;
                }
                data['imgurl'] = response.data
                $http({
                    method: 'POST',
                    url: 'http://139.9.160.22:8080/user/register',
                    data: data
                }).then(function (response) {
                    console.log(response.data)
                    if (response.data.code !== 200) {
                        swal("注册失败", response.data.message, "error");
                        vm.canReg=true
                        return;
                    }
                    swal("注册成功", "", "success")
                    window.location.href = "/login"
                }, function (response) {
                    vm.canReg=true
                });
            },
        error:function (xhr, textStatus, errorThrow) {
            vm.canReg=true
        }}  );
    }

}]);


