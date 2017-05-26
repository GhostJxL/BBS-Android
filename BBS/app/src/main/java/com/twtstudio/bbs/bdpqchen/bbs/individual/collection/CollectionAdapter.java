package com.twtstudio.bbs.bdpqchen.bbs.individual.collection;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.twtstudio.bbs.bdpqchen.bbs.R;
import com.twtstudio.bbs.bdpqchen.bbs.commons.utils.ImageUtil;
import com.twtstudio.bbs.bdpqchen.bbs.commons.utils.SnackBarUtil;
import com.twtstudio.bbs.bdpqchen.bbs.individual.model.CollectionBean;
import com.twtstudio.bbs.bdpqchen.bbs.main.content.ContentActivity;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by whm on 2017/5/14.
 **/

class CollectionAdapter extends RecyclerView.Adapter {
    private Context context;
    private CollectionBean collectionBean;
    private CollectionPresenter collectionPresenter;

    CollectionAdapter(Context context, CollectionBean collectionBean, CollectionPresenter collectionPresenter) {
        this.context = context;
        this.collectionBean = collectionBean;
        this.collectionPresenter = collectionPresenter;
    }

    class CollectionViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.collection_avatar)
        CircleImageView collection_avatar;
        @BindView(R.id.collection_author_name)
        TextView collection_author_name;
        @BindView(R.id.collection_delete)
        ImageView collection_delete;
        @BindView(R.id.collection_summary)
        TextView collection_summary;
        @BindView(R.id.collection_time)
        TextView collection_time;

        CollectionViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_collection, parent, false);
        return new CollectionViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CollectionBean.DataBean data = collectionBean.data.get(position);
        String date = timeFromEpoch(data.t_create);
        CollectionViewHolder collectionViewHolder = (CollectionViewHolder) holder;
        collectionViewHolder.collection_author_name.setText(data.author_nickname);
        collectionViewHolder.collection_summary.setText(data.title);
        collectionViewHolder.collection_time.setText(date);
        collectionViewHolder.collection_delete.setOnClickListener(view -> collectionPresenter.deleteCollection(data.id));
        ImageUtil.loadAvatarAsBitmapByUid(context, data.author_id, collectionViewHolder.collection_avatar);
        collectionViewHolder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, ContentActivity.class);
            intent.putExtra("threadid", String.valueOf(data.id));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        if (collectionBean == null || collectionBean.data == null)
            return 0;
        return collectionBean.data.size();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private String timeFromEpoch(long epoch) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date(epoch * 1000L - 8L * 3600L));
    }
}
