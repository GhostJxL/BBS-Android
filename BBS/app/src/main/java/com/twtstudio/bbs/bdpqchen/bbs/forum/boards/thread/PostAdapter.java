package com.twtstudio.bbs.bdpqchen.bbs.forum.boards.thread;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.twtstudio.bbs.bdpqchen.bbs.R;
import com.twtstudio.bbs.bdpqchen.bbs.commons.utils.ImageUtil;
import com.twtstudio.bbs.bdpqchen.bbs.commons.utils.LogUtil;
import com.twtstudio.bbs.bdpqchen.bbs.commons.utils.StampUtil;
import com.twtstudio.retrox.bbcode.BBCodeParse;

import org.sufficientlysecure.htmltextview.GlideImageGeter;
import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by bdpqchen on 17-5-23.
 */

public class PostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_POST = 0;
    private static final int TYPE_THREAD = 1;

    private Context mContext;
    private ThreadModel.ThreadBean mThreadData = new ThreadModel.ThreadBean();
    private List<ThreadModel.PostBean> mPostData = new ArrayList<>();

    public PostAdapter(Context context) {
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == TYPE_POST) {
            LogUtil.dd("view = normal");
            view = LayoutInflater.from(mContext).inflate(R.layout.item_rv_thread_post, parent, false);
            return new ViewHolder(view);
        } else if (viewType == TYPE_THREAD) {
            LogUtil.dd("view = header");
            view = LayoutInflater.from(mContext).inflate(R.layout.item_rv_thread_thread, parent, false);
            return new HeaderHolder(view);
        }
        view = LayoutInflater.from(mContext).inflate(R.layout.item_rv_thread_thread, parent, false);
        return new HeaderHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (mThreadData != null) {
            if (holder instanceof HeaderHolder) {
                HeaderHolder headerHolder = (HeaderHolder) holder;
                headerHolder.mTvUsernameThread.setText(mThreadData.getAuthor_nickname());
                headerHolder.mTvTitle.setText(mThreadData.getTitle());
                headerHolder.mTvDatetimeThread.setText(StampUtil.getDatetimeByStamp(mThreadData.getT_create()));
                ImageUtil.loadAvatarAsBitmapByUid(mContext, mThreadData.getAuthor_id(), headerHolder.mCivAvatarThread);
                // TODO: 17-5-26 Level is not set
                String str = BBCodeParse.bbcode2Html(mThreadData.getContent());
                headerHolder.mHtvContent.setHtml(str, new GlideImageGeter(headerHolder.mHtvContent.getContext(), headerHolder.mHtvContent));
            }

            if (mPostData != null && mPostData.size() > 0) {
                if (holder instanceof ViewHolder) {
                    ThreadModel.PostBean p = mPostData.get(position - 1);
                    ViewHolder h = (ViewHolder) holder;
                    ImageUtil.loadAvatarAsBitmapByUid(mContext, p.getAuthor_id(), h.mCivAvatarPost);
                    h.mTvNicknamePost.setText(p.getAuthor_nickname());
                    h.mTvPostDatetime.setText(StampUtil.getDatetimeByStamp(p.getT_create()));
                    h.mTvFloorPost.setText(p.getFloor() + "楼");
                    String htmlStr = BBCodeParse.bbcode2Html(p.getContent());
                    LogUtil.dd(htmlStr);
                    h.mTvPostContent.setHtml(htmlStr, new GlideImageGeter(h.mTvPostContent.getContext(), h.mTvPostContent));

                    // TODO: 17-5-23 是否收藏的判定
                }
            }

        }
    }

    @Override
    public int getItemViewType(int position) {
//        LogUtil.dd("position", String.valueOf(position));
        if (position > 0) {
            return TYPE_POST;
        } else {
            return TYPE_THREAD;
        }
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (mThreadData != null) {
            count++;
        }
        if (mPostData != null && mPostData.size() != 0) {
            count += mPostData.size();
        }
        return count;
    }

    public void setPostData(List<ThreadModel.PostBean> postData) {
        mPostData = postData;
        notifyDataSetChanged();
    }

    public void setThreadData(ThreadModel.ThreadBean threadData) {
        mThreadData = threadData;
        notifyDataSetChanged();
    }

    public void clearData(ThreadModel model){
        mThreadData = null;
        mPostData.clear();
//        mPostData.removeAll(mPostData);
//        mPostData.addAll(model.getPost());
//        mThreadData = model.getThread();
//        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.civ_avatar_post)
        CircleImageView mCivAvatarPost;
        @BindView(R.id.tv_nickname_post)
        TextView mTvNicknamePost;
        @BindView(R.id.tv_post_datetime)
        TextView mTvPostDatetime;
        @BindView(R.id.tv_post_content)
        HtmlTextView mTvPostContent;
        @BindView(R.id.tv_floor_post)
        TextView mTvFloorPost;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

        }
    }

    static class HeaderHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.civ_avatar_thread)
        CircleImageView mCivAvatarThread;
        @BindView(R.id.tv_username_thread)
        TextView mTvUsernameThread;
        @BindView(R.id.tv_level_thread)
        TextView mTvLevelThread;
        @BindView(R.id.tv_datetime_thread)
        TextView mTvDatetimeThread;
        @BindView(R.id.tv_title)
        TextView mTvTitle;
        @BindView(R.id.htv_content)
        HtmlTextView mHtvContent;

        HeaderHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


}
