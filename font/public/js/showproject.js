var myApp = angular.module('index', ['ngCookies'])

myApp.controller('indexController', ['$scope', '$http', '$templateCache', '$cookies', '$cookieStore', '$compile', function ($scope, $http, $templateCache, $cookies, $cookieStore, $compile) {
    var vm = $scope
    var cookie = $cookieStore
    vm.name=null
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

    if(vm.user==null){
        swal("请先登录","","error")
        window.location.href="/login"
    }

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
            url: 'http://139.9.160.22:8080/project/getAll',
            data:{
                pageNum:page,
                pageSize:vm.PageSize,
                if:vm.pageIf,
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


