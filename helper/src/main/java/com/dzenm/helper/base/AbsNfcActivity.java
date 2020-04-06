package com.dzenm.helper.base;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.provider.Settings;
import android.widget.TextView;

import com.dzenm.helper.date.DateHelper;
import com.dzenm.helper.dialog.InfoDialog;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * @author dinzhenyan
 * @date 2019-04-30 20:03
 * <p>
 * <pre>
 * <!--开发nfc的权限-->
 * <uses-permission android:name="android.permission.NFC"/>
 * <!--声明只有带有nfc功能的手机才能下载你在google市场发布的具有NFC功能的app-->
 * <uses-feature android:name="android.hardware.nfc"
 *               android:required="true" />
 * </pre>
 */
public abstract class AbsNfcActivity extends AbsBaseActivity {

    // show text log
    private TextView mTextView;
    private NfcAdapter mNfcAdapter;

    // nfc info
    protected Tag mTag;

    // 是否显示dialog的日志提示, 是否自动检测NFC可用, 是否可以未开启NFC使用
    private boolean isShowDialog = false, isAutoDetection = true, isCanCancel = false;

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
    protected void setTextView(TextView textView) {
        mTextView = textView;
    }

    /**
     * @param showDialog 是否通过dialog显示NFC的状态
     */
    public void setShowDialog(boolean showDialog) {
        isShowDialog = showDialog;
    }

    @Override
    protected void onStart() {
        super.onStart();
        findingNfc();
    }

    protected void findingNfc() {
        // 获取NFC的Adapter
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        // 创建一个PendingIntent, 当检测到一条NFC消息, 就会通过PendingIntent执行此Intent并调用窗口
        mPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isAutoDetection) {
            // 检查是否支持NFC
            checkNfcAvailable(mNfcAdapter);
        }
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
        log("当前设备不支持NFC功能");
    }

    /**
     * NFC可用
     */
    protected void availableNfc(NfcAdapter nfcAdapter) {
        log("当前设备支持NFC");
        if (!nfcAdapter.isEnabled()) {     // 判断设备NFC功能是否打开
            getDefaultDialogSetting("NFC未开启, 暂无法使用NFC功能, 是否进入NFC设置页面手动开启?")
                    .setOnClickListener(new InfoDialog.OnInfoClickListener() {
                        @Override
                        public boolean onClick(InfoDialog infoDialog, boolean b) {
                            if (b) {
                                Intent intent = new Intent(Settings.ACTION_NFC_SETTINGS);
                                startActivity(intent);
                            } else {
                                if (!isCanCancel) {
                                    finish();
                                }
                            }
                            return true;
                        }
                    }).show();
        } else {
            log("请将NFC设备靠近手机背面");
            // 打开前台发布系统, 使页面优于其它NFC处理, 当检测到一个Tag标签就会执行mPendingIntent
            nfcAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // 获取到Tag标签对象
        super.onNewIntent(intent);
        mTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        // 获取卡片的UID
        String uid = byteArrayToHex(mTag.getId());
        readNfcUid(uid);
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
     * @param uid 读取NFC的UID
     */
    protected void readNfcUid(String uid) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mNfcAdapter != null) {
            // 关闭前台发布系统
            mNfcAdapter.disableForegroundDispatch(this);
        }
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
            log("不支持NDEF格式");
            return null;
        }
        //判断可变的长度的类型
        if (!Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
            log("Nfc内存空间不足");
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
            log("内存空间不足");
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
            log("写入成功");
            mifareUltralight.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private InfoDialog getDefaultDialogSetting(String message) {
        return InfoDialog.newInstance(this).setTitle("").setMessage(message);
    }

    /**
     * @param message 打印log日志的信息
     */
    protected void log(String message) {
        String log = DateHelper.getCurrentTimeMillis() + ": " + message + "\n";
        logD(log);
        if (isShowDialog) {
            getDefaultDialogSetting(message)
                    .setButtonText("确定")
                    .setOnClickListener(null)
                    .show();
        } else if (mTextView != null) {
            mTextView.setText(message);
        }
    }
}
