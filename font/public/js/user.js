var myApp = angular.module('index', ['ngCookies'])

myApp.controller('indexController', ['$scope', '$http', '$templateCache', '$cookies', '$cookieStore', '$compile', function ($scope, $http, $templateCache, $cookies, $cookieStore, $compile) {
    var vm = $scope
    var cookie = $cookieStore
    vm.showCreate=false
    vm.showAllNotice=true
    vm.user=cookie.get("user")
    if(vm.user==null){
          swal("请先登录","","error")
         window.location.href="/login"
    }
    let websocket = null;
    //判断当前浏览器是否支持WebSocket
    if ('WebSocket' in window) {
        websocket = new WebSocket("ws://139.9.160.22:8080/webSocketServer?username="+vm.user.username);
    }
    else {
        swal('错误','当前浏览器不支持websocket',"error")
    }
    function  checkImg() {
        var path = $("#noticeImg").val();
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
    $(document).on('change', '#noticeImg', function () { //PictureUrl为input file 的id
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
        $('.newNotice').attr("src",objURL)
    });
    vm.creatProject=function(){
        if(""==vm.notice.newTitle||null==vm.notice.newTitle){
            swal("标题不能为空","","error")
            return
        }
        if(!checkImg())return;
        if(""==vm.notice.noticeNewMessage||null==vm.notice.noticeNewMessage||vm.notice.noticeNewMessage.length>20){
            swal("请检查公告内容","","error")
            return
        }
        var data={
            title:vm.notice.newTitle,
            message:vm.notice.noticeNewMessage,
        }
        var formData=new FormData();
        formData.append("img",vm.imgFile)
        formData.append("username",""+new Date().getTime())
        swal("正在上传图片，受限于ftp服务器配置，请耐心等待，请勿重复提交","","success")
        $.ajax({
            type: 'POST',
            url: 'http://139.9.160.22:8080/user/uploadImg',
            dataType:'json',
            processData: false, //因为data值是FormData对象，不需要对数据做处理。
            contentType: false,
            data:formData,
            success:function(response) {
                data['imgurl']= response.data
                $http({
                    method: 'POST',
                    url: 'http://139.9.160.22:8080/chat/createNotice',
                    data:data
                }).then(function (response) {
                    let rs=response.data;
                    if(rs.code!=200){
                        swal("上传公告失败",rs.message,"error")
                        return;
                    }
                    vm.notice.newTitle=null;
                    vm.notice.noticeNewMessage=null;
                }, function (response) {

                });


            },
            error:function (xhr, textStatus, errorThrow) {
                vm.canReg=true
            }}  );

    }
    function validateImage(pathImg) {
        var ImgObj = new Image();
        ImgObj.src = pathImg;
        if(ImgObj.fileSize > 0 || (ImgObj.width > 0 && ImgObj.height > 0)) {
            return true;
        } else {
            return false;
        }

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

    //发送消息
    vm.send=function () {

        let message = document.getElementById('text').value;
        websocket.send(message);
    }


    vm.chat=function(obj){
        swal({
            title: '发送简讯',
            html: '<br><input class="form-control" placeholder="Input Something" id="input-field">',
            content: {
                element: "input",
                attributes: {
                    placeholder: "你想对"+obj['name']+'说点什么',
                    type: "text",
                    id: "input-field",
                    className: "form-control"
                },
            },
            buttons: {
                cancel: {
                    visible: true,
                    className: 'btn btn-danger'
                },
                confirm: {
                    className : 'btn btn-success'
                }
            },
        }).then(
            function() {
                if(""!=$('#input-field').val()) {
                    vm.user['message']=$('#input-field').val()
                    vm.user['to']=obj['username']
                    $http({
                        method: 'POST',
                        url: 'http://139.9.160.22:8080/chat/sendMsgByUser',
                        data:vm.user
                    }).then(function (response) {
                        let rs=response.data;
                        if(!rs){
                            swal(obj['name']+"暂不在线，上线后可看到您的简讯","","success")
                        }
                    }, function (response) {

                    });
                }
            }
        );
    };


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
    vm.updateUser=function(){
        $http({
            method: 'POST',
            url: 'http://139.9.160.22:8080/user/update',
            data:vm.user
        }).then(function (response) {
            let rs=response.data;
            if(rs.code!=200){
                swal("修改失败",rs.message,"error")
                return;
            }
            swal("修改成功","","success")
            cookie.put("user",vm.user)

        }, function (response) {

        });
    }

    vm.getJianxun=function() {
        $http({
            method: 'POST',
            url: 'http://139.9.160.22:8080/chat/getLeft',
            data: vm.user.username
        }).then(function (response) {
            let rs = response.data;
            if (rs.code != 200) {
                swal("查询失败", rs.message, "error")
                return;
            }
            vm.jianxunList=new Array()
                rs.data.forEach(x=>{
                    vm.jianxunList.push(JSON.parse(x))
                })

        }, function (response) {

        });
    }
    vm.getJianxun()

    vm.resetUser=function(){
        vm.user=cookie.get("user")
    }
    vm.showUser=function(){
        vm.showProfile=true
        vm.showIndex=false
    }
    $(".touImg").attr("src",vm.user.imgurl)


}]);


