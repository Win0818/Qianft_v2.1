package com.qianft.m.qian.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import com.qianft.m.qian.R;
import com.qianft.m.qian.common.Constant;
import com.qianft.m.qian.utils.LogUtil;
import com.qianft.m.qian.utils.MySharePreData;

/**
 * Created by Administrator on 2016/11/11.
 */

public class UpdateDialog extends Dialog{

    private static final String TAG = "--UpdateDialog--";
    private static UpdateDialog mUpdateDialog = null;
    public UpdateDialog(Context context) {
        super(context);
    }

    public UpdateDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    /**
     * Helper for creating a custom dialog
     */
    public static class Builder {
        private Context mContext;
        private boolean mCancelable = true;
        private UpdateDialogListener mUpdateListener = null;
        private String mUpdateContent;
        private String[] updateContentDetail = new String[3];
        private String mNewVersionName;
        private int mNewVersionCode;
        //private OnClickListener updataNowListener;
        //private OnClickListener updateAfterListerner;

        public Builder(Context context) {
            this.mContext = context;
        }
        /**
         * 设置可否取消关闭对话框
         * @param cancelable
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setCancelable(boolean cancelable) {
            this.mCancelable = cancelable;
            return this;
        }
        /**
         * 设置按钮点击监听器
         * @param listener
         * @return his Builder object to allow for chaining of calls to set methods
         */
        public Builder setUpdateDialogListener(UpdateDialogListener listener) {
            this.mUpdateListener = listener;
            return this;
        }
        /**
         * 设置更新内容
         * @param updateContent
         * @return his Builder object to allow for chaining of calls to set methods
         */
        public Builder setUpdateContent(String updateContent) {
            this.mUpdateContent = updateContent;
            return this;
        }

        /**
         * 设置更新版本号
         * @param newVersionName
         * @return his Builder object to allow for chaining of calls to set methods
         */
        public Builder setUpdateVersion(String newVersionName) {
            this.mNewVersionName = newVersionName;
            return this;
        }

        /**
         * 设置更新版本号
         * @param newVersionCode
         * @return his Builder object to allow for chaining of calls to set methods
         */
        public Builder setNewVersionCode(int newVersionCode) {
            this.mNewVersionCode = newVersionCode;
            return this;
        }
        /**
         * 创建更新对话框
         * @return
         */
        public UpdateDialog create() {
            final UpdateDialog dialog = new UpdateDialog(mContext, R.style.detail_alert_dialog);
            dialog.setContentView(R.layout.update_dialog);
            dialog.setCancelable(mCancelable);
            dialog.setCanceledOnTouchOutside(false);
            if ( mUpdateContent.contains("\r\n")) {
                updateContentDetail = mUpdateContent.split("\r\n", 3);
            }
            WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
            layoutParams.width = layoutParams.WRAP_CONTENT;
            layoutParams.height = layoutParams.WRAP_CONTENT;

            TextView tv_title = (TextView) dialog.findViewById(R.id.version_title);
            TextView tv_UpdateContent_1 = (TextView) dialog.findViewById(R.id.update_content_1);
            TextView tv_UpdateContent_2 = (TextView) dialog.findViewById(R.id.update_content_2);
            TextView tv_UpdateContent_3 = (TextView) dialog.findViewById(R.id.update_content_3);
            tv_title.setText("钱富通" + mNewVersionName + "震撼发布");
            tv_UpdateContent_1.setText("1:" + updateContentDetail[0]);
            tv_UpdateContent_2.setText("2:" + updateContentDetail[1]);
            tv_UpdateContent_3.setText("3:" + updateContentDetail[2]);
            ImageButton updateNow = (ImageButton) dialog.findViewById(R.id.update_now);
            ImageButton updateAfter = (ImageButton) dialog.findViewById(R.id.update_after);

            //点击立即更新按钮
            updateNow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mUpdateListener != null) {
                        mUpdateListener.onConfirm();
                        dialog.dismiss();
                    }
                }
            });
            //点击稍后更新按钮
            updateAfter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mUpdateListener != null) {
                        mUpdateListener.onCancel();
                        dialog.dismiss();
                    }
                }
            });
            CheckBox mUpdate_CB = (CheckBox) dialog
                    .findViewById(R.id.update_checkbox);
            mUpdate_CB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        MySharePreData.SetBooleanData(mContext,
                                Constant.UPDATE_DIALOG,
                                mNewVersionCode + "", !isChecked);
                        dialog.dismiss();
                        LogUtil.d(TAG, "isChecked:  " + isChecked);
                    }
                });
            return dialog;
        }
    }

    //按钮监听接口
    public interface UpdateDialogListener {
        public void onConfirm();
        public void onCancel();
    }
}
