package com.emms.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * A class to manager the value store in {@link SharedPreferences}.
 *
 * 
 */
public final class SharedPreferenceManager {

	public static final String KEY_COOKIE = "cookie";

	public static final String USER_NAME = "username";

	public static final String PASS_WORD = "password";

	public static final String KEY_LAST_SYNC_DATA_DATE = "last-sync-data-date";

	public static final String USER_DATA_FROM_SERVER = "UserData";

	public static final String LOGIN_DATA="Data";

	public static final String FACTORY="Factory";

	public static final String MSG="Msg";

	public static final String USER_ROLE_ID="UserRole_ID";

	public static final String USER_ROLES="USER_ROLES";

	public static final String USER_MODULE_LIST="Module_ID_List";

	public static final String LANGUAGE="language";

	public static final String LANGUAGE_CHANGE="language_change";

	public static final String NETWORK="network";

	public static final String DATA_UPDATE_TIME="data_update_time";
	public static final String DATABASE_VERSION="DataBase_version";


	public static final String INTERANET_URL="IntranetURL";
	public static final String EXTRANET_URL="ExtranetURL";
	public static final String APP_MODE="appMode";

	public static final String MAINTIANCEN = "maintianence";

	public static final String FOUCES_TIME="facous_time";

	public static final String UPDATE_ID="update_id";//更新id

	public static final String TimeZone = "timezone";//时区

	private SharedPreferenceManager() {

	}

	public static long getFacousTime(Context context){
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		return pref.getLong(FOUCES_TIME,1200L);
	}

	public static void setFoucesTime(Context context,long focusTime){
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		pref.edit().putLong(FOUCES_TIME,focusTime).commit();
	}

	public static String getTimeZone(Context context){
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		return pref.getString(TimeZone,"8");
	}

	public static void setTimeZone(Context context,String Zone){
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		pref.edit().putString(TimeZone,Zone).commit();
	}

	public static String getUpdateId(Context context){
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		return pref.getString(UPDATE_ID,"");
	}

	public static void setUpdateId(Context context,String updateId){
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		pref.edit().putString(UPDATE_ID,updateId).commit();
	}

	public static String getAppMode(Context context) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		return pref.getString(APP_MODE, null);
	}

	public static void setAppMode(Context context, String appMode) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		pref.edit().putString(APP_MODE, appMode).commit();
	}

	public static String getInteranetUrl(Context context) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		return pref.getString(INTERANET_URL, null);
	}

	public static void setInteranetUrl(Context context, String interanetUrl) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		pref.edit().putString(INTERANET_URL, interanetUrl).commit();
	}

	public static String getExtranetUrl(Context context) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		return pref.getString(EXTRANET_URL, null);
	}

	public static void setExtranetUrl(Context context, String extranetUrl) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		pref.edit().putString(EXTRANET_URL, extranetUrl).commit();
	}

	public static String getCookie(Context context) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		return pref.getString(KEY_COOKIE, null);
	}

	public static void setCookie(Context context, String region) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		pref.edit().putString(KEY_COOKIE, region).commit();
	}

	public static String getUserName(Context context) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		return pref.getString(USER_NAME, null);
	}

	public static void setUserName(Context context, String username) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		pref.edit().putString(USER_NAME, username).commit();
	}

	public static String getPassWord(Context context) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		return pref.getString(PASS_WORD, null);
	}

	public static void setPassWord(Context context, String passWord) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		pref.edit().putString(PASS_WORD, passWord).commit();
	}
	public static long getLastSyncDataDate(Context context) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		return pref.getLong(KEY_LAST_SYNC_DATA_DATE, 0L);
	}

	public static void setLastSyncDataDate(Context context,
										   long lastSyncDataDate) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		pref.edit().putLong(KEY_LAST_SYNC_DATA_DATE, lastSyncDataDate).commit();
	}

	public static String getUserData(Context context) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		return pref.getString(USER_DATA_FROM_SERVER, null);
	}

	public static void setUserData(Context context, String Data) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		pref.edit().putString(USER_DATA_FROM_SERVER, Data).commit();
	}
	public static String getLoginData(Context context) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		return pref.getString(LOGIN_DATA, null);
	}

	public static void setLoginData(Context context, String Data) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		pref.edit().putString(LOGIN_DATA, Data).commit();
	}

	public static void setMsg(Context context, String Data) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		pref.edit().putString(MSG, Data).commit();
	}
	public static String getMsg(Context context) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		return pref.getString(MSG, null);
	}

	public static void setUserRoleID(Context context, String Data) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		pref.edit().putString(USER_ROLE_ID, Data).commit();
	}
	public static String getUserRoleID(Context context) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		return pref.getString(USER_ROLE_ID, null);
	}

	public static void setUserRoleIDS(Context context, String Data) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		pref.edit().putString(USER_ROLES, Data).commit();
	}
	public static String getUserRoleIDS(Context context) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		return pref.getString(USER_ROLES, null);
	}

	public static void setUserModuleList(Context context, String Data) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		pref.edit().putString(USER_MODULE_LIST, Data).commit();
	}
	public static String getUserModuleList(Context context) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		return pref.getString(USER_MODULE_LIST, null);
	}

	public static String getFactory(Context context) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		return pref.getString(FACTORY, null);
	}

	public static void setFactory(Context context, String Data) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		pref.edit().putString(FACTORY, Data).commit();
	}

	public static String getLanguage(Context context) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		return pref.getString(LANGUAGE, null);
	}

	public static void setLanguage(Context context, String Data) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		pref.edit().putString(LANGUAGE, Data).commit();
	}

	public static boolean getLanguageChange(Context context) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		return pref.getBoolean(LANGUAGE_CHANGE, false);
	}

	public static void setLanguageChange(Context context, boolean Data) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		pref.edit().putBoolean(LANGUAGE_CHANGE, Data).commit();
	}

	public static String getNetwork(Context context) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		return pref.getString(NETWORK, "");
	}

	public static void setNetwork(Context context, String Data) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		pref.edit().putString(NETWORK, Data).commit();
	}

	public static String getDatabaseVersion(Context context) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		return pref.getString(DATABASE_VERSION, null);
	}

	public static void setDatabaseVersion(Context context, String Data) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		pref.edit().putString(DATABASE_VERSION, Data).commit();
	}

	public static long getDataUpdateTime(Context context) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		return pref.getLong(DATA_UPDATE_TIME,0);
	}

	public static void setDataUpdateTime(Context context, long Data) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		pref.edit().putLong(DATA_UPDATE_TIME, Data).commit();
	}

	/**
	* 说明：保存map
	* 添加时间：2019/10/8 下午2:01
	* 作者：Jason
	*/
	public static void putHashMapData(Context context, String key, Map<String, String> datas) {
		JSONArray mJsonArray = new JSONArray();
		Iterator<Map.Entry<String, String>> iterator = datas.entrySet().iterator();

		JSONObject object = new JSONObject();

		while (iterator.hasNext()) {
			Map.Entry<String, String> entry = iterator.next();
			try {
				object.put(entry.getKey(), entry.getValue());
			} catch (JSONException e) {
				LogUtils.e("解释出错---->"+e.toString());
			}
		}
		mJsonArray.put(object);

		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString(key, mJsonArray.toString());
		editor.commit();
	}

	/**
	* 说明：取出map
	* 添加时间：2019/10/8 下午2:01
	* 作者：Jason
	*/
	public static Map<String, String> getHashMapData(Context context, String key) {

		Map<String, String> datas = new HashMap<>();
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		String result = pref.getString(key, "");
		try {
			JSONArray array = new JSONArray(result);
			for (int i = 0; i < array.length(); i++) {
				JSONObject itemObject = array.getJSONObject(i);
				JSONArray names = itemObject.names();
				if (names != null) {
					for (int j = 0; j < names.length(); j++) {
						String name = names.getString(j);
						String value = itemObject.getString(name);
						datas.put(name, value);
					}
				}
			}
		} catch (JSONException e) {
			LogUtils.e("解释出错---->"+e.toString());
		}

		return datas;
	}


}
 