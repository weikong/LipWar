package com.king.speak.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.baidu.aip.asrwakeup3.core.recog.listener.ChainRecogListener;
import com.baidu.aip.asrwakeup3.core.recog.listener.MessageStatusRecogListener;
import com.baidu.tts.chainofresponsibility.logger.LoggerProxy;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;
import com.baidu.voicerecognition.android.ui.BaiduASRDigitalDialog;
import com.baidu.voicerecognition.android.ui.DigitalDialogInput;
import com.king.speak.R;
import com.king.speak.httptask.HttpTaskUtil;
import com.king.speak.tts.control.InitConfig;
import com.king.speak.tts.control.MySyntherizer;
import com.king.speak.tts.control.NonBlockSyntherizer;
import com.king.speak.tts.listener.UiMessageListener;
import com.king.speak.tts.util.AutoCheck;
import com.king.speak.tts.util.OfflineResource;
import com.king.speak.ui.activity.tts.SaveFileActivity;
import com.king.speak.ui.activity.tts.SynthActivity;
import com.king.speak.ui.adapter.ChatTranslateAdapter;
import com.king.speak.ui.adapter.PopTranslateAdapter;
import com.king.speak.ui.bean.ChatTranslate;
import com.king.speak.ui.bean.LanguageType;
import com.king.speak.ui.view.popup.CommonPopupWindow;
import com.king.speak.util.SharePreferceTool;
import com.squareup.okhttp.Request;
import com.king.speak.tts.MainHandlerConstant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.king.speak.tts.MainHandlerConstant.INIT_SUCCESS;
import static com.king.speak.tts.MainHandlerConstant.PRINT;
import static com.king.speak.tts.MainHandlerConstant.UI_CHANGE_INPUT_TEXT_SELECTION;
import static com.king.speak.tts.MainHandlerConstant.UI_CHANGE_SYNTHES_TEXT_SELECTION;

/**
 * UI 界面调用
 * <p>
 * 本类仅仅初始化及释放MyRecognizer，具体识别逻辑在BaiduASRDialog。对话框UI在BaiduASRDigitalDialog
 * 依赖SimpleTransApplication 在两个activity中传递输入参数
 * <p>
 * Created by fujiayi on 2017/10/17.
 */

public class ActivityRegTranslateDialog extends ActivityAbstractRecog {

    /**
     * 对话框界面的输入参数
     */
    private DigitalDialogInput input;
    private ChainRecogListener chainRecogListener;
    private static String TAG = "ActivityUiDialog";

    private HttpTaskUtil httpTaskUtil = null;

    private StringBuffer stringBuffer = new StringBuffer();


    public ActivityRegTranslateDialog() {
        super(R.raw.uidialog_recog, false);
    }

    // ================== 初始化参数设置开始 ==========================
    /**
     * 发布时请替换成自己申请的appId appKey 和 secretKey。注意如果需要离线合成功能,请在您申请的应用中填写包名。
     * 本demo的包名是com.baidu.tts.sample，定义在build.gradle中。
     */
//    protected String appId = "11005757";
//
//    protected String appKey = "Ovcz19MGzIKoDDb3IsFFncG1";
//
//    protected String secretKey = "e72ebb6d43387fc7f85205ca7e6706e2";

    protected String appId = "11956666";

    protected String appKey = "fZTEvatC5pRwFxms0fUGQIqc";

    protected String secretKey = "W631v0AnXfGU19izCsg0hzDtBy9GL0Ko";

    // TtsMode.MIX; 离在线融合，在线优先； TtsMode.ONLINE 纯在线； 没有纯离线
    protected TtsMode ttsMode = TtsMode.MIX;

    // 离线发音选择，VOICE_FEMALE即为离线女声发音。
    // assets目录下bd_etts_common_speech_m15_mand_eng_high_am-mix_v3.0.0_20170505.dat为离线男声模型；
    // assets目录下bd_etts_common_speech_f7_mand_eng_high_am-mix_v3.0.0_20170512.dat为离线女声模型
    protected String offlineVoice = OfflineResource.VOICE_MALE;

    // ===============初始化参数设置完毕，更多合成参数请至getParams()方法中设置 =================

    // 主控制类，所有合成控制方法从这个类开始
    protected MySyntherizer synthesizer;

    protected static String DESC = "请先看完说明。之后点击“合成并播放”按钮即可正常测试。\n"
            + "测试离线合成功能需要首次联网。\n"
            + "纯在线请修改代码里ttsMode为TtsMode.ONLINE， 没有纯离线。\n"
            + "本Demo的默认参数设置为wifi情况下在线合成, 其它网络（包括4G）使用离线合成。 在线普通女声发音，离线男声发音.\n"
            + "合成可以多次调用，SDK内部有缓存队列，会依次完成。\n\n";

    private ChatTranslateAdapter adapter;

    private CommonPopupWindow popupWindow;
    private ListView listview_popup;
    private PopTranslateAdapter popTranslateAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * 有2个listner，一个是用户自己的业务逻辑，如MessageStatusRecogListener。另一个是UI对话框的。
         * 使用这个ChainRecogListener把两个listener和并在一起
         */
        chainRecogListener = new ChainRecogListener();
        // DigitalDialogInput 输入 ，MessageStatusRecogListener可替换为用户自己业务逻辑的listener
        chainRecogListener.addListener(new MessageStatusRecogListener(handler));
        myRecognizer.setEventListener(chainRecogListener); // 替换掉原来的listener

        setting.setVisibility(View.GONE);

        httpTaskUtil = new HttpTaskUtil();
        httpTaskUtil.setResultListener(new HttpTaskUtil.ResultListener() {
            @Override
            public void onResponse(final String response) {
                ivSend.setEnabled(true);
                btn.setEnabled(true);
                stringBuffer.append(response).append("\n");
//                txtLog.setText(stringBuffer.toString());
                ChatTranslate chatTranslate = adapter.getItem(adapter.getCount() - 1);
                chatTranslate.setTranslateContent(response);
                adapter.notifyDataSetChanged();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        listView.smoothScrollToPosition(adapter.getCount());
                    }
                });
                if (!TextUtils.isEmpty(response)){
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            speak(response);
                        }
                    },200);
                }
            }

            @Override
            public void onFailure(Request request, Exception e) {
                btn.setEnabled(true);
                ivSend.setEnabled(true);
            }
        });
        initListView();
        initialTts();
    }

    private void initListView(){
        if (listView == null)
            listView = (ListView)findViewById(R.id.listview);
        adapter = new ChatTranslateAdapter(this);
        adapter.setVoiceListener(new ChatTranslateAdapter.VoiceListener() {
            @Override
            public void speakText(String translate) {
                speak(translate);
            }
        });
        listView.setAdapter(adapter);
        ivSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = editText.getText().toString();
                if (TextUtils.isEmpty(input))
                    return;
                ivSend.setEnabled(false);
                addChatTranslate(input);
                editText.setText("");
            }
        });
        tvActionMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.showAsDropDown(ivActionMore,1,1);
            }
        });
        popupWindow = new CommonPopupWindow(this,R.layout.layout_popup, LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT) {
            @Override
            protected void initView() {
//                View view = popupWindow.getContentView();
//                listview_popup = (ListView)view.findViewById(R.id.listview_popup);
//                popTranslateAdapter = new PopTranslateAdapter(ActivityRegTranslateDialog.this);
//                listview_popup.setAdapter(popTranslateAdapter);
//                String[] arrayTranslate = getResources().getStringArray(R.array.array_popup_translate);
//                popTranslateAdapter.setData(Arrays.asList(arrayTranslate));
//                popTranslateAdapter.notifyDataSetChanged();
            }

            @Override
            protected void initEvent() {
                String language = SharePreferceTool.getInstance(ActivityRegTranslateDialog.this).getString(SharePreferceTool.DeaufaltTranslateLanguage,"英语");
                if (TextUtils.isEmpty(language)){
                    SharePreferceTool.getInstance(ActivityRegTranslateDialog.this).setCache(SharePreferceTool.DeaufaltTranslateLanguage,"英语");
                    language = SharePreferceTool.getInstance(ActivityRegTranslateDialog.this).getString(SharePreferceTool.DeaufaltTranslateLanguage,"英语");
                }
                tvActionMore.setText("（"+language+"）");
            }
        };
    }

    /**
     * 开始录音，点击“开始”按钮后调用。
     */
    @Override
    protected void start() {
        // 此处params可以打印出来，直接写到你的代码里去，最终的json一致即可。
        final Map<String, Object> params = fetchParams();

        // BaiduASRDigitalDialog的输入参数
        input = new DigitalDialogInput(myRecognizer, chainRecogListener, params);
        BaiduASRDigitalDialog.setInput(input); // 传递input信息，在BaiduASRDialog中读取,
        Intent intent = new Intent(this, BaiduASRDigitalDialog.class);

        // 修改对话框样式
        // intent.putExtra(BaiduASRDigitalDialog.PARAM_DIALOG_THEME, BaiduASRDigitalDialog.THEME_ORANGE_DEEPBG);

        running = true;
        startActivityForResult(intent, 2);
        btn.setEnabled(false);
    }

    private void addChatTranslate(String message){
        ChatTranslate chatTranslate = new ChatTranslate();
        chatTranslate.setMessageId(UUID.randomUUID().toString());
        chatTranslate.setSourceContent(message);
        adapter.addData(chatTranslate);
        adapter.notifyDataSetChanged();
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                listView.smoothScrollToPosition(adapter.getCount());
            }
        });
        String language = SharePreferceTool.getInstance(ActivityRegTranslateDialog.this).getString(SharePreferceTool.DeaufaltTranslateLanguage,"英语");
        LanguageType languageType = LanguageType.getName(language);
        httpTaskUtil.googleTranslate(message, "auto", languageType!= null ? languageType.getType() : "en");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        running = false;
        Log.i(TAG, "requestCode" + requestCode);
        if (requestCode == 2) {
            String message = "对话框的识别结果：";
            if (resultCode == RESULT_OK) {
                ArrayList results = data.getStringArrayListExtra("results");
                if (results != null && results.size() > 0) {
                    message = (String) results.get(0);

                    stringBuffer.append(message).append("\n");
//                    txtLog.setText(stringBuffer.toString());

                    addChatTranslate(message);
                }
            } else {
                message = "没有结果";
            }
//            MyLogger.info(stringBuffer.toString());
        }
    }

    @Override
    protected void onPause() {
        Log.i(TAG, "onPause");
        super.onPause();
//        if (!running) {
//            myRecognizer.release();
//            finish();
//        }
    }

    private Handler mHandler = new Handler();

    private Handler mainHandler = new Handler() {
            /*
             * @param msg
             */
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            handle(msg);
        }
    };

    protected void handle(Message msg) {
        int what = msg.what;
        switch (what) {
            case PRINT:
//                print(msg);
                break;
            case UI_CHANGE_INPUT_TEXT_SELECTION:
//                if (msg.arg1 <= mInput.getText().length()) {
//                    mInput.setSelection(0, msg.arg1);
//                }
                break;
            case UI_CHANGE_SYNTHES_TEXT_SELECTION:
//                SpannableString colorfulText = new SpannableString(mInput.getText().toString());
//                if (msg.arg1 <= colorfulText.toString().length()) {
//                    colorfulText.setSpan(new ForegroundColorSpan(Color.GRAY), 0, msg.arg1, Spannable
//                            .SPAN_EXCLUSIVE_EXCLUSIVE);
//                    mInput.setText(colorfulText);
//                }
                break;
            case INIT_SUCCESS:
                Toast.makeText(ActivityRegTranslateDialog.this,"語音合成初始化成功",Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    /**
     * 初始化引擎，需要的参数均在InitConfig类里
     * <p>
     * DEMO中提供了3个SpeechSynthesizerListener的实现
     * MessageListener 仅仅用log.i记录日志，在logcat中可以看见
     * UiMessageListener 在MessageListener的基础上，对handler发送消息，实现UI的文字更新
     * FileSaveListener 在UiMessageListener的基础上，使用 onSynthesizeDataArrived回调，获取音频流
     */
    protected void initialTts() {
        LoggerProxy.printable(true); // 日志打印在logcat中
        // 设置初始化参数
        // 此处可以改为 含有您业务逻辑的SpeechSynthesizerListener的实现类
        SpeechSynthesizerListener listener = new UiMessageListener(mainHandler);

        Map<String, String> params = getParams();


        // appId appKey secretKey 网站上您申请的应用获取。注意使用离线合成功能的话，需要应用中填写您app的包名。包名在build.gradle中获取。
        InitConfig initConfig = new InitConfig(appId, appKey, secretKey, ttsMode, params, listener);

        // 如果您集成中出错，请将下面一段代码放在和demo中相同的位置，并复制InitConfig 和 AutoCheck到您的项目中
        // 上线时请删除AutoCheck的调用
        AutoCheck.getInstance(getApplicationContext()).check(initConfig, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 100) {
                    AutoCheck autoCheck = (AutoCheck) msg.obj;
                    synchronized (autoCheck) {
                        String message = autoCheck.obtainDebugMessage();
//                        toPrint(message); // 可以用下面一行替代，在logcat中查看代码
                        // Log.w("AutoCheckMessage", message);
                    }
                }
            }

        });
        synthesizer = new NonBlockSyntherizer(this, initConfig, mainHandler); // 此处可以改为MySyntherizer 了解调用过程
    }


    /**
     * 合成的参数，可以初始化时填写，也可以在合成前设置。
     *
     * @return
     */
    protected Map<String, String> getParams() {
        Map<String, String> params = new HashMap<String, String>();
        // 以下参数均为选填
        // 设置在线发声音人： 0 普通女声（默认） 1 普通男声 2 特别男声 3 情感男声<度逍遥> 4 情感儿童声<度丫丫>
        params.put(SpeechSynthesizer.PARAM_SPEAKER, "0");
        // 设置合成的音量，0-9 ，默认 5
        params.put(SpeechSynthesizer.PARAM_VOLUME, "9");
        // 设置合成的语速，0-9 ，默认 5
        params.put(SpeechSynthesizer.PARAM_SPEED, "5");
        // 设置合成的语调，0-9 ，默认 5
        params.put(SpeechSynthesizer.PARAM_PITCH, "5");

        params.put(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_DEFAULT);
        // 该参数设置为TtsMode.MIX生效。即纯在线模式不生效。
        // MIX_MODE_DEFAULT 默认 ，wifi状态下使用在线，非wifi离线。在线状态下，请求超时6s自动转离线
        // MIX_MODE_HIGH_SPEED_SYNTHESIZE_WIFI wifi状态下使用在线，非wifi离线。在线状态下， 请求超时1.2s自动转离线
        // MIX_MODE_HIGH_SPEED_NETWORK ， 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线
        // MIX_MODE_HIGH_SPEED_SYNTHESIZE, 2G 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线

        // 离线资源文件， 从assets目录中复制到临时目录，需要在initTTs方法前完成
        OfflineResource offlineResource = createOfflineResource(offlineVoice);
        // 声学模型文件路径 (离线引擎使用), 请确认下面两个文件存在
        params.put(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, offlineResource.getTextFilename());
        params.put(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE,
                offlineResource.getModelFilename());
        return params;
    }


    protected OfflineResource createOfflineResource(String voiceType) {
        OfflineResource offlineResource = null;
        try {
            offlineResource = new OfflineResource(this, voiceType);
        } catch (IOException e) {
            // IO 错误自行处理
            e.printStackTrace();
//            toPrint("【error】:copy files from assets failed." + e.getMessage());
        }
        return offlineResource;
    }

    /**
     * speak 实际上是调用 synthesize后，获取音频流，然后播放。
     * 获取音频流的方式见SaveFileActivity及FileSaveListener
     * 需要合成的文本text的长度不能超过1024个GBK字节。
     */
    private void speak(String text) {
//        mShowText.setText("");
//        String text = mInput.getText().toString();
//        // 需要合成的文本text的长度不能超过1024个GBK字节。
//        if (TextUtils.isEmpty(mInput.getText())) {
//            text = "百度语音，面向广大开发者永久免费开放语音合成技术。";
//            mInput.setText(text);
//        }
        // 合成前可以修改参数：
        // Map<String, String> params = getParams();
        // synthesizer.setParams(params);
        int result = synthesizer.speak(text);
        checkResult(result, "speak");
    }


//    /**
//     * 合成但是不播放，
//     * 音频流保存为文件的方法可以参见SaveFileActivity及FileSaveListener
//     */
//    private void synthesize() {
//        mShowText.setText("");
//        String text = this.mInput.getText().toString();
//        if (TextUtils.isEmpty(mInput.getText())) {
//            text = "欢迎使用百度语音合成SDK,百度语音为你提供支持。";
//            mInput.setText(text);
//        }
//        int result = synthesizer.synthesize(text);
//        checkResult(result, "synthesize");
//    }

    /**
     * 批量播放
     */
    private void batchSpeak() {
//        mShowText.setText("");
        List<Pair<String, String>> texts = new ArrayList<Pair<String, String>>();
        texts.add(new Pair<String, String>("开始批量播放，", "a0"));
        texts.add(new Pair<String, String>("123456，", "a1"));
        texts.add(new Pair<String, String>("欢迎使用百度语音，，，", "a2"));
        texts.add(new Pair<String, String>("重(chong2)量这个是多音字示例", "a3"));
        int result = synthesizer.batchSpeak(texts);
        checkResult(result, "batchSpeak");
    }


    /**
     * 切换离线发音。注意需要添加额外的判断：引擎在合成时该方法不能调用
     */
    private void loadModel(String mode) {
        offlineVoice = mode;
        OfflineResource offlineResource = createOfflineResource(offlineVoice);
//        toPrint("切换离线语音：" + offlineResource.getModelFilename());
        int result = synthesizer.loadModel(offlineResource.getModelFilename(), offlineResource.getTextFilename());
        checkResult(result, "loadModel");
    }

    private void checkResult(int result, String method) {
        if (result != 0) {
//            toPrint("error code :" + result + " method:" + method + ", 错误码文档:http://yuyin.baidu.com/docs/tts/122 ");
        }
    }


    /**
     * 暂停播放。仅调用speak后生效
     */
    private void pause() {
        int result = synthesizer.pause();
        checkResult(result, "pause");
    }

    /**
     * 继续播放。仅调用speak后生效，调用pause生效
     */
    private void resume() {
        int result = synthesizer.resume();
        checkResult(result, "resume");
    }

//    /*
//     * 停止合成引擎。即停止播放，合成，清空内部合成队列。
//     */
//    private void stop() {
//        int result = synthesizer.stop();
//        checkResult(result, "stop");
//    }


    @Override
    protected void onDestroy() {
        synthesizer.release();
        Log.i(TAG, "释放资源成功");
        super.onDestroy();
    }
}