
package com.qianft.m.qian.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.text.TextUtils;
import android.util.Log;

/**
 * @charset UTF-8
 * @date 2016-7-11
 * @version 
 */
public class JsonUtils {
	public static JSONObject initSSLWithHttpClinet(String path)
			throws ClientProtocolException, IOException {
		HTTPSTrustManager.allowAllSSL();
		JSONObject jsonObject = null;
		int timeOut = 30 * 1000;
		HttpParams param = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(param, timeOut);
		HttpConnectionParams.setSoTimeout(param, timeOut);
		HttpConnectionParams.setTcpNoDelay(param, true);

		SchemeRegistry registry = new SchemeRegistry();
		registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		registry.register(new Scheme("https", TrustAllSSLSocketFactory.getDefault(), 443));
		ClientConnectionManager manager = new ThreadSafeClientConnManager( param, registry);
		DefaultHttpClient client = new DefaultHttpClient(manager, param);

		HttpGet request = new HttpGet(path);
		// HttpGet request = new HttpGet("https://www.alipay.com/");
		HttpResponse response = client.execute(request);
		HttpEntity entity = response.getEntity();
		BufferedReader reader = new BufferedReader(new InputStreamReader( entity.getContent()));
		StringBuilder result = new StringBuilder();
		String line = "";
		while ((line = reader.readLine()) != null) {
			result.append(line);
			try {
				jsonObject = new JSONObject(line);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		Log.e("HTTPS TEST", result.toString());
		return jsonObject;
	}

	/**
	 * 带ERROR_CODE的构造函数
	 */
	public static JSONObject buildJsonObj(ERROR_CODE reCode) throws Exception{
		JSONObject result = new JSONObject();
		if (reCode == null) {
			reCode = ERROR_CODE.X0000;
		}
		String reMsg = reCode.getName();
		if (reMsg == null) {
			reMsg = "操作成功";
		}
		result.put("errCode", reCode);
		result.put("errMsg", reMsg);
		return result;
	}

	/**
	 * 带ERROR_CODE的构造函数
	 */
	public static String buildJsonStr(ERROR_CODE reCode) throws Exception{
		JSONObject result = new JSONObject();
		if (reCode == null) {
			reCode = ERROR_CODE.X0000;
		}
		String reMsg = reCode.getName();
		if (reMsg == null) {
			reMsg = "操作成功";
		}
		result.put("errCode", reCode);
		result.put("errMsg", reMsg);
		return result.toString();
	}

	/**
	 * 带ERROR_CODE与jsonCt的两参构造函数
	 */
	public String buildJsonStr(ERROR_CODE reCode, String jsonCt) throws Exception{
		JSONObject result = new JSONObject();
		if (reCode == null) {
			reCode = ERROR_CODE.X0000;
		}
		String reMsg = reCode.getName();
		if (reMsg == null) {
			reMsg = "操作成功";
		}
		result.put("errCode", reCode);
		result.put("errMsg", reMsg);
		if (!TextUtils.isEmpty(jsonCt)) {
			result.put("ref", jsonCt);
		}
		return result.toString();
	}

	/**
	 * 带ERROR_CODE与Object的两参构造函数
	 */
	public static JSONObject buildJsonObj(ERROR_CODE reCode, Object obj) throws Exception{
		JSONObject result = new JSONObject();
		if (reCode == null) {
			reCode = ERROR_CODE.X0000;
		}
		String reMsg = reCode.getName();
		if (reMsg == null) {
			reMsg = "操作成功";
		}
		result.put("errCode", reCode);
		result.put("errMsg", reMsg);
		if (!isEmpty(obj)) {
			result.put("rel", obj);
		}
		return result;
	}

	public static boolean isEmpty(Object obj)
	{
		if (obj == null)
		{
			return true;
		}
		if ((obj instanceof List))
		{
			return ((List) obj).size() == 0;
		}
		if ((obj instanceof String))
		{
			return ((String) obj).trim().equals("");
		}
		if ((obj instanceof String))
		{
			return ((String) obj).trim().equals("null");
		}
		return false;
	}

	//解析链接json数组
	public static List<String> parseAdUrlJSON2List(String para) {
		List<String> list = new ArrayList<String>();
		try {
			JSONObject json = new JSONObject(para);
			JSONArray arrayUrls = json.getJSONArray("urls");
			for (int i=0; i <  arrayUrls.length(); i++) {
				list.add(arrayUrls.getString(i));
			}
			JSONArray arrayPayurls = json.getJSONArray("payurls");
			for (int i=0; i < arrayPayurls.length(); i++) {
				list.add(arrayPayurls.getString(i));
			}
			LogUtil.d("Wing", "ad url list:  " + list.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
}
