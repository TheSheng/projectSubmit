package com.bishe.java.util;

import org.springframework.web.socket.handler.TextWebSocketHandler;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName： WebSocketPushHandler
 * @description:
 * @author: lisheng
 * @create: 2020-04-07 16:20
 **/
public class WebSocketPushHandler extends TextWebSocketHandler {
    private static final List<WebSocketSession> USER_LIST = new ArrayList<>();

    /**
     * 用户进入系统监听
     */
    public static Integer getCurrentUser(){
        return USER_LIST.size();
    }
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("3.用户进入系统。。。");
        System.out.println("用户信息:" + session.getAttributes());
        Map<String, Object> map = session.getAttributes();
        for (String key : map.keySet()) {
            System.out.println("key:" + key + " and value:" + map.get(key));
        }
        USER_LIST.add(session);
    }

    /**
     * 处理用户请求
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        System.out.println("系统处理xxx用户的请求信息。。。");
        System.out.println("===>>> 当前系统用户数为:"+USER_LIST.size());
        System.out.println("用户所发送消息为："+message.getPayload() );
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("----------------"+ LocalTime.now() +"----------------");
        stringBuilder.append("<h3>当前系统在线用户数为:"+USER_LIST.size()+"</h3><br/><br/>");
        stringBuilder.append("<h4>用户"+session.getAttributes().get("userId")+"：</h4><br/>");
        stringBuilder.append("<h5>"+message.getPayload()+"</h5><br/>");
        TextMessage textMessage = new TextMessage(stringBuilder.toString());
        //向所有用户广播消息
        sendMessagesToUsers(textMessage);
    }

    /**
     * 用户退出后的处理
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        if (session.isOpen()) {
            session.close();
        }
        System.out.println("===>>> 退出系统时用户数为:"+USER_LIST.size());
        USER_LIST.remove(session);
        System.out.println(session.getAttributes().get("userId")+"xxx用户退出系统。。。");
    }

    /**
     * 自定义函数
     * 给所有的在线用户发送消息
     */
    public static  void sendMessagesToUsers(TextMessage message) {
        for (WebSocketSession user : USER_LIST) {
            try {
                // isOpen()在线就发送
                if (user.isOpen()) {
                    user.sendMessage(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println(e.getLocalizedMessage());
            }
        }
    }

    /**
     * 自定义函数
     * 发送消息给指定的在线用户
     */
    public static boolean sendMessageToUser(String userId, TextMessage message) {
        for (WebSocketSession user : USER_LIST) {
            if (user.getAttributes().get("userId").equals(userId)) {
                try {
                    // isOpen()在线就发送
                    if (user.isOpen()) {

                        user.sendMessage(message);
                        return  true;
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println(e.getLocalizedMessage());
                }
            }
        }
        return false;
    }

}
