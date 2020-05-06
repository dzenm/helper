package com.dzenm.lib.base;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.provider.Settings;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.dzenm.lib.R;
import com.dzenm.lib.date.DateHelper;
import com.dzenm.lib.log.Logger;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * @author dzenm
 * @date 2020/5/3 下午4:01
 * <p>
 * <pre>
 * <!--开发nfc的权限-->
 * <uses-permission android:name="android.permission.NFC"/>
 * <!--声明只有带有nfc功能的手机才能下载你在google市场发布的具有NFC功能的app-->
 * <uses-feature android:name="android.hardware.nfc"
 *               android:required="true" />
 * </pre>
 */
public class NFCHelper {

    private Activity mActivity;
    private NfcAdapter mNfcAdapter;
    private final String TAG = mActivity.getClass().getSimpleName() + "| ";

    // show text log
    private TextView mTextView;

    // nfc info
    protected Tag mTag;

    // 是否显示dialog的日志提示, 是否自动检测NFC可用, 是否可以未开启NFC使用
    private boolean isShowDialog = false, isAutoDetection = true, isCanCancel = false;

    public NFCHelper(Activity activity, TextView textView) {
        mActivity = activity;
        mTextView = textView;
    }

    private PendingIntent mPendingIntent;

    /**
     * @param autoDetection 设置自动检测是否支持NFC和NFC开启状态
     */
    public void setAutoDetection(boolean autoDetection) {
        isAutoDetection = autoDetection;
    }

    /**
     * @param textView 显示NFC状态的TextView
     */
    public void setTextView(TextView textView) {
        mTextView = textView;
    }

    /**
     * @param showDialog 是否通过dialog显示NFC的状态
     */
    public void setShowDialog(boolean showDialog) {
        isShowDialog = showDialog;
    }

    public void onStart() {
        findingNfc();
    }

    public void onResume() {
        // 检查是否支持NFC
        if (isAutoDetection) checkNfcAvailable(mNfcAdapter);
    }

    public String onNewIntent(Intent intent) {
        // 获取到Tag标签对象
        mTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        // 获取卡片的UID
        return byteArrayToHex(mTag.getId());
    }

    public void onPause() {
        // 关闭前台发布系统
        if (mNfcAdapter != null) mNfcAdapter.disableForegroundDispatch(mActivity);
    }

    public void findingNfc() {
        // 获取NFC的Adapter
        mNfcAdapter = NfcAdapter.getDefaultAdapter(mActivity);
        // 创建一个PendingIntent, 当检测到一条NFC消息, 就会通过PendingIntent执行此Intent并调用窗口
        mPendingIntent = PendingIntent.getActivity(mActivity, 0,
                new Intent(mActivity, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0
        );
    }

    /**
     * 检查NFC是否可用
     */
    private void checkNfcAvailable(NfcAdapter nfcAdapter) {
        if (nfcAdapter == null) {           // 判断设备是否支持NFC功能
            unavailableNfc();
        } else {
            availableNfc(nfcAdapter);
        }
    }

    /**
     * NFC不可用
     */
    protected void unavailableNfc() {
        log(mActivity.getText(R.string.nfc_info_unavailable));
    }

    /**
     * NFC可用
     */
    protected void availableNfc(NfcAdapter nfcAdapter) {
        log(mActivity.getText(R.string.nfc_info_available));
        if (!nfcAdapter.isEnabled()) {     // 判断设备NFC功能是否打开
            new AlertDialog.Builder(mActivity)
                    .setTitle(mActivity.getText(R.string.dialog_info))
                    .setMessage(mActivity.getText(R.string.nfc_info_disabled))
                    .setPositiveButton(mActivity.getText(R.string.dialog_btn_setting),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Settings.ACTION_NFC_SETTINGS);
                                    mActivity.startActivity(intent);
                                }
                            })
                    .setNegativeButton(mActivity.getText(R.string.dialog_btn_cancel),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (!isCanCancel) {
                                        mActivity.finish();
                                    }
                                }
                            })
                    .create().show();
        } else {
            log(mActivity.getText(R.string.nfc_info_close_to_back));
            // 打开前台发布系统, 使页面优于其它NFC处理, 当检测到一个Tag标签就会执行mPendingIntent
            nfcAdapter.enableForegroundDispatch(mActivity, mPendingIntent, null, null);
        }
    }

    /**
     * @param bytes NFC卡ID字节数据
     * @return 十六进制String
     */
    protected String byteArrayToHex(byte[] bytes) {
        int i, in;
        String[] hex = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};
        String out = "";
        for (byte aByte : bytes) {
            in = (int) aByte & 0xff;
            i = (in >> 4) & 0x0f;
            out += hex[i];
            i = in & 0x0f;
            out += hex[i];
        }
        return out;
    }

    /**
     * @param ndefRecord NDEF文本记录
     * @return 从第三个字节开始，后面的文本数据
     */
    protected String parseTextRecord(NdefRecord ndefRecord) {
        /**
         * 判断数据是否为NDEF格式
         */
        //判断TNF
        if (ndefRecord.getTnf() != NdefRecord.TNF_WELL_KNOWN) {
            log(mActivity.getText(R.string.nfc_info_ndef_unavailable));
            return null;
        }
        //判断可变的长度的类型
        if (!Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
            log(mActivity.getText(R.string.nfc_info_under_capacity));
            return null;
        }
        try {
            //获得字节数组，然后进行分析
            byte[] payload = ndefRecord.getPayload();
            //下面开始NDEF文本数据第一个字节，状态字节
            //判断文本是基于UTF-8还是UTF-16的，取第一个字节"位与"上16进制的80，16进制的80也就是最高位是1，
            //其他位都是0，所以进行"位与"运算后就会保留最高位
            String textEncoding = ((payload[0] & 0x80) == 0) ? "UTF-8" : "UTF-16";
            //3f最高两位是0，第六位是1，所以进行"位与"运算后获得第六位
            int languageCodeLength = payload[0] & 0x3f;
            //下面开始NDEF文本数据第二个字节，语言编码
            //获得语言编码
            String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
            //下面开始NDEF文本数据后面的字节，解析出文本
            return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        } catch (Exception e) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * 是否是MifareUltralight数据格式
     */
    protected void isExistMifareUltralight() {
        boolean hasMifareUltralight = false;
        String[] techList = mTag.getTechList();
        log("tech list is: " + techList.length);
        for (String tech : techList) {
            if (tech.indexOf("MifareUltralight") >= 0) {
                hasMifareUltralight = true;
                break;
            }
        }
        if (!hasMifareUltralight) {
            log("不支持MifareUltralight的数据格式");
            return;
        }
        log("支持MifareUltralight的数据格式");
    }

    /**
     * @return MifareUltralight格式数据
     */
    protected String readMifareUltralightData() {
        MifareUltralight mifareUltralight = MifareUltralight.get(mTag);
        try {
            mifareUltralight.connect();
            byte[] bytes = mifareUltralight.readPages(4);
            mifareUltralight.close();
            // 读取成功
            return new String(bytes, Charset.forName("GB2312"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param data 写入MifareUltralight格式的数据
     */
    protected void writeMifareUltralightData(String data) {
        if (data.length() < 22) {
            log(mActivity.getText(R.string.nfc_info_under_capacity));
            return;
        }
        MifareUltralight mifareUltralight = MifareUltralight.get(mTag);
        try {
            mifareUltralight.connect();
            for (int i = 0; i < data.length(); i += 2) {
                String s = data.substring(i, i + 2);
                byte[] datas = s.getBytes(Charset.forName("GB2312"));
                mifareUltralight.writePage(4 + i, datas);
            }
            log(mActivity.getText(R.string.nfc_info_write_success));
            mifareUltralight.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param message 打印log日志的信息
     */
    protected void log(CharSequence message) {
        String log = DateHelper.getCurrentTimeMillis() + ": " + message + "\n";
        Logger.d(TAG + log);
        if (isShowDialog) {
            new AlertDialog.Builder(mActivity)
                    .setTitle(mActivity.getText(R.string.dialog_info))
                    .setMessage(message)
                    .setPositiveButton(mActivity.getText(R.string.dialog_btn_confirm),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                    .create()
                    .show();
        } else if (mTextView != null) {
            mTextView.setText(message);
        }
    }
}
