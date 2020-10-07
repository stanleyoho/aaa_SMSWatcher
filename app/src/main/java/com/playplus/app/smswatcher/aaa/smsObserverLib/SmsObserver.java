package com.playplus.app.smswatcher.aaa.smsObserverLib;

import android.app.Activity;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.playplus.app.smswatcher.aaa.utils.FormatUtils;
import com.playplus.app.smswatcher.aaa.utils.LogUtils;

/***
 * 短信接收观察者
 *
 * @author 江钰锋 0152
 * @version [版本号, 2015年9月17日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class SmsObserver extends ContentObserver {

    private Context mContext;
    public static final int MSG_RECEIVED_CODE = 1001;
    private SmsHandler mHandler;
    private Uri mUri ;
    private String TAG = getClass().getName();

    /***
     * 构造器
     * @param context
     * @param callback 短信接收器
     * @param smsFilter 短信过滤器
     */
    public SmsObserver(Activity context, SmsResponseCallback callback, SmsFilter smsFilter) {
        this(new SmsHandler(callback,smsFilter));
        this.mContext = context;
    }

    public SmsObserver(Context context, SmsResponseCallback callback) {
        this(new SmsHandler(callback));
        this.mContext = context;
    }

    public SmsObserver(SmsHandler handler) {
        super(handler);
        this.mHandler = handler;
    }

    /***
     * 设置短信过滤器
     * @param smsFilter
     */
    public void setSmsFilter(SmsFilter smsFilter) {
        mHandler.setSmsFilter(smsFilter);
    }

    /***
     * 注册短信变化观察者
     *
     * @see [类、类#方法、类#成员]
     */
    public void registerSMSObserver() {
        Uri uri = Uri.parse("content://sms");
        if (mContext != null) {
            mContext.getContentResolver().registerContentObserver(uri,
                    true, this);
        }
    }

    /***
     * 注销短信变化观察者
     *
     * @see [类、类#方法、类#成员]
     */
    public void unregisterSMSObserver() {
        if (mContext != null) {
            mContext.getContentResolver().unregisterContentObserver(this);
        }
        if (mHandler != null) {
            mHandler = null;
        }
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange, uri);
        if (uri == null) {
            LogUtils.i(TAG,"onChange 沒有uri");
            mUri = Uri.parse("content://sms/inbox");
        } else {
            LogUtils.i(TAG,"onChange 有uri");
            mUri = uri;
        }

        if (mUri.toString().contains("content://sms/raw") || mUri.toString().equals("content://sms")) {
            LogUtils.i(TAG,"onChange uri:"+mUri.toString()+"不符合條件");
            return;
        }
        LogUtils.i(TAG,"onChange uri:"+mUri.toString()+"符合條件");
        try {
            Cursor c = mContext.getContentResolver().query(mUri, null, null,
                    null, "date desc");
            if (c != null) {
                if (c.moveToFirst()) {
                    String type = c.getString(c.getColumnIndex("type"));
                    if(!type.equals("1")) {
                        LogUtils.i(TAG,"onChange message type 不正確,Type:"+type);
                        return;
                    }

                    String address = c.getString(c.getColumnIndex("address"));
                    String body = c.getString(c.getColumnIndex("body"));
                    String messageId = c.getString(c.getColumnIndex("_id"));
                    String messageTime = c.getString(c.getColumnIndex("date"));
                    String targetTime = "";
                    try{
                     targetTime = FormatUtils.INSTANCE.parseToTargetTimeFormat(Long.parseLong(messageTime));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    if (mHandler != null) {
                        mHandler.obtainMessage(MSG_RECEIVED_CODE, new String[]{messageId,address, body,targetTime})
                                .sendToTarget();
                    }
                }
                c.close();
            }
        } catch (SecurityException e) {
            Log.e(getClass().getName(), "获取短信权限失败", e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
