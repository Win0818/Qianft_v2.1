package com.qianft.m.qian.activity;

import android.Manifest;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.zxing.Result;
import com.qianft.m.qian.BaseApplication;
import com.qianft.m.qian.R;
import com.qianft.m.qian.bean.TokenBean;
import com.qianft.m.qian.common.Constant;
import com.qianft.m.qian.common.Global;
import com.qianft.m.qian.service.AppUpgradeService;
import com.qianft.m.qian.utils.ERROR_CODE;
import com.qianft.m.qian.utils.HttpUtils;
import com.qianft.m.qian.utils.Installation;
import com.qianft.m.qian.utils.JsonUtils;
import com.qianft.m.qian.utils.LogUtil;
import com.qianft.m.qian.utils.MySharePreData;
import com.qianft.m.qian.utils.QrUtil;
import com.qianft.m.qian.utils.SharePopMenu;
import com.qianft.m.qian.utils.StorageUtils;
import com.qianft.m.qian.utils.TokenManagerUtil;
import com.qianft.m.qian.utils.Util;
import com.qianft.m.qian.view.GlobalProgressDialog;
import com.qianft.m.qian.view.MyWebView;
import com.qianft.m.qian.view.MyWebViewClient;
import com.qianft.m.qian.view.UpdateDialog;
import com.qianft.m.qian.zxing.DecodeImage;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.ShareContent;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;


/**
 *
 * @author
 */
public class MainActivity extends BaseActivity implements OnClickListener {

    private String TAG = "Wing";
    private MyWebView mWebView;
    private ImageButton mRefreshBtn;
    private LinearLayout mNoNetworkLinearLayout;
    private String mAddress = Constant.ADDRESS;
    //private String mAddress = "file:///android_asset/html/index.html";
    //private String mAddress = "http://192.168.0.88:8011/Home/Index";
    private boolean DEBUG = true;
    private String mShareUrl;
    private String mTitle;
    private long exitTime = 0;
    private IWXAPI wxApi;
    private GlobalProgressDialog mGlobalProgressDialog;
    private SharePopMenu popMenu;
    private Context mContext;
    private boolean flag = true;
    public static final int TAKE_PHOTO = 0x00001007;
    public static final int CROP_PHOTO = 0x00001008;
    private Uri imageUri;
    private String mImageUrl = null;
    private String mDescription = null;
    private RequestQueue requestQueue = null;
    private String downloadUrl = null;
    private String newVersionName = null;
    private String packageSize = null;
    private String updateContent;
    private int newVersionCode = 1;
    private PushAgent mPushAgent;
    private boolean pushFlag= true;
    private String mAuthCallback = null;
    private String mAuthCancel = null;
    private ValueCallback<Uri> mUploadMessage;
    public static boolean isActive = true; //activity是否在后台
    public static boolean Screen_Off_Flag = true;
    private String url;
    private boolean isQR;//判断是否为二维码
    private Result result;//二维码解析结果
    private File file;
    private ImageView imageView;

    final SHARE_MEDIA[] displaylist = new SHARE_MEDIA[]
            {
                SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE,SHARE_MEDIA.SINA,
                SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE
            };
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.UPDATE_DIALOG_HANDLER:
                    JSONObject jsonObject = (JSONObject) msg.obj;
                    try {
                        downloadUrl = jsonObject.getString("DownloadUrl");
                        newVersionName = jsonObject.getString("New_VersionName");
                        newVersionCode = jsonObject.getInt("New_VersionCode");
                        packageSize = jsonObject.getString("Package_Size");
                        updateContent = jsonObject.getString("Update_Content");
                            /*if (jsonObject.has("Update_Content") && updateContent.contains("\r\n")) {
                                updateContentDetail = updateContent.split("\r\n", 3);
                            }*/
                        LogUtil.d("Wing", "response:  " + jsonObject.toString()
                                + "-----" + downloadUrl + "-----" + newVersionName
                                + "-----" + newVersionCode + "-----" + packageSize
                                + "-----" + updateContent);
                        upgradeVersion();
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;
                case 0x00001010:
                    Bitmap bitmap = (Bitmap) msg.obj;
                    if (bitmap != null) {
                        result = DecodeImage.handleQRCodeFormBitmap(bitmap);
                    }
                    LogUtil.d("Wing", "QR sUrl-------->>>>>>>>>>>>>>>>>: " + result);
                    UMImage image = new UMImage(MainActivity.this, bitmap);//bitmap文件
                    new ShareAction(MainActivity.this)
                            .setListenerList(umShareListener)
                            .setDisplayList(displaylist)
                            .withMedia(image).open();
                    break;
                default:
                    break;
            }
        }
    };
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        if(Build.VERSION.SDK_INT>=23){
            String[] mPermissionList = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_LOGS, Manifest.permission.SET_DEBUG_APP,
                    Manifest.permission.SYSTEM_ALERT_WINDOW, Manifest.permission.WRITE_APN_SETTINGS,
                    Manifest.permission.READ_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(MainActivity.this, mPermissionList, 100);
        }
        mContext = this;
        registerScreenBroadcast();
        mPushAgent = PushAgent.getInstance(this);
        initView();
        initData();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 100:
                break;
            default:

        }
    }

    private void initView() {
        mWebView = (MyWebView) findViewById(R.id.webview);
        mNoNetworkLinearLayout = (LinearLayout) findViewById(R.id.no_network);

        mRefreshBtn = (ImageButton) findViewById(R.id.refresh_btn);
        mRefreshBtn.setOnClickListener(this);
        wxApi = WXAPIFactory.createWXAPI(this, Constant.APP_ID);
        wxApi.registerApp(Constant.APP_ID);

        if (!Util.isNetWorkConnected(this)) {
            mNoNetworkLinearLayout.setVisibility(View.VISIBLE);
        } else {
            setWebView();
        }
    }
	/*public void registerScreenBroadcast() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		registerReceiver(mScreenBroadcast, filter);
	}*/
    //友盟分享监听器
    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            LogUtil.d("plat","platform"+platform);
            Toast.makeText(MainActivity.this,  " 分享成功", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(MainActivity.this," 分享失败", Toast.LENGTH_SHORT).show();
            if(t!=null){
                LogUtil.d("throw","throw:"+t.getMessage());
            }
        }
        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(MainActivity.this," 分享取消", Toast.LENGTH_SHORT).show();
        }
    };
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get( this ).onActivityResult(requestCode, resultCode, data);
        LogUtil.d("result","onActivityResult");
        if (requestCode == 0) {
            if (null == mUploadMessage){
                return;
            }
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        }
    }
    private void initData() {
        EventBus.getDefault().register(this);
        requestQueue = Volley.newRequestQueue(mContext);
        cheakVersion();
    }
    /**
     * 当Activity执行onResume()时让WebView执行resume
     */
    @Override
    protected void onResume() {
        super.onResume();
        Bundle bun = getIntent().getExtras();
        String action = getIntent().getAction();
        LogUtil.i(TAG, "action::  -------  "   + action);
        if (pushFlag && action != null && action.equals("com.qianft.m.qian.push")) {
            String Push_Url = getIntent().getStringExtra("Push_Url");
            mWebView.loadUrl(Push_Url);
            pushFlag = false;
        }
        if (action != null && action.equals("com.qianft.m.qian.login")) {
            String login_url = getIntent().getStringExtra("login_url");
            mWebView.loadUrl(login_url);
        }
        //友盟统计
        MobclickAgent.onResume(this);
        LogUtil.d(TAG, "onResume:  is executed");
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.refresh_btn:
                if (!Util.isNetWorkConnected(this)) {
                    mNoNetworkLinearLayout.setVisibility(View.VISIBLE);
                } else {
                    mNoNetworkLinearLayout.setVisibility(View.INVISIBLE);
                    setWebView();
                }
                break;
            default:
                break;
        }
    }
    /**
     * 判断是否为二维码
     * param url 图片地址
     * return
     */
    private void create2DCoder(final String sUrl){
        final Bitmap bitmap = null;
        LogUtil.d("Wing", "QR sUrl: " + sUrl);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = QrUtil.create2DCoderBitmap(sUrl, 200, 200);
                Message message = new Message();
                message.what = 0x00001010;
                message.obj = bitmap;
                mHandler.sendMessage(message);
            }
        }).start();
    }
    //@SuppressLint("SetJavaScriptEnabled")
    private void setWebView() {
        try {
            Log.i(TAG, "MainActivity ----->>>>>setWebview");
            mWebView.addJavascriptInterface(/*getHtmlObject()*/new MyJsBridgeInterface(), "jsObj");
            Log.d("Wing", "");
            mWebView.loadUrl(mAddress);
            mWebView.setLongClickCallBack(new MyWebView.LongClickCallBack() {
                @Override
                public void onLongClickCallBack(String imgUrl) {
                    if (imgUrl.contains("GetQrCode?")) {
                        url = imgUrl;
                        imgUrl = imgUrl.substring(imgUrl.indexOf("url=") + 4);
                        Log.d("Wing", "onLongClickCallBack imgUrl: " + imgUrl);
                        create2DCoder(imgUrl);
                    }
                }
            });
        } catch (Exception e) {
            return;
        }
    }
    public void pickFile() {
        Intent chooserIntent = new Intent(Intent.ACTION_GET_CONTENT);
        chooserIntent.setType("image/*");
        startActivityForResult(chooserIntent, 0);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_BACK)) {
            if (mWebView != null) {
                LogUtil.d(TAG, "mWebView.getUrl();  "  + mWebView.getUrl());
                if (keyCode == KeyEvent.KEYCODE_BACK && (mWebView.getUrl().equals(Constant.ADDRESS) || mWebView.getUrl().equals(Constant.HOME_ADDRESS))) {
                    exitApp();
                    return true;
                } else {
                    mWebView.canBack();
                    return true;
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }
    /**
     * 连续两次点击Back键退出App
     */
    public void exitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.quit_app),
                    Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
            System.exit(0);
        }
    }
    /**
     *
     */
    private void loginWechat() {
        // send oauth request
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "wechat_sdk_demo_test";
        wxApi.sendReq(req);
    }

    private void loginWechat_2() {
        // send oauth request
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "login_state";
        wxApi.sendReq(req);
    }
    /**
     * 与js交互的对象
     *
     * @return
     */
    public class MyJsBridgeInterface {
        protected Context ctx;
        protected WebView vw;

        public MyJsBridgeInterface() {}
        public MyJsBridgeInterface(Context context, WebView webview) {
            this.ctx = context;
            this.vw = webview;
        }

        //Object insertObj = new Object() {
        @JavascriptInterface
        public void Js_Invoke_Android_Main_Interface(String functionName, String json) {
            JSONObject jsonObject = null;
            JSONObject returnJson = null;
            String mCallback = null;
            try {
                returnJson = new JSONObject();
                jsonObject = new JSONObject(json);
                mCallback = jsonObject.getString("callback");
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            switch (functionName) {
                case "share_To_Wechat_android":
                    share_To_Wechat_android(json);
                    break;
                case "loginWechat_android":
                    loginWechat_android(json);
                    break;
                case "wechat_Auth_Login_android":
                    wechat_Auth_Login_android(json);
                    break;
                case "takePhoto_android":
                    takePhoto_android(json);
                    break;
                case "getUserAPPInfo_android":
                    getUserAPPInfo_android(json);
                    break;
                case "startQQDialog_android":
                    startQQDialog_android(json);
                    break;
                case "clearUserInfo_android":
                    clearUserInfo_android(json);
                    break;
                case "startGesturePasswordSetup_android":
                    startGesturePasswordSetup_android(json);
                    break;
                case "setToken_android":
                    setToken(json);
                    break;
                case "clearToken_android":
                    clearToken(json);
                    break;
                case "setGestrueLock_android":   //设置保存手势解锁
                    setGestrueLock(json);
                    break;
                case "removeGestrueLock_android"://删除保存手势解锁
                    removeGestrueLock(json);
                    break;
                case "share_share_group_android"://通过传参分享特定目标
                    shareGroup(json);
                    break;
                default:
                    mWebView.loadUrl("javascript:" + mCallback + "(" + ERROR_CODE.X0002 +")" );
                    break;
            }
        }

        @JavascriptInterface
        public void getSource(String html) {
            LogUtil.d("html=   ", html);
        }

        @JavascriptInterface
        public void share_To_Wechat_android(final String webpageUrl,
                                            final String title, final String description, final String imageUrl) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mTitle = title;
                    mShareUrl = webpageUrl;
                    mDescription = description;
                    mImageUrl = imageUrl;
						/*popMenu.showAsDropDown(MainActivity.this
								.findViewById(R.id.main_root));*/

                    final SHARE_MEDIA[] displaylist = new SHARE_MEDIA[]
                            {
                                    SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE,SHARE_MEDIA.SINA,
                                    SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE
                            };
                    Bitmap thumb = BitmapFactory.decodeResource(getResources(),
                            R.drawable.app_icon);
                    // msg.setThumbImage(thumb);
                    Bitmap thumb2 = null;
                    try {
                        thumb2 = Glide.with(mContext).load(imageUrl).asBitmap() // 必须
                                .centerCrop().into(500, 500).get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    new ShareAction(MainActivity.this).setDisplayList(displaylist)
                            .withText(description)
                            .withTitle(title)
                            .withTargetUrl(webpageUrl)
                            .withMedia(new UMImage(MainActivity.this,
                                    thumb2))
                            .setListenerList(umShareListener)
                            .open();
                }
            });
        }

        public void  share_To_Wechat_android(final String json) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject returnJson = null;
                    String mCallback = null;
                    String mCancel = null;
                    String result = null;
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        //returnJson = new JSONObject();
                        mShareUrl = jsonObject.getString("link");
                        mTitle = jsonObject.getString("title");
                        mDescription = jsonObject.getString("desc");
                        mImageUrl = jsonObject.getString("imgUrl");

                        LogUtil.d("wing", "mImageUrl" + mImageUrl);
                        if (jsonObject.has("callback")) {
                            mCallback = jsonObject.getString("callback");
                        }
                        if (jsonObject.has("cancel")) {
                            mCancel = jsonObject.getString("cancel");
                        }
                        final SHARE_MEDIA[] displaylist = new SHARE_MEDIA[]
                            {
                                SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE,SHARE_MEDIA.SINA,
                                SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE
                            };
                        Bitmap thumb2 = null;
                        Bitmap bitmap1 = null;
                        try {
                            Glide.with(mContext).load(mImageUrl).asBitmap().listener(new RequestListener<String, Bitmap>() {
                                @Override
                                public boolean onResourceReady(Bitmap bitmap,
                                                               String arg1, Target<Bitmap> arg2,
                                                               boolean arg3, boolean arg4) {
                                    //thumb2 = bitmap;
                                    new ShareAction(MainActivity.this).setDisplayList(displaylist)
                                        .withText(mDescription)
                                        .withTitle(mTitle)
                                        .withTargetUrl(mShareUrl)
                                        .withMedia(new UMImage(MainActivity.this, bitmap))
                                        .setListenerList(umShareListener)
                                        .open();
                                    return false;
                                }
                                @Override
                                public boolean onException(Exception arg0,
                                                           String arg1, Target<Bitmap> arg2,
                                                           boolean arg3) {
                                    return false;
                                };
                            }) // 必须
                                    .centerCrop().into(500, 500).get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        returnJson = new JSONObject();
                        returnJson.put("errCode", "0000");
                        returnJson.put("errMsg", "执行成功");

                        JSONObject jsonObject2 = new JSONObject();
                        jsonObject2.put("link", mShareUrl);
                        jsonObject2.put("title", mTitle);
                        jsonObject2.put("desc", mDescription);

                        returnJson.put("ref", jsonObject2);
                    } catch (Exception e) {
                        try {
                            returnJson.put("errCode", "0001");
                            returnJson.put("errMsg", "NO");
                        } catch (JSONException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                        e.printStackTrace();
                        final String errorMsg = Util.getErrorInfo(e);
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("logInfo ", errorMsg);
                        map.put("type", "2");
                        try {
                            String response = HttpUtils.postRequest(Constant.ERROR_MSG_POST_URL,
                                    map, MainActivity.this);
                            LogUtil.d("Wing", "--post commit---" + response);
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }finally {
                        if (!TextUtils.isEmpty(mCallback) && returnJson != null) {
                            result = returnJson.toString();
                            mWebView.loadUrl("javascript:" + mCallback + "(" + result +")" );
                        }
                    }
                }
            });
        }

        public void loginWechat_android(final String json) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject returnJson = null;
                    String mCallback = null;
                    String mCancel = null;
                    String result = null;
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        String mLoginUrl = jsonObject.getString("loginUrl");
                        if (jsonObject.has("callback")) {
                            mCallback = jsonObject.getString("callback");
                        }
                        if (jsonObject.has("cancel")) {
                            mCancel = jsonObject.getString("cancel");
                        }
                        String uid = MySharePreData.GetData(MainActivity.this,
                                Constant.WECHAT_LOGIN_SP_NAME, "union_id");
                        if (TextUtils.isEmpty(uid)) {
                            loginWechat_2();
                        } else {
                            LogUtil.d(TAG, "union_id:   " + uid);
                            mWebView.loadUrl(mLoginUrl.replace("UNIONID", uid));
                        }
                        returnJson = new JSONObject();
                        returnJson.put("userVersionCode", Global.localVersionCode);
                        returnJson.put("userVersionName", Global.localVersionName);
                        returnJson.put("errCode", "0000");
                        returnJson.put("errMsg", "执行成功");

                        JSONObject jsonObject2 = new JSONObject();
                        jsonObject2.put("loginUrl", mLoginUrl);
                        returnJson.put("ref", jsonObject2);
                    } catch (Exception e) {
                        try {
                            returnJson.put("errCode", "0004");
                            returnJson.put("errMsg", "登录失败");
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                        e.printStackTrace();

                        final String errorMsg = Util.getErrorInfo(e);

                        Map<String, String> map = new HashMap<String, String>();
                        map.put("logInfo ", errorMsg);
                        map.put("type", "2");
                        try {
                            String response = HttpUtils.postRequest(Constant.ERROR_MSG_POST_URL,
                                    map, MainActivity.this);
                            LogUtil.d("Wing", "--post commit---" + response);
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }finally{
                        if (!TextUtils.isEmpty(mCallback) && returnJson != null) {
                            result = returnJson.toString();
                            mWebView.loadUrl("javascript:" + mCallback + "(" + result +")" );
                        }
                    }
                }
            });
        }
        @JavascriptInterface
        public void takePhoto_android(String path, String picFileName) {
            takePhoto(path, picFileName);
        }
        public void takePhoto_android(final String json) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject returnJson = null;
                    JSONObject jsonObject;
                    String mCallback = null;
                    String result;
                    try {
                        jsonObject = new JSONObject(json);
                        String mSaveTargetDir = jsonObject.getString("saveTargetDir");
                        String mPicFileName = jsonObject.getString("picFileName");

                        if (jsonObject.has("callback")) {
                            mCallback = jsonObject.getString("callback");
                        }
                        takePhoto(mSaveTargetDir, mPicFileName);

                        returnJson = new JSONObject();
                        returnJson.put("errCode", "0000");
                        returnJson.put("errMsg", "执行成功");

                        JSONObject jsonObject2 = new JSONObject();
                        jsonObject2.put("saveTargetDir", mSaveTargetDir);
                        jsonObject2.put("picFileName", mPicFileName);

                        returnJson.put("ref", jsonObject2);

                    } catch (Exception e) {
                        e.printStackTrace();
                        try {
                            returnJson.put("errCode", "0006");
                            returnJson.put("errMsg", "保存失败");
                        } catch (JSONException e2) {
                            e2.printStackTrace();
                        }
                        final String errorMsg = Util.getErrorInfo(e);
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("logInfo ", errorMsg);
                        map.put("type", "2");
                        try {
                            String response = HttpUtils.postRequest(Constant.ERROR_MSG_POST_URL,
                                    map, MainActivity.this);
                            LogUtil.d("Wing", "--post commit---" + response);
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    } finally {
                        if (!TextUtils.isEmpty(mCallback) && returnJson != null) {
                            result = returnJson.toString();
                            mWebView.loadUrl("javascript:" + mCallback + "(" + result +")" );
                        }
                    }
                }
            });
        }
        /**
         * 微信授权
         * @param json
         */
        @JavascriptInterface
        public void  wechat_Auth_Login_android(final String json) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    LogUtil.d(TAG, "wechat_Auth_Login_android  ------------------json"  + json);
                    String mCallback = null;
                    String mCancel = null;
                    JSONObject jsonObject = null;
                    JSONObject returnJson = null;

                    try {
                        jsonObject = new JSONObject(json);
                        //String userid = jsonObject.getString("userId");
                        //String postServerUrl = jsonObject.getString("postServerUrl");
                        //String auth_Success_Url = jsonObject.getString("auth_Success_Url");
                        LogUtil.d(TAG, "jsonObject.has(callback) "  + jsonObject.has("callback"));
                        if (jsonObject.has("callback")) {
                            mAuthCallback = jsonObject.getString("callback");
                        }
                        LogUtil.d(TAG, "jsonObject.has(cancel): "   + jsonObject.has("cancel"));
                        if (jsonObject.has("cancel")) {
                            mAuthCancel = jsonObject.getString("cancel");
                        }
                        //LogUtil.d("Wing", "userid:   " + userid);
                        //Util.SERVER_URL = postServerUrl;
                        //Util.Auth_Success_Url = auth_Success_Url;
                        //Util.USER_ID = userid;
                        loginWechat();

                        returnJson = new JSONObject();
                        returnJson.put("errCode", "0000");
                        returnJson.put("errMsg", "执行成功");

                        //JSONObject jsonObject2 = new JSONObject();
                        //jsonObject2.put("userId", userid);
                        //jsonObject2.put("postServerUrl", postServerUrl);
                        //jsonObject2.put("auth_Success_Url", auth_Success_Url);

                        //returnJson.put("ref", jsonObject2);

                        //if (!TextUtils.isEmpty(mCallback)) {
                        //mWebView.loadUrl("javascript:" + mSuccess + "(" + result +")" );
                        //}
                    } catch (JSONException e) {
                        try {
                            returnJson.put("errCode", "0003");
                            returnJson.put("errMsg", "授权失败");
                        } catch (JSONException e2) {
                            e2.printStackTrace();
                        }
                        e.printStackTrace();
                        final String errorMsg = Util.getErrorInfo(e);
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("logInfo ", errorMsg);
                        map.put("type", "2");
                        try {
                            String response = HttpUtils.postRequest(Constant.ERROR_MSG_POST_URL,
                                    map, MainActivity.this);
                            LogUtil.d("Wing", "--post commit---" + response);
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }finally {
                        if (!TextUtils.isEmpty(mCallback) && returnJson != null) {
                            LogUtil.d(TAG, "returnJson:   " + returnJson);
                            String result = returnJson.toString();
                            mWebView.loadUrl("javascript:" + mCallback + "(" + result +")" );
                        }
                    }
                }
            });
        }
        /**
         * 图片下载
         * @return
         */
        @JavascriptInterface
        public void downloadPicture_android(final String json) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject returnJson = new JSONObject();
                    JSONObject jsonObject = null;
                    String mCallback = null;
                    try {
                        jsonObject = new JSONObject(json);
                        String imageUrl = jsonObject.getString("imageUrl");
                        String savePath = jsonObject.getString("savePath");
                        String picFileName = jsonObject.getString("picFileName");
                        long totalSize = jsonObject.getInt("totalSize");

                        if (jsonObject.has("callback")) {
                            mCallback = jsonObject.getString("callback");
                        }
                        File mTempFile = new File(picFileName);
                        returnJson = new JSONObject();

                        returnJson.put("errCode", "0000");
                        returnJson.put("errMsg", "执行成功");

                        JSONObject jsonObject2 = new JSONObject();
                        jsonObject2.put("imageUrl", imageUrl);
                        jsonObject2.put("savePath", savePath);
                        jsonObject2.put("picFileName", picFileName);
                        jsonObject2.put("totalSize", totalSize);

                        returnJson.put("ref", jsonObject2);

                        long storage = StorageUtils.getAvailableStorage();
                        if (DEBUG) {
                            Log.i(TAG, "storage:" + storage + " totalSize:" + totalSize);
                        }

                        if (totalSize - mTempFile.length() > storage) {
                            returnJson.put("errCode", "0007");
                            returnJson.put("errMsg", "存储不足");
                            //throw new NoMemoryException("SD card no memory.");
                        }

                        Util.downLoadPicture(imageUrl, savePath, picFileName);

                    }catch (Exception e) {
                        try {
                            returnJson.put("errCode", "0009");
                            returnJson.put("errMsg", "保存失败");
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                        final String errorMsg = Util.getErrorInfo(e);
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("logInfo ", errorMsg);
                        map.put("type", "2");
                        try {
                            String response = HttpUtils.postRequest(Constant.ERROR_MSG_POST_URL,
                                    map, MainActivity.this);
                            LogUtil.d("Wing", "--post commit---" + response);
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    } finally {
                        if (!TextUtils.isEmpty(mCallback) && returnJson != null) {
                            LogUtil.d(TAG, "finally is running");
                            String result = returnJson.toString();
                            mWebView.loadUrl("javascript:" + mCallback + "(" + result +")" );
                        }
                    }
                }
            });
        }
        @JavascriptInterface
        public void clearUserInfo_android() {
            MySharePreData.SetData(mContext, Constant.WECHAT_LOGIN_SP_NAME,
                    "union_id", "");
        }

        public void clearUserInfo_android(String json) {

            JSONObject jsonObject = null;
            JSONObject returnJson = null;
            String mCallback = null;
            try {
                jsonObject = new JSONObject(json);
                if (jsonObject.has("callback")) {
                    mCallback = jsonObject.getString("callback");
                }
                returnJson = new JSONObject();
                returnJson.put("errCode", "0000");
                returnJson.put("errMsg", "执行成功");
                MySharePreData.SetData(mContext, Constant.WECHAT_LOGIN_SP_NAME,
                        "union_id", "");
                String result = returnJson.toString();
                mWebView.loadUrl("javascript:" + mCallback + "(" + result +")" );
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @JavascriptInterface
        public void startQQDialog_android(String qqId) {
            String url = "mqqwpa://im/chat?chat_type=wpa&uin=UIN".replace(
                    "UIN", qqId);
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        }
			/*public void startQQDialog_android(String json) {
				JSONObject jsonObject = null;
				JSONObject returnJson = null;
				String mCallback = null;
				try {
					jsonObject = new JSONObject(json);

					String mQQID = jsonObject.getString("qqId");
					if (jsonObject.has("callback")) {
						mCallback = jsonObject.getString("callback");
					}
					returnJson = new JSONObject();
					returnJson.put("errCode", "0000");
					returnJson.put("errMsg", "执行成功");
					String url = "mqqwpa://im/chat?chat_type=wpa&uin=UIN".replace(
							"UIN", mQQID);
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
					String result = returnJson.toString();
					mWebView.loadUrl("javascript:" + mCallback + "(" + result +")" );
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}*/

        @JavascriptInterface
        public void  getUserAPPInfo_android(final String json) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject jsonObject = null;
                    JSONObject returnJson = null;
                    String mCallback = null;
                    try {
                        LogUtil.d("Wing", "json_info:  " + json);
                        jsonObject = new JSONObject(json);
                        if (jsonObject.has("callback")) {
                            mCallback = jsonObject.getString("callback");
                        }
                        String phoneMode = android.os.Build.MODEL;
                        String systemSDK = android.os.Build.VERSION.SDK;

                        returnJson = new JSONObject();
                        returnJson.put("errCode", "0000");
                        returnJson.put("errMsg", "执行成功");

                        JSONObject returnRefJson = new JSONObject();
                        returnRefJson.put("userVersionCode", Global.localVersionCode);
                        returnRefJson.put("userVersionName", "v" + Global.localVersionName);
                        returnJson.put("ref", returnRefJson);
                        String result = returnJson.toString();
                        mWebView.loadUrl("javascript:" + mCallback + "(" + result +")" );
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        public void startGesturePasswordSetup_android(final String json) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        startActivity(new Intent(MainActivity.this, CreateGesturePasswordActivity.class));
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }
            });
        }
        @JavascriptInterface
        public void startGesturePasswordSetup_android() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        startActivity(new Intent(MainActivity.this, CreateGesturePasswordActivity.class));
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }
            });
        }

        @JavascriptInterface
        public void isSettingGesturePSW_android(final String json) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject jsonObject = null;
                    JSONObject returnJson = null;
                    String mCallback = null;
                    try {
                        jsonObject = new JSONObject(json);
                        if (jsonObject.has("callback")) {
                            mCallback = jsonObject.getString("callback");
                        }
                        returnJson = new JSONObject();
                        if (BaseApplication.getInstance().getLockPatternUtils().savedPatternExists()) {
                            returnJson.put("is_setting_gesture_password", true);
                        } else {
                            returnJson.put("is_setting_gesture_password", false);
                        }
                        Log.d("Wing", "is_setting_gesture_password:  " + returnJson.toString());
                        mWebView.loadUrl("javascript: " + mCallback + "(" + returnJson.toString() + ")");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        @JavascriptInterface
        public void changeGesturePassword_android() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        BaseApplication.getInstance().getLockPatternUtils().clearLock();
                        startActivity(new Intent(MainActivity.this, CreateGesturePasswordActivity.class));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        //登录时设置token
        public void setToken(final String para) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LogUtil.d("Wing", "token para: " + para);
                    TokenBean tokenBean = TokenBean.getTokenIstance();
                    JSONObject jsonObject = null;
                    String mCallback = null;
                    try {
                        jsonObject = new JSONObject(para);
                        TokenManagerUtil.setToken(MainActivity.this, jsonObject.getString("token"), jsonObject.getString("expireTime"));
                        if (jsonObject.has("callback")) {
                            mCallback = jsonObject.getString("callback");
                            mWebView.loadUrl("javascript:" + mCallback + "(" + JsonUtils.buildJsonStr(ERROR_CODE.X0000) +")" );
                        }
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        //退出登录清除token
        public void clearToken(final String para) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LogUtil.d("Wing", "clearToken ");
                    TokenManagerUtil.setToken(MainActivity.this, "", "");
                    JSONObject jsonPara;
                    String mCallback = null;
                    try {
                        jsonPara = new JSONObject(para);
                        if (jsonPara.has("callback")) {
                            mCallback = jsonPara.getString("callback");
                            mWebView.loadUrl("javascript:" + mCallback + "(" + JsonUtils.buildJsonStr(ERROR_CODE.X0000) +")" );
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        //设置手势密码
        public void setGestrueLock(final String para) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LogUtil.d("Wing", "gesture para: " + para);
                    JSONObject jsonPara;
                    String mCallback = null;
                    try {
                        jsonPara = new JSONObject(para);
                        BaseApplication.getInstance().getLockPatternUtils().saveLockPatternV2(jsonPara.getString("gestrueLock"));
                        if (jsonPara.has("callback")) {
                            mCallback = jsonPara.getString("callback");
                            mWebView.loadUrl("javascript:" + mCallback + "(" + JsonUtils.buildJsonStr(ERROR_CODE.X0000) +")" );
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        //清除手势密码
        public void removeGestrueLock(final String para) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject jsonPara;
                    String mCallback = null;
                    try {
                        jsonPara = new JSONObject(para);
                        BaseApplication.getInstance().getLockPatternUtils().clearLockV2();
                        if (jsonPara.has("callback")) {
                            mCallback = jsonPara.getString("callback");
                            mWebView.loadUrl("javascript:" + mCallback + "(" + JsonUtils.buildJsonStr(ERROR_CODE.X0000) +")" );
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
        }
        //特定目标分享
        public void shareGroup(final String para) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject paraJson;
                    String mCallback = null;
                    try {
                        paraJson = new JSONObject(para);
                        String shareType = paraJson.getString("type");
                        String mShareUrl = paraJson.getString("link");
                        String mTitle = paraJson.getString("title");
                        String mDescription = paraJson.getString("desc");
                        String mImageUrl = paraJson.getString("imgUrl");
                        Bitmap thumb = BitmapFactory.decodeResource(getResources(),
                                R.drawable.app_icon);
                        LogUtil.d("wing", "mImageUrl" + mImageUrl);
                        if (paraJson.has("callback")) {
                            mCallback = paraJson.getString("callback");
                        }
                        switch (shareType) {
                            case "friends":
                                new ShareAction(MainActivity.this)
                                        .setPlatform(SHARE_MEDIA.WEIXIN)//传入平台
                                        .withText(mDescription)
                                        .withTitle(mTitle)
                                        .withTargetUrl(mShareUrl)
                                        .withMedia(new UMImage(MainActivity.this, thumb))
                                        .setCallback(umShareListener)//回调监听器
                                        .share();
                                break;
                            case "circle":
                                new ShareAction(MainActivity.this)
                                        .setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)//传入平台
                                        .withText(mDescription)
                                        .withTitle(mTitle)
                                        .withTargetUrl(mShareUrl)
                                        .withMedia(new UMImage(MainActivity.this, thumb))
                                        .setCallback(umShareListener)//回调监听器
                                        .share();
                                break;
                            case "space":
                                new ShareAction(MainActivity.this)
                                        .setPlatform(SHARE_MEDIA.QZONE)//传入平台
                                        .withText(mDescription)
                                        .withTitle(mTitle)
                                        .withTargetUrl(mShareUrl)
                                        .withMedia(new UMImage(MainActivity.this, thumb))
                                        .setCallback(umShareListener)//回调监听器
                                        .share();
                                break;
                            case "qq":
                                new ShareAction(MainActivity.this)
                                        .setPlatform(SHARE_MEDIA.QQ)//传入平台
                                        .withText(mDescription)
                                        .withTitle(mTitle)
                                        .withTargetUrl(mShareUrl)
                                        .withMedia(new UMImage(MainActivity.this, thumb))
                                        .setCallback(umShareListener)//回调监听器
                                        .share();
                                break;
                            case "sms":
                                sendSMS(mDescription, mShareUrl );
                                break;
                            default:
                                break;
                        }
                        if (paraJson.has("callback")) {
                            mCallback = paraJson.getString("callback");
                            mWebView.loadUrl("javascript:" + mCallback + "(" + JsonUtils.buildJsonStr(ERROR_CODE.X0000) +")" );
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
        }
        //return insertObj;
    }

    /**
     * 发短信
     */
    private   void  sendSMS(String description, String webUrl){
        String smsBody = description + webUrl;
        Uri smsToUri = Uri.parse( "smsto:" );
        Intent sendIntent =  new  Intent(Intent.ACTION_VIEW, smsToUri);
        //短信内容
        sendIntent.putExtra( "sms_body", smsBody);
        sendIntent.setType( "vnd.android-dir/mms-sms" );
        startActivity(sendIntent);
    }

    /**
     * @param saveTargetDir
     * @param picFileName
     */
    private void takePhoto(String saveTargetDir, String picFileName) {
        String rootPath = Environment.getExternalStorageDirectory().toString()
                + "/";
        File pathDir = new File(rootPath + saveTargetDir);
        if (!pathDir.exists()) {
            pathDir.mkdirs();
        }
        File outputImage = new File(pathDir, picFileName + ".jpg");
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageUri = Uri.fromFile(outputImage);

        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        //startActivityForResult(intent, TAKE_PHOTO);
        startActivity(intent);

        //Bitmap saveBitmap =(Bitmap)params[0];
        //String picOuputPath =(String)params[1];
        FileOutputStream out = null;
        try {

            Bitmap saveBitmap = BitmapFactory
                    .decodeStream(getContentResolver().openInputStream(
                            imageUri));
            out = new FileOutputStream(saveTargetDir);
            saveBitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            // Release bitmap
            if (saveBitmap != null && !saveBitmap.isRecycled()) {
                saveBitmap.recycle();
                saveBitmap = null;
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "Failed to write image --- ");
        } finally {
            try {
                out.close();
            } catch (Exception e) {
            }
        }
    }



	/*@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case TAKE_PHOTO:
			if (resultCode == RESULT_OK) {
				Intent intent = new Intent("com.android.camera.action.CROP");
				intent.setDataAndType(imageUri, "image*//*");
				intent.putExtra("scale", true);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
				startActivityForResult(intent, CROP_PHOTO);
			}
			break;
		case CROP_PHOTO:
			if (resultCode == RESULT_OK) {
				try {
					Bitmap bitmap = BitmapFactory
							.decodeStream(getContentResolver().openInputStream(
									imageUri));
					picture.setImageBitmap(bitmap);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
			break;
		default:
			break;
		}
	}*/
    @Override
    protected void onPause() {
        super.onPause();
        //友盟统计
        Log.d("Wing", "onPause is executed! ");
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("wing", "onStop execute!0000000");

        //只执行一次
        if(!isAppOnForeground()){
            Log.d("wing", "onStop back");
            isActive = false;
            String tokenExpirn = MySharePreData.GetData(MainActivity.this, Constant.TOKEN_SP_NAME, Constant.EXPIRE_TIME_SP_KEY);
            if(BaseApplication.getInstance().getLockPatternUtils().savedPatternExists() && !TextUtils.isEmpty(tokenExpirn)){
                //从后台唤醒
                isActive = true;
                //Screen_Off_Flag = true;
                LogUtil.d(TAG, "onResume:  start UnlockGesturePasswordActivity");
                if (Long.valueOf(tokenExpirn).longValue() > new Date().getTime()) {
                    LogUtil.d(TAG, "onResume:  start UnlockGesturePasswordActivity");
                    Intent intent = new Intent(this, UnlockGesturePasswordActivity.class);
                    startActivity(intent);
                } else {
                    mWebView.loadUrl(Constant.USER_LOGIN_URL);
                }
            }
        }
    }
    /**
     * 是否在后台
     * @return
     */
    public boolean isAppOnForeground(){
        LogUtil.d("Wing", "onStop execute! isAppOnForeground111111");
        ActivityManager am = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        String curPackageName = getApplicationContext().getPackageName();
        List<RunningAppProcessInfo> app = am.getRunningAppProcesses();
        if(app==null){
            return false;
        }
        for(RunningAppProcessInfo a:app){
            if(a.processName.equals(curPackageName)&&
                    a.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND){
                Log.d("Wing", "isAppOnForeground------>>>>>>>>>>");
                return true;
            }
        }
        return false;
        /*ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        if(!TextUtils.isEmpty(curPackageName)&&curPackageName.equals(getPackageName())){
            return true;
        }
        return false;*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        //程序执行Destroy方法强制不弹出解锁界面
        LogUtil.d("Wing", "wwwwww");
        isActive = true;
		/*if (mScreenBroadcast != null) {
			unregisterReceiver(mScreenBroadcast);
		}*/
    }

    /**
     * EventBus2.0 微信授权
     * @param message
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void helloEventBus(String message) {
        Log.d("Wing", "message:  " + message);
        switch (message) {
            case "refresh_url":
                mWebView.loadUrl(Constant.ADDRESS);
                break;
            case "refresh_login":
                mWebView.loadUrl(Constant.USER_LOGIN_URL);
                break;
            case "auth_cancel":
                JSONObject returnCancelJson = new JSONObject();
                try {
                    returnCancelJson.put("errCode", "0000");
                    returnCancelJson.put("errMsg", "登录取消");
                    mWebView.loadUrl("javascript: " + mAuthCancel + "(" + returnCancelJson.toString() + ")");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case "auth_fail":
                JSONObject returnFailJson = new JSONObject();
                try {
                    returnFailJson.put("errCode", "0004");
                    returnFailJson.put("errMsg", "登录失败");
                    mWebView.loadUrl("javascript: " + mAuthCallback + "(" + returnFailJson.toString() + ")");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
		/*if (message.equals("hello")) {
			mWebView.loadUrl("http://m.qianft.com/WeiXin/Success");
		} else if (message.equals("login_state")) {
			String uid = MySharePreData.GetData(MainActivity.this,
					Constant.WECHAT_LOGIN_SP_NAME, "union_id");
			mWebView.loadUrl("http://m.qianft.com/UserLogin/WeChatLogin?unionId=UNIONID"
					.replace("UNIONID", uid));
		} else if (message.equals("")) {
			mWebView.loadUrl("");
		}*/
    }
    /**
     * 微信授权
     * @param bundle
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void weChat(Bundle bundle) {

        if ((Global.RESP.errCode == BaseResp.ErrCode.ERR_OK) &&
                (Global.RESP.getType() == ConstantsAPI.COMMAND_SENDAUTH)) {
            JSONObject returnJson = new JSONObject();
            try {
                if (Global.RESP.errCode == BaseResp.ErrCode.ERR_COMM);
                returnJson.put("errCode", "0000");
                returnJson.put("errMsg", "执行成功");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            LogUtil.d(TAG, "bundle::::  " + bundle.toString());
            JSONObject jsonRef = new JSONObject();
            Log.d("Wing", "");
            try {
                jsonRef.put("openid", bundle.getString("openid"));
                jsonRef.put("province", bundle.getString("province"));
                jsonRef.put("unionid", bundle.getString("unionid"));
                jsonRef.put("sex", bundle.getString("sex"));
                jsonRef.put("city", bundle.getString("city"));
                jsonRef.put("nickname", bundle.getString("nickname"));
                jsonRef.put("country", bundle.getString("country"));
                jsonRef.put("headimgurl", bundle.getString("headimgurl"));
                returnJson.put("ref", jsonRef);
                LogUtil.d("Wing", "returnJson.toString():   "  + returnJson.toString());
                mWebView.loadUrl("javascript: " + mAuthCallback + "(" + returnJson.toString() + ")");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 检查更新版本
     */
    public void cheakVersion() {
        LogUtil.d("Wing", "Global.serverVersionCode------>>>>>>>>");
        if (Util.isWifi(mContext)) {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Constant.DOWNLOAD_NEW_APK, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Message message = mHandler.obtainMessage();
                        message.what = Constant.UPDATE_DIALOG_HANDLER;
                        message.obj = response;
                        mHandler.sendMessage(message);
                    }
                }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            requestQueue.add(jsonObjectRequest);
        }
    }

    private void upgradeVersion() {
        boolean isUpdate = MySharePreData.GetBooleanTrueData(mContext,
                                Constant.UPDATE_DIALOG, newVersionCode + "");
        if (Global.localVersionCode < newVersionCode && isUpdate) {
           final UpdateDialog dialog = new UpdateDialog.Builder(this)
                    .setCancelable(false)
                    .setUpdateContent(updateContent)
                    .setUpdateDialogListener(new UpdateDialog.UpdateDialogListener() {
                        @Override
                        public void onConfirm() {
                            Intent intent = new Intent(MainActivity.this,
                                    AppUpgradeService.class);
                            LogUtil.i(TAG, "updateNow.setOnClickListener");
                            intent.putExtra("downloadUrl", downloadUrl);
                            startService(intent);
                        }
                        @Override
                        public void onCancel() {

                        }
                    })
                    .setUpdateVersion(newVersionName)
                    .setNewVersionCode(newVersionCode)
                    .create();
            dialog.show();
        }
    }
    /*
     * 推送Intent
     * (non-Javadoc)
     * @see android.app.Activity#onNewIntent(android.content.Intent)
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null && intent.getAction().equals("com.qianft.m.qian.push")) {
            setIntent(intent);
            LogUtil.d(TAG, "intent:   " + intent);
        }
    }
}
