package com.emms.util;

import android.content.Context;

public class BuildConfig {

    //打包时修改该值
    //UAT到清单文件中修改JPUSH_APPKEY为5e06ea48dfe4d377295cbff3
    //测试环境到清单文件中修改JPUSH_APPKEY为8f158ccb3769786e8814d044
    //PROD环境到清单文件中修改JPUSH_APPKEY为d77ebacf4c4368c50e5d8081
    //UAT包需要把两个String.xml文件的app_name改为EMMSUAT,PROD包String.xml文件的app_name为EMMS
    //PROD打包release,UAT打包debug,DEVELOPMENT打包preview

    public enum APPEnvironment {
        DEVELOPMENT,
        UAT,
        PROD
    }

    public static APPEnvironment appEnvironment = APPEnvironment.UAT;
    //Production
    //UAT
    //    public static  final String UATServer="http://42.159.202.12/emmswebapi/api/BaseOrganise/GetAllFactoriesAndURL";
    public static final String FactoryListApi = "/api/BaseOrganise/GetAllFactoriesAndURL";
    public static final String TranslateApi = "/api/Language_Translation/GetLanguageMap";

    public static String getBaseUrl(Context context) {
        switch (appEnvironment) {
            case UAT: {
                if (SharedPreferenceManager.getNetwork(context).equals("InnerNetwork")) {
//					return "https://emmsuat.esquel.cn/emms";
                    return "http://getazdevnt009c.chinacloudapiaeast.cloudapp.chinacloudapi.cn/emmswebapi";
//					return "http://10.231.131.88:8080";
                } else {
                    return "http://getazdevnt009c.chinaeast.cloudapp.chinacloudapi.cn/emmswebapi";
//					return "https://emmsuat.esquel.cn/emms";
//
                }
            }
            case PROD: {
                if (SharedPreferenceManager.getNetwork(context).equals("InnerNetwork")) {
//					return "http://emmsin.esquel.cn/emms";
                    LogUtils.e("使用内网地址咯");
//					return "http://10.11.2.15/emms";
                    return "http://emmsin.esquel.cn/emms";
//					return "http://10.231.131.85:49863";
                } else {
                    LogUtils.e("使用外网地址咯");
                    return "http://emmsin.esquel.cn/emms";
//					return "http://10.231.131.85:49863";
                }
            }
            case DEVELOPMENT: {
                if (SharedPreferenceManager.getNetwork(context).equals("InnerNetwork")) {
                    return "http://10.11.2.24/emmswebapi";
                } else {
                    return "http://getazdevnt009c.chinaeast.cloudapp.chinacloudapi.cn/emmswebapi";
                }
            }
            default: {
                if (SharedPreferenceManager.getNetwork(context).equals("InnerNetwork")) {
                    return "http://10.11.2.24/emmswebapi";
                } else {
                    return "http://getazdevnt009c.chinaeast.cloudapp.chinacloudapi.cn/emmswebapi";
                }
            }
        }
    }

    public static String getServerAPIEndPoint(Context context) {
        if (SharedPreferenceManager.getNetwork(context).equals("InnerNetwork")) {
            return SharedPreferenceManager.getInteranetUrl(context);
        } else {
            return SharedPreferenceManager.getExtranetUrl(context);
        }
    }


    public static void NetWorkSetting(Context context) {

    }
}
