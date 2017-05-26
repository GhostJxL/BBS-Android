package com.twtstudio.bbs.bdpqchen.bbs.main.content;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.twtstudio.bbs.bdpqchen.bbs.R;
import com.twtstudio.bbs.bdpqchen.bbs.commons.base.BaseAdapter;
import com.twtstudio.bbs.bdpqchen.bbs.commons.base.BaseViewHolder;
import com.twtstudio.bbs.bdpqchen.bbs.commons.utils.ImageUtil;
import com.twtstudio.bbs.bdpqchen.bbs.main.TimeUtils;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by zhangyulong on 5/19/17.
 */

public class ContentAdapter extends BaseAdapter<ContentModel.DataBean.PostBean> {
    public ContentAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ContentViewHolder holder = new ContentViewHolder(LayoutInflater.from(
                parent.getContext()).inflate(R.layout.activity_index_content_post_item, parent,
                false));
        return holder;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        ContentViewHolder mholder = (ContentViewHolder) holder;
        mholder.itemView.setTag(position);
        mholder.data = mDataSet.get(position);
        mholder.title.setText(mholder.data.getContent());
        mholder.author.setText(mholder.data.getAuthor_name());
        mholder.create_time.setText(TimeUtils.getStandardDate(mholder.data.getT_create()));
        ImageUtil.loadAvatarAsBitmapByUid(mContext, mholder.data.getAuthor_id(), mholder.avatar);
    }

    class ContentViewHolder extends BaseViewHolder {
        @BindView(R.id.main_collection_delete)
        ImageView collection;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.author)
        TextView author;
        @BindView(R.id.create_time)
        TextView create_time;
        @BindView(R.id.item_civ_avatar)
        CircleImageView avatar;
        ContentModel.DataBean.PostBean data;

        ContentViewHolder(View itemView) {
            super(itemView);
        }


    }
}
