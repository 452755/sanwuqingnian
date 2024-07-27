package com.emms.util;

public class HttpErrorCode {

    public static String Message(int errorCode){
        String mes = "";
        switch (errorCode){
            case 401:
                mes =  "未登录";
                break;
            case 402:
                mes = "控制器方法未注册";
            break;
            case 403:
                mes =  "无Cookie未登录";
            break;
            case 404:
                mes = "解密令牌无效";
            break;case 405:
                mes = "Session连接不存在";
            break;
            case 408:
                mes = "请求超时";
                break;
                default:
                    mes = "";
                    break;
        }
        return mes;
    }

}
