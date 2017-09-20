package com.twtstudio.bbs.bdpqchen.bbs.forum.boards.thread;


import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.twtstudio.bbs.bdpqchen.bbs.R;
import com.twtstudio.bbs.bdpqchen.bbs.commons.base.viewholder.BaseFooterViewHolder;
import com.twtstudio.bbs.bdpqchen.bbs.commons.utils.ImageUtil;
import com.twtstudio.bbs.bdpqchen.bbs.commons.utils.IntentUtil;
import com.twtstudio.bbs.bdpqchen.bbs.commons.utils.IsUtil;
import com.twtstudio.bbs.bdpqchen.bbs.commons.utils.LogUtil;
import com.twtstudio.bbs.bdpqchen.bbs.commons.utils.StampUtil;
import com.twtstudio.bbs.bdpqchen.bbs.commons.utils.TextUtil;
import com.twtstudio.bbs.bdpqchen.bbs.commons.viewholder.TheEndViewHolder;
import com.twtstudio.bbs.bdpqchen.bbs.forum.boards.thread.model.ThreadModel;
import com.twtstudio.bbs.bdpqchen.bbs.htmltextview.GlideImageGeter;
import com.twtstudio.bbs.bdpqchen.bbs.htmltextview.HtmlTextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.twtstudio.bbs.bdpqchen.bbs.commons.support.Constants.ANONYMOUS_NAME;
import static com.twtstudio.bbs.bdpqchen.bbs.commons.support.Constants.ITEM_END;
import static com.twtstudio.bbs.bdpqchen.bbs.commons.support.Constants.ITEM_FOOTER;
import static com.twtstudio.bbs.bdpqchen.bbs.commons.support.Constants.ITEM_HEADER;
import static com.twtstudio.bbs.bdpqchen.bbs.commons.support.Constants.ITEM_JUST_HEADER;
import static com.twtstudio.bbs.bdpqchen.bbs.commons.support.Constants.ITEM_NORMAL;
import static com.twtstudio.bbs.bdpqchen.bbs.commons.support.Constants.MAX_LENGTH_POST;
import static com.twtstudio.bbs.bdpqchen.bbs.commons.support.Constants.MAX_LENGTH_QUOTE;

/**
 * Created by bdpqchen on 17-5-23.
 */

public class PostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<ThreadModel.PostBean> mPostData = new ArrayList<>();
    private int mPage = 0;
    private boolean mIsNoMore = false;

    List<ThreadModel.PostBean> getPostList() {
        return mPostData;
    }

    private OnPostClickListener mListener;

    public interface OnPostClickListener {
        void onLikeClick(int position, boolean isLike, boolean isPost);

        void onReplyClick(int position);
    }

    int getPostId(int position) {
        return mPostData.get(position).getId();
    }

    PostAdapter(Context context, OnPostClickListener listener) {
        mContext = context;
        mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == ITEM_NORMAL) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_rv_thread_post, parent, false);
            return new PostHolder(view);
        } else if (viewType == ITEM_FOOTER) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_common_footer, parent, false);
            return new BaseFooterViewHolder(view);
        } else if (viewType == ITEM_HEADER) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_rv_thread_thread, parent, false);
            return new HeaderHolder(view);
        } else if (viewType == ITEM_END) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_common_no_more, parent, false);
            return new TheEndViewHolder(view);
        } else if (viewType == ITEM_JUST_HEADER) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_common_just_header, parent, false);
            return new JustHeaderHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (mPostData != null && mPostData.size() > 0) {
            int likeColor = mContext.getResources().getColor(R.color.colorPrimaryCopy);
            int unlikeColor = mContext.getResources().getColor(R.color.colorTintIconBlack);
            if (holder instanceof HeaderHolder) {
                HeaderHolder headerHolder = (HeaderHolder) holder;
                ThreadModel.PostBean p = mPostData.get(position);
                if (IsUtil.is1(p.getAnonymous())) {
                    p.setAuthor_name(ANONYMOUS_NAME);
                    ImageUtil.loadIconAsBitmap(mContext, R.drawable.avatar_anonymous_left, headerHolder.mCivAvatarThread);
                    headerHolder.mCivAvatarThread.setOnClickListener(null);
                } else {
                    headerHolder.mCivAvatarThread.setOnClickListener(v -> {
                        startToPeople(p.getAuthor_id(), p.getAuthor_name());
                    });
                    ImageUtil.loadAvatarAsBitmapByUidWithLeft(mContext, p.getAuthor_id(), headerHolder.mCivAvatarThread);
                }
                headerHolder.mTvTitle.setText(p.getTitle());
                headerHolder.mTvUsernameThread.setText(TextUtil.getNameWithFriend(p.getAuthor_name(), p.getAuthor_nickname(), p.getFriend()));
                headerHolder.mHtvContent.setHtml(p.getContent_converted(), new GlideImageGeter(mContext, headerHolder.mHtvContent));
                headerHolder.mTvDatetimeThread.setText(TextUtil.getThreadDateTime(p.getT_create(), p.getT_modify()));
                final boolean isLiked = IsUtil.is1(p.getLiked());
                headerHolder.mTvLike.setText(String.valueOf(p.getLike()));
                headerHolder.mIvLike.setOnClickListener(v -> mListener.onLikeClick(position, !isLiked, false));
                headerHolder.mIvComment.setOnClickListener(v -> mListener.onReplyClick(position));
                if (isLiked) {
                    headerHolder.mTvLike.setTextColor(likeColor);
                    headerHolder.mIvLike.setColorFilter(likeColor, PorterDuff.Mode.SRC_IN);
                } else {
                    headerHolder.mIvLike.clearColorFilter();
                    headerHolder.mTvLike.setTextColor(unlikeColor);
                }
            } else if (holder instanceof PostHolder) {
                ThreadModel.PostBean p = mPostData.get(position);
                PostHolder h = (PostHolder) holder;
                int uid = p.getAuthor_id();
                if (IsUtil.is1(p.getAnonymous())) {
                    p.setAuthor_name(ANONYMOUS_NAME);
                    ImageUtil.loadIconAsBitmap(mContext, R.drawable.avatar_anonymous_left, h.mCivAvatarPost);
                    h.mCivAvatarPost.setOnClickListener(null);
                } else {
                    h.mCivAvatarPost.setOnClickListener(v -> {
                        startToPeople(uid, p.getAuthor_name());
                    });
                    ImageUtil.loadAvatarAsBitmapByUidWithRight(mContext, uid, h.mCivAvatarPost);
                }
                h.mTvUsernamePost.setText(TextUtil.getNameWithFriend(p.getAuthor_name(), p.getAuthor_nickname(), p.getFriend()));
                h.mTvPostDatetime.setText(StampUtil.getDatetimeByStamp(p.getT_create()));
                h.mTvFloorPost.setText(p.getFloor() + "楼");
                h.mHtvPostContent.setHtml(p.getContent_converted(), new GlideImageGeter(mContext, h.mHtvPostContent));
                final boolean isLiked = IsUtil.is1(p.getLiked());
                h.mTvLike.setText(String.valueOf(p.getLike()));
                h.mIvLike.setOnClickListener(v -> mListener.onLikeClick(position, !isLiked, true));
                h.mIvReply.setOnClickListener(v -> mListener.onReplyClick(position));
                if (isLiked) {
                    h.mTvLike.setTextColor(likeColor);
                    h.mIvLike.setColorFilter(likeColor, PorterDuff.Mode.SRC_IN);
                } else {
                    h.mIvLike.clearColorFilter();
                    h.mTvLike.setTextColor(unlikeColor);
                }
            } else if (holder instanceof BaseFooterViewHolder) {
//                LogUtil.dd("base footer view");
            } else if (holder instanceof TheEndViewHolder) {
//                LogUtil.dd("the end view");
            } else if (holder instanceof JustHeaderHolder) {
//                LogUtil.dd("just header view");
            }
        }
    }

    private void startToPeople(int uid, String username) {
        mContext.startActivity(IntentUtil.toPeople(mContext, uid, username));
    }

    @Override
    public int getItemCount() {
        if (mPostData == null || mPostData.size() == 0) {
            return 0;
        } else {
            return mPostData.size() + 1;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mPostData != null && mPostData.size() > 0) {
            if (position == 0) {
                return ITEM_HEADER;
            }
            if (mPostData.size() == 1) {
                return ITEM_JUST_HEADER;
            }
            if (position + 1 == getItemCount()) {
                if (getItemCount() < (mPage) * MAX_LENGTH_POST + 1) {
                    return ITEM_END;
                }
                if (mIsNoMore) {
                    mIsNoMore = false;
                    return ITEM_END;
                }
                return ITEM_FOOTER;
            } else {
                return ITEM_NORMAL;
            }
        }
        return ITEM_NORMAL;
    }

    void updateThreadPost(List<ThreadModel.PostBean> postList, int page) {
        mPage = page + 1;
        if (postList == null || postList.size() == 0) {
            mIsNoMore = true;
        }
        if (postList != null) {
            mPostData.addAll(postList);
        }
        notifyDataSetChanged();

    }

    public void refreshList(List<ThreadModel.PostBean> model) {
        mPostData.removeAll(mPostData);
        mPostData.addAll(model);
        notifyDataSetChanged();
    }

    void refreshThisPage(List<ThreadModel.PostBean> postList, int page) {
        LogUtil.dd("refreshThisPage()");
        LogUtil.dd(String.valueOf(mPostData.size()));
        if (page == 0) {
            mPostData.removeAll(mPostData);
        } else {
            for (int i = page * MAX_LENGTH_POST; i < mPostData.size(); i++) {
                LogUtil.dd(String.valueOf(mPostData.size()));
                mPostData.remove(i);
                LogUtil.dd(String.valueOf(mPostData.size()));
            }
        }
        mPostData.addAll(postList);
        notifyDataSetChanged();
        LogUtil.dd(String.valueOf(mPostData.size()));


    }

    void likeItem(int position, boolean isLike) {
        int add = 1;
        int liked = 1;
        if (!isLike) {
            add = -1;
            liked = 0;
        }
        ThreadModel.PostBean entity = mPostData.get(position);
        entity.setLike(entity.getLike() + add);
        entity.setLiked(liked);
        mPostData.set(position, entity);
        notifyItemChanged(position);
    }

    String comment2reply(int postPosition, String content) {
        ThreadModel.PostBean post = mPostData.get(postPosition);
        String beforeCommendContent = post.getContent();
        String cut = cutTwoQuote(beforeCommendContent);
        String added = addTwoQuote(cut);
        content = content +
                "\n> 回复 #" +
                post.getFloor() + " " +
                getAuthorName(postPosition) +
                " :\n> \n> " +
                added;
        LogUtil.dd("content final", content);
        return content;
    }

    //添加两层的引用并截断1层 和 2层太长的部分
    private String addTwoQuote(String str0) {
        String key = "> ";
        if (str0.contains(key)) {
            str0 = str0.replaceAll("\n> \n>", "\n> ");
            int p = str0.indexOf(key);
            String start = str0.substring(0, p);
            start = cutIfTooLong(start);
            start = start.replaceAll("\n", "\n> ");
            String end = str0.substring(p + 2, str0.length());
            end = cutIfTooLong(end);
            end = end.replaceAll("> ", "> > ");
            str0 = start + "\n> >" + end;
        } else {
            str0 = cutIfTooLong(str0);
            str0 = str0.replaceAll("\n", "\n> ");
        }
        return str0;
    }

    private String cutIfTooLong(String s) {
        if (s.length() > MAX_LENGTH_QUOTE) {
            return s.substring(0, MAX_LENGTH_QUOTE) + "...";
        }
        return s;
    }

    //去掉最后面的两层的引用
    private String cutTwoQuote(String str0) {
        StringBuilder strNew = new StringBuilder();
        String key = "> > ";
        if (str0.contains(key)) {
            //去掉末尾的\\n
            int i = str0.indexOf(key);
            str0 = str0.substring(0, i);
            String strStart = str0.substring(0, i - 3);
            String strEnd = str0.substring(str0.length() - 3, str0.length());
            strEnd = strEnd.replace("\n", "");
            str0 = strStart + strEnd;
        }
        strNew.append(str0);
        return strNew.toString();
    }

    private String getAuthorName(int position) {
        int uid = 0;
        if (mPostData != null && mPostData.size() > 0) {
            uid = mPostData.get(position).getAuthor_id();
        }
        if (uid == 0) {
            return "匿名用户";
        } else {
            if (mPostData.get(position) != null) {
                return mPostData.get(position).getAuthor_name();
            }
        }
        return ".";
    }

    String getDynamicHint(int postPosition) {
        String hint;
        if (postPosition == 0) {
            hint = "评论帖主 " + getAuthorName(0);
        } else {
            ThreadModel.PostBean post = mPostData.get(postPosition);
            hint = "回复 " + post.getFloor() + "楼 " + getAuthorName(postPosition);
        }
        return hint;
    }

    static class PostHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.civ_avatar_post)
        CircleImageView mCivAvatarPost;
        @BindView(R.id.tv_username_post)
        TextView mTvUsernamePost;
        @BindView(R.id.tv_post_datetime)
        TextView mTvPostDatetime;
        @BindView(R.id.tv_floor_post)
        TextView mTvFloorPost;
        @BindView(R.id.htv_post_content)
        HtmlTextView mHtvPostContent;
        @BindView(R.id.tv_post_like)
        TextView mTvLike;
        @BindView(R.id.iv_post_reply)
        ImageView mIvReply;
        @BindView(R.id.iv_post_like)
        ImageView mIvLike;

        PostHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    static class HeaderHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.civ_avatar_thread)
        CircleImageView mCivAvatarThread;
        @BindView(R.id.tv_username_thread)
        TextView mTvUsernameThread;
        @BindView(R.id.tv_datetime_thread)
        TextView mTvDatetimeThread;
        @BindView(R.id.tv_title)
        TextView mTvTitle;
        @BindView(R.id.htv_content)
        HtmlTextView mHtvContent;
        @BindView(R.id.tv_thread_like)
        TextView mTvLike;
        @BindView(R.id.iv_thread_like)
        ImageView mIvLike;
        @BindView(R.id.iv_thread_comment)
        ImageView mIvComment;

        HeaderHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class JustHeaderHolder extends RecyclerView.ViewHolder {
        JustHeaderHolder(View view) {
            super(view);
        }
    }
}
