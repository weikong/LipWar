package com.king.speak.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.king.speak.R;
import com.king.speak.ui.bean.ChatTranslate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xinzhendi-031 on 2016/12/14.
 */
public class ChatTranslateAdapter extends BaseAdapter {

    private Context mContext;
    private List<ChatTranslate> datas = new ArrayList<>();

    public ChatTranslateAdapter(Context context) {
        this.mContext = context;
    }

    public List<ChatTranslate> getDatas() {
        return datas;
    }

    public void setData(List<ChatTranslate> list) {
        if (list != null) {
            datas.clear();
            datas.addAll(list);
        }
    }

    public void addData(ChatTranslate item) {
        if (item != null) {
            datas.add(item);
        }
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public ChatTranslate getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_chat_translate, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final ChatTranslate item = getItem(position);
        viewHolder.tv_content.setText(item.getSourceContent());
        viewHolder.tv_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        if (TextUtils.isEmpty(item.getTranslateContent())){
            viewHolder.ivVoice.setVisibility(View.GONE);
            viewHolder.tv_translate.setVisibility(View.GONE);
            viewHolder.tv_translate.setText(item.getTranslateContent());
        } else {
            viewHolder.ivVoice.setVisibility(View.VISIBLE);
            viewHolder.ivVoice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (voiceListener != null)
                        voiceListener.speakText(item.getTranslateContent());
                }
            });
            viewHolder.tv_translate.setVisibility(View.VISIBLE);
            viewHolder.tv_translate.setText(item.getTranslateContent());
        }
        return convertView;
    }

    private class ViewHolder {
        public ImageView ivVoice;
        public TextView tv_content,tv_translate;

        public ViewHolder(View view) {
            this.ivVoice = (ImageView)view.findViewById(R.id.iv_icon);
            this.tv_content = (TextView) view.findViewById(R.id.tv_content);
            this.tv_translate = (TextView) view.findViewById(R.id.tv_translate);
        }
    }

    public VoiceListener voiceListener;

    public void setVoiceListener(VoiceListener voiceListener) {
        this.voiceListener = voiceListener;
    }

    public interface VoiceListener{
        public void speakText(String translate);
    }
}
