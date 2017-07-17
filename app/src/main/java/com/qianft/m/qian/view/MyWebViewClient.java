package com.qianft.m.qian.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.MimeTypeMap;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.qianft.m.qian.BaseApplication;
import com.qianft.m.qian.bean.TokenBean;
import com.qianft.m.qian.callback.ProgressCallback;
import com.qianft.m.qian.common.Constant;
import com.qianft.m.qian.common.Global;
import com.qianft.m.qian.utils.Installation;
import com.qianft.m.qian.utils.LogUtil;
import com.qianft.m.qian.utils.MySharePreData;
import com.qianft.m.qian.utils.NoAdTool;
import com.qianft.m.qian.utils.TokenManagerUtil;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;

import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.internal.Util;

public class MyWebViewClient extends WebViewClient{

	private WebView webView;
	private ProgressCallback mProgressCallback;
	private Context context;
	private static String homeurl = "http://m.qianft.com/";
	public static final String TAG = "MyWebViewClient";

	public MyWebViewClient (WebView webView, ProgressCallback progressCallback, Context context) {

		this.webView = webView;
		this.context = context;
		this.mProgressCallback = progressCallback;
	}

	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		LogUtil.i("Wing", "MyWebViewClient..shouldOverrideUrlLoading..  url=" + url);
		Map<String, String> headerMap = TokenManagerUtil.getToken(context);
		view.loadUrl(url, headerMap);
		return true;
	}

	@Override
	public void onPageStarted(WebView view, String url, Bitmap favicon) {
		super.onPageStarted(view, url, favicon);
		if (mProgressCallback != null) {
			mProgressCallback.onLoading();
		}
	}

	@Override
	public void onPageFinished(WebView view, String url) {
		super.onPageFinished(view, url);
		if (mProgressCallback != null) {
			mProgressCallback.onLoaded();
		}
		view.loadUrl("javascript:function setTop(){document.querySelector('#BAIDU_DUP_fp_wrapper').style.display=\"none\";}setTop();");
		view.loadUrl("javascript:window.jsObj.getSource('<head>'+" +
				"document.getElementsByTagName('html')[0].innerHTML+'</head>');");
	}

	@Override
	public void onReceivedError(WebView view, int errorCode,
								String description, String failingUrl) {
		if (view != null) {
			view.stopLoading();
			view.clearView();
		}
		if (mProgressCallback != null) {
			mProgressCallback.onError();
		}
		super.onReceivedError(view, errorCode, description, failingUrl);
		view.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
	}

/*	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
	@SuppressLint("NewApi")*/
	@Override
	public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
		url = url.toLowerCase();
		if (!url.contains(Constant.ADDRESS)) {
			if (NoAdTool.hasAd(context, url)) {
				return super.shouldInterceptRequest(view, url);
			} else {
				return new WebResourceResponse(null, null, null);
			}
		} else {
			return super.shouldInterceptRequest(view, url);

		}
	}


		/*JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("Token", "1111");
		} catch (Exception e) {
			e.printStackTrace();
		}
		String url = requests.getUrl().toString().toLowerCase();
		String method = "";
		if(!url.contains(Constant.ADDRESS)){
			if (NoAdTool.hasAd(context, url)) {
				return super.shouldInterceptRequest(view, url);
			}else{
				return new WebResourceResponse(null,null,null);
			}
		}else{
			CookieManager cookieManager = CookieManager.getInstance();
			String cookieStr = cookieManager.getCookie(url);
			LogUtil.d("wuyong", "Cookies = " + cookieStr);
			String ajaxUrl = url;
			RequestBody rb;
			LogUtil.d("Wing", "requests.getMethod(): " + requests.getMethod());
			if (requests.getMethod().equals("GET")) {
				method = "GET";
				rb = null;
			} else {
				method = "POST";
				rb = RequestBody.create(JSON, jsonObject.toString());
			}
			try {
				OkHttpClient httpClient = new OkHttpClient();

				Request request = new Request.Builder()
						.method(method, rb)
						.url(url.trim())
						.addHeader("Token", "111111111") //add headers
						.addHeader("UDID", "222222222")
						.build();
				Response response = httpClient.newCall(request).execute();
				LogUtil.d("Wing", "response:  " + response.toString() + "aaaaa" + response.body().toString());
				return new WebResourceResponse(
						getMimeType(url), // set content-type
						response.header("content-encoding", "utf-8"),
						response.body().byteStream());
			}  catch (Exception e) {
				return null;
			}
			//return super.shouldInterceptRequest(view, url);
		}
	}*/

	/*@Override
	public void onLoadResource(WebView view, String url) {
		super.onLoadResource(view, url);
	}

	//get mime type by url
	public String getMimeType(String url) {
		String type = null;
		String extension = MimeTypeMap.getFileExtensionFromUrl(url);
		if (extension != null) {
			if (extension.equals("js")) {
				return "text/javascript";
			}
			else if (extension.equals("css")) {
				return "text/css";
			}
			else if (extension.equals(".html")) {
				return "text/html";
			}
			else if (extension.equals(".jpg")) {
				return "application/x-jpg";
			}
			else if (extension.equals(".png")) {
				return "application/x-png";
			}
			else if (extension.equals("svg")) {
				return "image/svg+xml";
			}
			type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
		}
		return type;
	}*/
	/*@Override
	public WebResourceResponse shouldInterceptRequest(WebView view, String url) {

		//if (request != null && request.getUrl() != null && request.getMethod().equalsIgnoreCase("get")) {
		//String url = request.getUrl().toString().toLowerCase();
		if (!url.contains(Constant.ADDRESS)) {
			if (NoAdTool.hasAd(context, url)) {
				return super.shouldInterceptRequest(view, url);
			} else {
				return new WebResourceResponse(null, null, null);
			}
		} else {
			String ajaxUrl  = injectIsParams(url);
			LogUtil.d("Wing", "ajaxUrl: " + ajaxUrl);
			return super.shouldInterceptRequest(view, ajaxUrl);
		}
	}


		@SuppressLint("NewApi")
		@Override
		public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {

			if (request != null && request.getUrl() != null && request.getMethod().equalsIgnoreCase("get")) {
				String url = request.getUrl().toString().toLowerCase();
				if (!url.contains(Constant.ADDRESS)) {
					if (NoAdTool.hasAd(context, url)) {
						return super.shouldInterceptRequest(view, url);
					} else {
						return new WebResourceResponse(null, null, null);
					}
				} else {
					//String ajaxUrl  = injectIsParams(url);
					return super.shouldInterceptRequest(view, request);
				}
			} else {
				final Map<String, String> extraHeaders = getExtraHeaders();
				String scheme = request.getUrl().getScheme().trim();
				try {
					if (scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https")) {
                        //String ajaxUrl  = injectIsParams(request.getUrl().toString());
                        URL ajaxUrl  = new URL(injectIsParams(request.getUrl().toString()));
						//URL ajaxUrl  = new URL(request.getUrl().toString());
                        //return super.shouldInterceptRequest(view, ajaxUrl);
                        HttpURLConnection conn = (HttpURLConnection) ajaxUrl.openConnection();
						LogUtil.d("Wing", "response: header " + request.getRequestHeaders().toString());
						//conn.getHeaderField();
                        conn.setRequestMethod("POST");
						conn.setDoOutput(true);
						conn.setDoInput(true);
						//conn.connect();
						//conn.setRequestMethod("POST");
						*//*for (Map.Entry<String, String> h : extraHeaders.entrySet()) {
							conn.addRequestProperty(h.getKey(), h.getValue());
						}*//*
						String contentType = conn.getContentType().split(";")[0];
						String encoding    = conn.getContentType().split(";")[0];
						LogUtil.d("Wing", "response:  " + contentType + "++++++++++" +  encoding + "+++++++++" + conn.getResponseCode());
                        // URL url = new URL(injectIsParams(request.getUrl().toString()));
                        //URLConnection connection = url.openConnection();
						LogUtil.d("Wing", "response:  " + conn.getContentType() + "aaaaa" +  conn.getHeaderField("encoding") + "----" + conn.getResponseCode() + "-------inputstream:  " + conn.getInputStream());
                        // return new WebResourceResponse(conn.getContentType(), conn.getHeaderField("encoding"), conn.getInputStream());
                         return new WebResourceResponse(contentType, "UTF-8", conn.getInputStream());
                       // return new WebResourceResponse(contentType, encoding, conn.getInputStream());
                    }
				} catch (IOException e) {
					e.printStackTrace();
					InputStream is = new ByteArrayInputStream(e.getMessage().getBytes());
					return new WebResourceResponse("text/plain", "UTF-8", 500, "Intercept failed",
							Collections.<String, String>emptyMap(), is);

				}
				//return null;
			}
			 return null;
		}*/

		/*@Override
		public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
			if (!TextUtils.isEmpty(url) && Uri.parse(url).getScheme() != null) {
				String scheme = Uri.parse(url).getScheme().trim();
				if (scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https")) {
					try {
						URL urlRes = new URL(injectIsParams(url));
						URLConnection connection = urlRes.openConnection();
						return new WebResourceResponse(connection.getContentType(), connection.getHeaderField("encoding"), connection.getInputStream());
					} catch (MalformedURLException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			return null;
		}*/

	@Override
	public void onFormResubmission(WebView view, Message dontResend, Message resend) {
		super.onFormResubmission(view, dontResend, resend);
	}

	public static String injectIsParams(String url) {
		if (url != null && !url.contains("UDID=") && !url.contains("Token=")) {
			if (url.contains("?")) {
				//return url + "&UDID=" + Global.installationId + "&Token=" + TokenBean.getTokenIstance().getToken();
				return url + "&UDID=" + Global.installationId + "&Token=" + 111111111;
			} else {
				//return url + "?UDID=" + Global.installationId + "&Token=" + TokenBean.getTokenIstance().getToken();
				return url + "?UDID=" + Global.installationId + "&Token=" + 111111111;
			}
		} else {
			return url;
		}
	}


	/*@SuppressLint("NewApi")
	@Override
	public WebResourceResponse shouldInterceptRequest(WebView view, final WebResourceRequest request) {
		if (request != null && request.getUrl() != null) {
			String scheme = request.getUrl().getScheme().trim();
			if (scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https")) {
				return super.shouldInterceptRequest(view, new WebResourceRequest() {
					@Override
					public Uri getUrl() {
						return Uri.parse(injectIsParams(request.getUrl().toString()));
					}

					@SuppressLint("NewApi")
					@Override
					public boolean isForMainFrame() {
						return request.isForMainFrame();
					}

					@Override
					public boolean isRedirect() {
						return false;
					}

					@SuppressLint("NewApi")
					@Override
					public boolean hasGesture() {
						return request.hasGesture();
					}

					@SuppressLint("NewApi")
					@Override
					public String getMethod() {
						return request.getMethod();
					}

					@SuppressLint("NewApi")
					@Override
					public Map<String, String> getRequestHeaders() {
						return request.getRequestHeaders();
					}
				});
			}
		}
		return super.shouldInterceptRequest(view, request);
	}


	@Override
	public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
		if (!TextUtils.isEmpty(url) && Uri.parse(url).getScheme() != null) {
			String scheme = Uri.parse(url).getScheme().trim();
			if (scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https")) {
				return super.shouldInterceptRequest(view, injectIsParams(url));
			}
		}
		return super.shouldInterceptRequest(view, url);
	}*/
	public static Map<String, String> getExtraHeaders() {
		Map<String, String> map = new HashMap<>();
		map.put("Token", TokenBean.getTokenIstance().getToken());
		map.put("UDID", Global.installationId);
		return map;
	}
}
