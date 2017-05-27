package com.twtstudio.bbs.bdpqchen.bbs.individual.my_release;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.twtstudio.bbs.bdpqchen.bbs.R;
import com.twtstudio.bbs.bdpqchen.bbs.commons.base.BaseAdapter;
import com.twtstudio.bbs.bdpqchen.bbs.commons.base.BaseViewHolder;
import com.twtstudio.bbs.bdpqchen.bbs.commons.utils.StampUtil;

import java.util.List;

import butterknife.BindView;


/**
 * Created by Arsener on 2017/5/13.
 */

public class MyRecyclerAdapter extends BaseAdapter<MyReleaseModel> {

    private View mFooterView;
    private List<MyReleaseModel> data;

    public static int NORMAL_TYPE = 0;//without footer
    public static int FOOTER_TYPE = 1;//with footer

    public MyRecyclerAdapter(Context context, List<MyReleaseModel> data) {

        super(context);
        this.data = data;
    }

    public View getFooterView() {
        return mFooterView;
    }

    public void setFooterView(View footerView) {
        mFooterView = footerView;
        notifyItemInserted(getItemCount() - 1);
    }

    public void removeFooterView (){
        Log.d("remove", "removeFooterView: ");
        notifyItemRemoved(2);
    }

    public void clear() {
        data.clear();
    }

    public void addItems(List<MyReleaseModel> newData) {
        data.addAll(newData);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (mFooterView == null) {
            return NORMAL_TYPE;
        }
        if (position == getItemCount() - 1) {
            //最后一个,应该加载Footer
            return FOOTER_TYPE;
        }
        return NORMAL_TYPE;
    }

    @Override
    public int getItemCount() {
        // TODO Auto-generated method stub
        //return data.size();
        if (mFooterView == null) {
            return data.size();
        } else {
            return data.size() + 1;
        }
    }

    // 填充onCreateViewHolder方法返回的holder中的控件
    public void onBindViewHolder(BaseViewHolder mholder, int position) {
        // TODO Auto-generated method stub
        //holder.tv.setText(data.get(position));
        if (getItemViewType(position) == NORMAL_TYPE) {
            MyHolder holder = (MyHolder) mholder;
            holder.tv_title.setText(data.get(position).title);
            holder.tv_title.setEllipsize(TextUtils.TruncateAt.valueOf("END"));
            Typeface iconfont = Typeface.createFromAsset(mContext.getAssets(), "iconfont/iconfont.ttf");
            holder.tv_icon.setTypeface(iconfont);
            holder.tv_visit.setText(String.valueOf(data.get(position).c_post));
            holder.tv_time.setText(StampUtil.TimeStamp2Date(data.get(position).t_create));

            holder.cdv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent intent = new Intent(context, ContentActivity.class);
//                    intent.putExtra("index", data.get(position).index);
//                    context.startActivity(intent);//注意这里的context！！！
                }
            });
        }
    }

    @Override
    // 重写onCreateViewHolder方法，返回一个自定义的ViewHolder
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 填充布局
        if (mFooterView != null && viewType == FOOTER_TYPE) {
            return new FooterViewHolder(mFooterView);
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_cv_release, parent, false);
        MyHolder holder = new MyHolder(view);
        return holder;
    }

    class FooterViewHolder extends BaseViewHolder {
        public FooterViewHolder(View view) {
            super(view);
        }
    }

    class MyHolder extends BaseViewHolder {

        @BindView(R.id.tv_title)
        TextView tv_title;
        @BindView(R.id.tv_icon)
        TextView tv_icon;
        @BindView(R.id.tv_visit)
        TextView tv_visit;
        @BindView(R.id.tv_time)
        TextView tv_time;
        @BindView(R.id.cdv)
        CardView cdv;

        public MyHolder(View view) {
            super(view);

            cdv.setRadius(0);//设置图片圆角的半径大小
            cdv.setCardElevation(0);//设置阴影部分大小

        }
    }
}
