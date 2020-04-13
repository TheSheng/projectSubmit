package com.bishe.java.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

/**
 * @ClassName： ResponseOk
 * @description:
 * @create: 2020-01-13 17:07
 **/
public class ResponseOk {

    /**
     * 创建成功信息返回类
     *
     * @param result 返回的结果
     * @return 包含返回结果的成功信息返回类
     * @author 557092
     * @date 2018年5月21日
     * @version v1.0
     */
    public static ResponseEntity<Object> create(Object result) {
        ResponseResult okResponse = new ResponseResult();
        okResponse.setCode(200);
        okResponse.setMessage("succeed");
        okResponse.setData(result);
        return ResponseEntity.ok(okResponse);
    }

    /**
     * 响应结果(内部类)
     *
     * @author 557092
     * @version v1.0
     * @date 2018年5月21日
     */
    public static class ResponseResult {

        /**
         * 响应码
         */
        @JsonProperty("code")
        private int code;

        /**
         * 响应信息
         */
        @JsonProperty("message")
        private String message;

        /**
         * 响应结果
         */
        @JsonProperty("data")
        private Object data;

        /**
         * 2018年5月21日
         *
         * @return the code
         * @author 557092
         * @version 1.0
         */
        public int getCode() {
            return code;
        }

        /**
         * 2018年5月21日
         *
         * @param code the code to set
         * @author 557092
         * @version 1.0
         */
        public void setCode(int code) {
            this.code = code;
        }

        /**
         * 2018年5月21日
         *
         * @return the message
         * @author 557092
         * @version 1.0
         */
        public String getMessage() {
            return message;
        }

        /**
         * 2018年5月21日
         *
         * @param message the message to set
         * @author 557092
         * @version 1.0
         */
        public void setMessage(String message) {
            this.message = message;
        }

        /**
         * 2018年5月21日
         *
         * @return the data
         * @author 557092
         * @version 1.0
         */
        public Object getData() {
            return data;
        }

        /**
         * 2018年5月21日
         *
         * @param data the data to set
         * @author 557092
         * @version 1.0
         */
        public void setData(Object data) {
            this.data = data;
        }

    }
}
