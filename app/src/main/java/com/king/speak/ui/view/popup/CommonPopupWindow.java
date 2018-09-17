package com.king.speak.ui.view.popup;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.king.speak.R;
import com.king.speak.ui.activity.ActivityRegTranslateDialog;
import com.king.speak.ui.adapter.PopTranslateAdapter;
import com.king.speak.ui.bean.LanguageType;
import com.king.speak.util.SharePreferceTool;

import java.util.Arrays;

/**
 * Created by maesinfo on 2018/9/17.
 */

public abstract class CommonPopupWindow {
    protected Context context;
    protected View contentView;
    protected PopupWindow mInstance;

    private ListView listview_popup;
    private PopTranslateAdapter popTranslateAdapter;

    public CommonPopupWindow(Context c, int layoutRes, int w, int h) {
        context=c;
        contentView= LayoutInflater.from(c).inflate(layoutRes, null, false);
        listview_popup = (ListView)contentView.findViewById(R.id.listview_popup);
        popTranslateAdapter = new PopTranslateAdapter(context);
        listview_popup.setAdapter(popTranslateAdapter);
        listview_popup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mInstance != null){
                    mInstance.dismiss();
                    String language = popTranslateAdapter.getItem(position);
                    SharePreferceTool.getInstance(context).setCache(SharePreferceTool.DeaufaltTranslateLanguage,language);
//                    Toast.makeText(context,language,Toast.LENGTH_SHORT).show();
                    initEvent();
                }
            }
        });
        String[] arrayTranslate = context.getResources().getStringArray(R.array.array_popup_translate);
//        popTranslateAdapter.setData(Arrays.asList(arrayTranslate));
        popTranslateAdapter.setData(LanguageType.getLanguageName());
        popTranslateAdapter.notifyDataSetChanged();
        initView();
        initEvent();
        mInstance=new PopupWindow(contentView, w, h, true);
        initWindow();
    }

    public View getContentView() { return contentView; }
    public PopupWindow getPopupWindow() { return mInstance; }

    protected abstract void initView();
    protected abstract void initEvent();

    protected void initWindow() {
        mInstance.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mInstance.setOutsideTouchable(true);
        mInstance.setTouchable(true);
    }

    public void showBashOfAnchor(View anchor, LayoutGravity layoutGravity, int xmerge, int ymerge) {
        int[] offset=layoutGravity.getOffset(anchor, mInstance);
        mInstance.showAsDropDown(anchor, offset[0]+xmerge, offset[1]+ymerge);
    }

    public void showAsDropDown(View anchor, int xoff, int yoff) {
        mInstance.showAsDropDown(anchor, xoff, yoff);
    }

    public void showAtLocation(View parent, int gravity, int x, int y) {
        mInstance.showAtLocation(parent, gravity, x, y);
    }
}
