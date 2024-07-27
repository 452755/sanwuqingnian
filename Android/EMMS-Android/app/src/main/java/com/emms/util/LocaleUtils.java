package com.emms.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.rest.JsonObjectElement;
import com.datastore_android_sdk.rxvolley.RxVolley;
import com.datastore_android_sdk.rxvolley.client.HttpCallback;
import com.datastore_android_sdk.rxvolley.client.HttpParams;
import com.emms.R;
import com.emms.activity.BaseActivity;
import com.emms.fragment.BaseFragment;
import com.emms.httputils.HttpUtils;
import com.google.gson.JsonObject;
import com.tencent.bugly.crashreport.CrashReport;

import org.json.JSONObject;

import static android.content.Context.MODE_PRIVATE;
import static com.emms.util.CrashHandler.TAG;

public class LocaleUtils {

    private static final String KEY_LANGUAGE = "current-lang";
    private static JSONObject i18n;
    public static final String LANGUAGE_SETTING_ACTION = "com.emms.language_setting";
    private static AlertDialog alertDialog;

    public enum SupportedLanguage {
        CHINESE_SIMPLFIED(Locale.CHINESE.toString()),
        ENGLISH(Locale.ENGLISH.toString()),
        VIETNAMESE(new Locale("vi").toString());

        String code;

        SupportedLanguage(String language) {
            code = language;
        }

        public static SupportedLanguage getSupportedLanguage(String code) {

            if (CHINESE_SIMPLFIED.code.equals(code)) {
                return CHINESE_SIMPLFIED;
            } else if (VIETNAMESE.code.equals(code)) {
                return VIETNAMESE;
            } else if (ENGLISH.code.equals(code)) {
                return ENGLISH;
            }
            return null;
        }

        public String getCode() {
            return code;
        }
    }

    public static void setLanguage(final Context context, final String language, final Boolean Loading) {
		/*final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		pref.edit().putString(KEY_LANGUAGE, language).commit();
		Locale locale = new Locale(language.code);
	    Locale.setDefault(locale);

	    Configuration config = context.getResources().getConfiguration();
	    config.locale = locale;

	    context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());*/
        HttpParams httpParams = new HttpParams();
        httpParams.put("languageCode", language);
        if (Loading)
            ((BaseActivity) context).showCustomDialog(i18nLength() > 0 ? getI18nValue("downloading") : context.getString(R.string.downloading));
        LogUtils.e("请求语言文件路径--->" + BuildConfig.getBaseUrl(context) + BuildConfig.TranslateApi);
        HttpUtils.getWithoutCookiesAndShouldCacheByUrl(context, BuildConfig.getBaseUrl(context) + BuildConfig.TranslateApi, httpParams, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if (t != null) {
                    try {
                        JSONObject jsonObjectElement = new JSONObject(t.trim());
                        //若请求语言不存在，serves返回默认语言
                        //保存语言CODE
                        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
                        pref.edit().putString(KEY_LANGUAGE, jsonObjectElement.getString("LangCode")).commit();
                        updateResConfiguration(context, language);
                        //保存语言i18n
                        setI18n(context, jsonObjectElement.getString("LangMap"));
                        i18n = jsonObjectElement.getJSONObject("LangMap");
                        LogUtils.e("请求语言文件成功--->" + i18n.toString());
                    } catch (Exception e) {
                        Log.e("Get I18n Exception ", e.toString());
                        //已在启动页SplashActivity初始化i18n
                    }
                } else {
                    Log.e("Get I18n Exception ", "返回参数为空");
                    //已在启动页SplashActivity初始化i18n
                }
                if (Loading) ((BaseActivity) context).dismissCustomDialog();
                sendLanguageSettingBroadcast(context);
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                Log.e("Get I18n Exception ", "Http onFailure");
                //已在启动页SplashActivity初始化i18n
                if (i18nLength() > 0) {
                    sendLanguageSettingBroadcast(context);
                    Toast.makeText(context, i18nLength() > 0 ? getI18nValue("FailDownloadLanguagePackage") : context.getString(R.string.FailDownloadLanguagePackage), Toast.LENGTH_SHORT).show();
                } else {
                    setLanguageFail(context, language, true, strMsg);
                }
                if (Loading) ((BaseActivity) context).dismissCustomDialog();
            }
        });
    }
	/*
		过时API
	 */
	/*public static  SupportedLanguage getLanguage(Context context) {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		String language = pref.getString(KEY_LANGUAGE, null);
		return SupportedLanguage.getSupportedLanguage(language);
	}*/

    /*
        返回缓存语言代码或者系统地区语言代码
     */
    public static String getLanguage(Context context) {
        try {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
            String language = pref.getString(KEY_LANGUAGE, null);
            if (language != null) return language;
            Locale locale = context.getResources().getConfiguration().locale;
            return locale.getLanguage() + "-" + locale.getCountry();
        } catch (Exception e) {
            CrashReport.postCatchedException(e);
        }
        return "";
    }

    private static void setLanguageFail(final Context context, final String language, final Boolean Loading, String msg) {
        try {
            if (alertDialog == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(context.getString(R.string.FailDownloadLanguagePackage));
                builder.setPositiveButton(context.getString(R.string.retry),
                        new DialogInterface.OnClickListener() {
                            @SuppressWarnings("ResultOfMethodCallIgnored")
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                setLanguage(context, language, Loading);
                            }
                        });
                builder.setCancelable(false);
                alertDialog = builder.create();
            }
            if (alertDialog != null && !alertDialog.isShowing()) {
                alertDialog.show();
            }
        } catch (Exception e) {
            CrashReport.postCatchedException(e);
        }
    }

    private static void updateResConfiguration(Context context, String language) {
        try {
            Locale locale = new Locale(language);
            Locale.setDefault(locale);

            Configuration config = context.getResources().getConfiguration();
            config.locale = locale;
        } catch (Exception e) {
            CrashReport.postCatchedException(e);
        }

    }

    /*
        保存i18n
     */
    private static void setI18n(Context mContext, String msg) {

        if (msg == null) return;
        try {
            //创建一个FileOutputStream对象,MODE_PRIVATE代表该文件是私有数据，只能被应用本身访问，在该模式下，写入的内容会覆盖原文件的内容
            FileOutputStream fos = mContext.openFileOutput("EMMSI18N.txt", MODE_PRIVATE);
            //将获取过来的值放入文件
            fos.write(msg.getBytes(Charset.forName("UTF-8")));
            //关闭数据流
            fos.close();
        } catch (Exception e) {
            LogUtils.e("setI18n---异常--->" + e);
            e.printStackTrace();
        }

//        if (msg == null) return;
//        try {
//            //创建一个FileOutputStream对象,MODE_PRIVATE代表该文件是私有数据，只能被应用本身访问，在该模式下，写入的内容会覆盖原文件的内容
//            FileOutputStream fos = mContext.openFileOutput("EMMSI18N.txt", MODE_PRIVATE);
//            OutputStreamWriter writer = new OutputStreamWriter(fos, "uft-8");
//            //将获取过来的值放入文件
//            writer.write(msg);
//            //关闭数据流
//            writer.close();
//            fos.close();
//        } catch (Exception e) {
//            LogUtils.e("setI18n---异常--->" + e);
//            e.printStackTrace();
//        }
    }

    /*
        获取i18n
     */
    private static JSONObject getI18n(Context mContext) {
        FileInputStream inStream = null;
        StringBuffer sb = new StringBuffer();
        JSONObject json = new JSONObject();
        try {
            inStream = mContext.openFileInput("EMMSI18N.txt");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inStream));
            byte[] buffer = new byte[inStream.available()];
            int hasRead = 0;
            while ((hasRead = inStream.read(buffer)) != -1) {
                sb.append(new String(buffer, 0, hasRead, "UTF-8"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (sb != null) {
            try {
                json = new JSONObject(sb.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return json;
    }

    /*
        初始化i18n
     */
    public static void initI18n(Context context) {
        i18n = getI18n(context);
    }

    /*
        获取某code对应的i18n值，有则返回value，无则返回code
     */
    public static String getI18nValue(String code) {
        if (code != null && !code.equals("")) {
            String value;
            try {
                if (i18n.has(code)) {
                    value = i18n.getString(code);
                    return value;
                }
            } catch (Exception e) {
                Log.e(TAG, "Get I18n Error; Code is not exist");
            }
        }
        return code;
    }

    /*
        获取i18n的长度
     */
    public static int i18nLength() {
        try {
            if (i18n != null) {
                return i18n.length();
            }
        } catch (Exception e) {
            CrashReport.postCatchedException(e);
        }

        return 0;
    }

    /*
        广播事件，通知Activity更新UI
     */
    private static void sendLanguageSettingBroadcast(Context mContext) {
        Intent intent = new Intent();
        intent.setAction(LANGUAGE_SETTING_ACTION);
        mContext.sendBroadcast(intent);
    }
}
