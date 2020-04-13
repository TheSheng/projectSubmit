var myApp = angular.module('index', ['ngCookies'])

myApp.controller('indexController', ['$scope', '$http', '$templateCache', '$cookies', '$cookieStore', '$compile','$interval', function ($scope, $http, $templateCache, $cookies, $cookieStore, $compile,$interval) {
    var vm = $scope
    var cookie = $cookieStore

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
    //连接发生错误的回调方法
    websocket.onerror = function () {
        swal('错误','连接服务端失败',"error")
    };
    vm.getContext=function(){
        $http({
            method: 'GET',
            url: 'http://139.9.160.22:8080/user/getContext',

        }).then(function (response) {
            let rs=response.data;
            if(200!==rs.code){
                swal("错误",rs.message,"error")
                return;
            }
            vm.context=rs.data

        }, function (response) {

        });
    }
    vm.getContext()
    $interval(vm.getContext,2000,-1);
    //
    //连接成功建立的回调方法
    websocket.onopen = function () {
        console.log("连接到服务器")
    };

    //接收到消息的回调方法
    websocket.onmessage = function (event) {
        vm.getJianxun()
        vm.getContext()
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
        vm.getJianxun()
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
    vm.comment=function(){
        if(""==vm.wantUser||null==vm.wantUser){
            vm.wantUser=null;
            return;
        }
        $http({
            method: 'POST',
            url: 'http://139.9.160.22:8080/user/getByPhone',
            data:{
                username:vm.wantUser
            }
        }).then(function (response) {
            let rs=response.data;
            if(null==rs.data){
                swal("错误","该用户尚未注册","error")
                return;
            }
            vm.chat(rs.data)
        }, function (response) {

        });

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

    vm.replay=function(obj){
        swal({
            title: '回复简讯',
            html: '<br><input class="form-control" placeholder="Input Something" id="input-field">',
            content: {
                element: "input",
                attributes: {
                    placeholder: "你想对"+obj['name']+'回复什么',
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

    $http({
        method: 'GET',
        url: 'http://139.9.160.22:8080/user/getTeacherSuggest',
    }).then(function (response) {
        let rs=response.data;
        if(rs.code!=200){
            swal("查询失败",rs.message,"error")
            return;
        }
        console.log(rs)
        vm.registerUser=rs.data
    }, function (response) {

    });
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


    $(".touImg").attr("src",vm.user.imgurl)


}]);


