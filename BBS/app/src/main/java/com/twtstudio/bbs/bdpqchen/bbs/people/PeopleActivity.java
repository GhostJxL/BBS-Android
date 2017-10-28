package com.twtstudio.bbs.bdpqchen.bbs.people;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jaeger.library.StatusBarUtil;
import com.twtstudio.bbs.bdpqchen.bbs.R;
import com.twtstudio.bbs.bdpqchen.bbs.commons.base.BaseActivity;
import com.twtstudio.bbs.bdpqchen.bbs.commons.helper.RecyclerViewItemDecoration;
import com.twtstudio.bbs.bdpqchen.bbs.commons.model.BaseModel;
import com.twtstudio.bbs.bdpqchen.bbs.commons.utils.DialogUtil;
import com.twtstudio.bbs.bdpqchen.bbs.commons.utils.ImageUtil;
import com.twtstudio.bbs.bdpqchen.bbs.commons.utils.IntentUtil;
import com.twtstudio.bbs.bdpqchen.bbs.commons.utils.SnackBarUtil;
import com.twtstudio.bbs.bdpqchen.bbs.commons.utils.TextUtil;
import com.twtstudio.bbs.bdpqchen.bbs.commons.utils.TransUtil;
import com.twtstudio.bbs.bdpqchen.bbs.commons.utils.UrlUtil;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.twtstudio.bbs.bdpqchen.bbs.commons.support.Constants.UID;

/**
 * Created by bdpqchen on 17-7-3.
 */

public class PeopleActivity extends BaseActivity implements PeopleContract.View {
    @BindView(R.id.iv_bg)
    ImageView mIvBg;
    @BindView(R.id.civ_avatar)
    CircleImageView mCivAvatar;
    @BindView(R.id.rl_avatar)
    RelativeLayout mRlAvatar;
    @BindView(R.id.tv_nickname)
    TextView mTvNickname;
    @BindView(R.id.tv_signature)
    TextView mTvSignature;
    @BindView(R.id.tv_points)
    TextView mTvPoints;
    @BindView(R.id.tv_post_count)
    TextView mTvPostCount;
    @BindView(R.id.info_past_day)
    TextView mInfoPastDay;
    @BindView(R.id.gl_count_data)
    GridLayout mGlCountData;
    @BindView(R.id.ll_nickname)
    LinearLayout mLlNickname;
    @BindView(R.id.ll_data)
    LinearLayout mLlData;
    @BindView(R.id.rv_people)
    RecyclerView mRvPeople;
    @BindView(R.id.tv_honor)
    TextView mTvHonor;
    @BindView(R.id.toolbar_behavior)
    Toolbar mToolbar;
    @BindView(R.id.nested_scroll)
    NestedScrollView mNestedScroll;
    private int mUid = 0;
    private PeopleAdapter mAdapter;
    private String mName = "";
    private String mConfirmMsg = "";
    private PeoplePresenter mPresenter;
    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_people;
    }

    @Override
    protected Toolbar getToolbarView() {
        mToolbar.getBackground().mutate().setAlpha(0);
        mToolbar.setTitle("");
        return mToolbar;
    }

    @Override
    protected PeoplePresenter getPresenter() {
        return mPresenter;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new PeoplePresenter(this);
        Intent intent = getIntent();
        mUid = intent.getIntExtra(UID, 0);
//        mName = intent.getStringExtra(USERNAME);
        mToolbar.setTitle(mName);
        StatusBarUtil.setTranslucentForImageView(this, 0, null);
//        ViewCompat.setTransitionName(mCivAvatar, getString(R.string.share_avatar));
        TransUtil.setSharedElementsInterpolator(getWindow());

        ImageUtil.loadAvatarAsBitmapByUidWithLeft(this, mUid, mCivAvatar);
        mPresenter.getUserInfo(mUid);
        ImageUtil.loadBgByUid(this, mUid, mIvBg);
        mAdapter = new PeopleAdapter(mContext);
        LinearLayoutManager manager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mRvPeople.setLayoutManager(manager);
        mRvPeople.setAdapter(mAdapter);
        mRvPeople.addItemDecoration(new RecyclerViewItemDecoration(2));
        mRvPeople.setNestedScrollingEnabled(false);
        mNestedScroll.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            double minus = (scrollY - oldScrollY) / 1.5;
            int alpha = mToolbar.getBackground().mutate().getAlpha();
            alpha += minus;
            if (alpha > 255) {
                alpha = 255;
                mToolbar.setTitle(mName);
            } else if (alpha <= 0) {
                alpha = 0;
                mToolbar.setTitle("");
            }
            mToolbar.getBackground().mutate().setAlpha(alpha);
        });
        mCivAvatar.setOnClickListener(v -> {
            Intent intent1 = IntentUtil.toBigPhoto(mContext, UrlUtil.getAvatarUrl(mUid));
            Bundle bundle  = TransUtil.getTransOptions(mContext, mCivAvatar, R.string.share_big_photo);
            startActivity(intent1, bundle);
        });

    }

    @Override
    public void onGetUserInfo(PeopleModel model) {
        if (model != null) {
            if (model.getSignature() == null || model.getSignature().length() == 0) {
                model.setSignature(getString(R.string.default_signature));
            }
            mTvPostCount.setText(String.valueOf(model.getC_post()));
            mTvNickname.setText(TextUtil.getTwoNames(model.getName(), model.getNickname()));
            mTvSignature.setText(model.getSignature());
            mTvPoints.setText(String.valueOf(model.getPoints()));
            mInfoPastDay.setText(TextUtil.getPastDays(mContext, model.getT_create()), TextView.BufferType.SPANNABLE);
            mAdapter.addList(model.getRecent());
            mTvHonor.setText(TextUtil.getHonor(model.getPoints()));
            mName = model.getName();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_people, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_people_add:
                DialogUtil.multiLineInputDialog(mContext, "请求信息", "", (dialog, input) -> {
                    mConfirmMsg = input.toString();
                    mPresenter.addFriend(mUid, mConfirmMsg);
                });
                break;
            case R.id.action_people_letter:
                startActivity(IntentUtil.toLetter(mContext, mUid, mName));
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onGetUserInfoFailed(String m) {
        SnackBarUtil.error(this, m);
    }

    @Override
    public void onAddFriend(BaseModel model) {
        SnackBarUtil.normal(mActivity, "发送成功");
    }

    @Override
    public void onAddFriendFailed(String s) {
        SnackBarUtil.error(mActivity, s);
    }
}
