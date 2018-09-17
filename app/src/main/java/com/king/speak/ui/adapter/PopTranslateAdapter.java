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
import com.king.speak.util.SharePreferceTool;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xinzhendi-031 on 2016/12/14.
 */
public class PopTranslateAdapter extends BaseAdapter {

    private Context mContext;
    private List<String> datas = new ArrayList<>();

    public PopTranslateAdapter(Context context) {
        this.mContext = context;
    }

    public List<String> getDatas() {
        return datas;
    }

    public void setData(List<String> list) {
        if (list != null) {
            datas.clear();
            datas.addAll(list);
        }
    }

    public void addData(String item) {
        if (item != null) {
            datas.add(item);
        }
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public String getItem(int position) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_popup_translate, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tv_content.setText(getItem(position));
        String language = SharePreferceTool.getInstance(mContext).getString(SharePreferceTool.DeaufaltTranslateLanguage,"英语");
        if (TextUtils.isEmpty(language)){
            SharePreferceTool.getInstance(mContext).setCache(SharePreferceTool.DeaufaltTranslateLanguage,"英语");
        }
        if (!TextUtils.isEmpty(language) && language.equals(getItem(position))){
            viewHolder.iv_select.setVisibility(View.VISIBLE);
        } else {
            viewHolder.iv_select.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }

    private class ViewHolder {
        public TextView tv_content;
        public ImageView iv_select;

        public ViewHolder(View view) {
            this.tv_content = (TextView) view.findViewById(R.id.tv_content);
            this.iv_select = (ImageView) view.findViewById(R.id.iv_select);
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
