package com.twtstudio.bbs.bdpqchen.bbs.commons.base;

import android.content.Context;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.twtstudio.bbs.bdpqchen.bbs.commons.base.viewholder.BaseViewHolder;
import com.twtstudio.bbs.bdpqchen.bbs.commons.listener.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

import static com.twtstudio.bbs.bdpqchen.bbs.commons.support.Constants.ITEM_CREATE_THREAD;
import static com.twtstudio.bbs.bdpqchen.bbs.commons.support.Constants.ITEM_FOOTER;
import static com.twtstudio.bbs.bdpqchen.bbs.commons.support.Constants.ITEM_NORMAL;
import static com.twtstudio.bbs.bdpqchen.bbs.commons.support.Constants.ITEM_UPDATE_AVAILABLE;

/**
 * Created by bdpqchen on 17-4-27.
 */

public abstract class BaseAdapter<T> extends RecyclerView.Adapter<BaseViewHolder> implements View.OnClickListener {

    protected Context mContext;
    protected List<T> mDataSet = new ArrayList<>();
    private boolean isShowFooter = false;
    private boolean isShowHeader = false;
    private OnItemClickListener mOnItemClickListener = null;
    protected int mPage = 0;
    private boolean mCreateThread, mUpdateAvailable = false;
DiffUtil
    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取position
            mOnItemClickListener.onItemClick(v, (int) v.getTag());
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

//    public BaseAdapter(){}

    public BaseAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public int getItemCount() {
        if (isShowFooter || isShowHeader) {
            if (mDataSet == null || mDataSet.size() == 0) {
                return 0;
            } else {
                return mDataSet.size() + 1;
            }
        }
        return mDataSet.size();
    }

    public void refreshList(List<T> items) {
        mDataSet.clear();
        mDataSet.addAll(items);
//        setShowFooter(false);
        notifyDataSetChanged();
    }

    public void addList(List<T> items) {
        mDataSet.addAll(items);
        notifyDataSetChanged();
    }

    public int getDataListSize() {
        return mDataSet.size();
    }

    public void addFirst(List<T> items) {
        mDataSet.addAll(0, items);
        notifyDataSetChanged();
    }

    public void addFirst(T item) {
        mDataSet.add(0, item);
        notifyDataSetChanged();
    }

    public void addList(List<T> items, int page) {
        mPage = page;
        addList(items);
    }

    @Override
    public int getItemViewType(int position) {
        //Position = 0 and have to display an item.
        if (mUpdateAvailable && position == 0) {
            return ITEM_UPDATE_AVAILABLE;
        }
        if (mCreateThread && ((position == 1 && mUpdateAvailable) || (!mUpdateAvailable && position == 0))) {
            return ITEM_CREATE_THREAD;
        }
        if (mDataSet != null && mDataSet.size() > 0) {
            if (position + 1 == getItemCount()) {
                return ITEM_FOOTER;
            } else {
                return ITEM_NORMAL;
            }
        }
        return 0;
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

    public void clearAll() {
        mDataSet.removeAll(mDataSet);
        mDataSet.clear();
//        notifyDataSetChanged();
    }

    public void addOne(T model) {
        mDataSet.add(model);
        notifyItemInserted(mDataSet.size() - 1);
//        notifyDataSetChanged();
    }

    public void setCreateThread(boolean b) {
        this.mCreateThread = b;
    }

    public void setUpdateAvailable(boolean b) {
        this.mUpdateAvailable = b;
    }
}
