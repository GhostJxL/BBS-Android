package com.twtstudio.bbs.bdpqchen.bbs.commons.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bdpqchen on 17-4-27.
 */

public abstract class BaseAdapter<T> extends RecyclerView.Adapter<BaseViewHolder> {

    protected Context mContext;
    protected List<T> mDataSet = new ArrayList<>();
    private boolean isShowFooter = false;
    private boolean isShowHeader = false;

    public BaseAdapter(Context context){
        this.mContext = context;
    }

    @Override
    public int getItemCount() {

        if (isShowFooter){

            if (mDataSet == null || mDataSet.size() == 0){
                return 0;
            }else{
                return mDataSet.size() + 1;
            }
        }
        return mDataSet.size();
    }

    public void refreshList(List<T> items){
        mDataSet.clear();
        mDataSet.addAll(items);
        setShowFooter(false);
        notifyDataSetChanged();
    }

    public void addList(List<T> items){
        mDataSet.addAll(items);
        notifyDataSetChanged();
    }


    public boolean isShowFooter() {
        return isShowFooter;
    }

    public void setShowFooter(boolean showFooter) {
        isShowFooter = showFooter;
    }

    public boolean isShowHeader() {
        return isShowHeader;
    }

    public void setShowHeader(boolean showHeader) {
        isShowHeader = showHeader;
    }

    public void clearAll(){
        mDataSet.clear();
    }
}
