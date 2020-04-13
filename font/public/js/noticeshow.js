var myApp = angular.module('index', ['ngCookies'])

myApp.controller('indexController', ['$scope', '$http', '$templateCache', '$cookies', '$cookieStore', '$compile', function ($scope, $http, $templateCache, $cookies, $cookieStore, $compile) {
    var vm = $scope
    var cookie = $cookieStore
    vm.showCreate=false
    vm.showAllNotice=true
    vm.showUpdateNotice=false
    vm.user=cookie.get("user")

    vm.noticePageNum=1;
    vm.noticePageSize=4;
    vm.noticeTotalPage=1;
    vm.noticeQuery={}
    vm.getNotice=function(page){
        if(vm.noticeTotalPage==0)vm.noticeTotalPage=1
        if(page>vm.noticeTotalPage){
            swal("已经是最后一页了","","error")
            return
        }
        if(page<1){
            swal("已经是第一页了","","error")
            return
        }
        $http({
            method: 'POST',
            url: 'http://139.9.160.22:8080/chat/getNotice',
            data:{
                pageNum:page,
                pageSize:vm.noticePageSize,
                if:vm.noticeQuery.noticeIf
            }
        }).then(function (response) {
            let rs=response.data;
            if(rs.code!=200){
                swal("查询公告失败",rs.message,"error")
                return;
            }
            rs=rs.data
            vm.noticeList=rs.list
            vm.noticePageNum=rs.pageNum
            vm.noticeTotalPage=rs.totalPage
        }, function (response) {

        });
    }
    vm.getNotice(1);
    vm.notice={}
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
    //
    //连接成功建立的回调方法
    websocket.onopen = function () {
        console.log("连接到服务器")
    };

    //接收到消息的回调方法
    websocket.onmessage = function (event) {
        vm.getNotice(1);
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


