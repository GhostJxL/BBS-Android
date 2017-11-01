package com.twtstudio.bbs.bdpqchen.bbs.main.latest;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.beta.UpgradeInfo;
import com.twtstudio.bbs.bdpqchen.bbs.R;
import com.twtstudio.bbs.bdpqchen.bbs.commons.base.BaseFragment;
import com.twtstudio.bbs.bdpqchen.bbs.commons.helper.RecyclerViewItemDecoration;
import com.twtstudio.bbs.bdpqchen.bbs.commons.utils.LogUtil;
import com.twtstudio.bbs.bdpqchen.bbs.commons.utils.PrefUtil;
import com.twtstudio.bbs.bdpqchen.bbs.commons.utils.SnackBarUtil;
import com.twtstudio.bbs.bdpqchen.bbs.individual.release.EndlessRecyclerOnScrollListener;
import com.twtstudio.bbs.bdpqchen.bbs.main.MainContract;
import com.twtstudio.bbs.bdpqchen.bbs.main.MainPresenter;
import com.twtstudio.bbs.bdpqchen.bbs.main.hot.HotEntity;

import java.util.List;

import butterknife.BindView;

/**
 * Created by bdpqchen on 17-6-5.
 */

public class LatestFragment extends BaseFragment implements MainContract.View {

    @BindView(R.id.pb_loading)
    ProgressBar mPbLoading;
    @BindView(R.id.rv_latest)
    RecyclerView mRvLatest;
    @BindView(R.id.srl_latest)
    SwipeRefreshLayout mSrlLatest;

    private LatestAdapter mAdapter;
    private boolean mRefreshing = false;
    private MainPresenter mPresenter;
    private int mPage = 0;
    private boolean mIsLoadingMore = false;

    public static LatestFragment newInstance() {
        return new LatestFragment();
    }

    @Override
    protected int getFragmentLayoutId() {
        return R.layout.fragmetn_latest;
    }

    @Override
    protected void initFragment() {
        mAdapter = new LatestAdapter(getActivity());
        mPresenter = new MainPresenter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRvLatest.setLayoutManager(linearLayoutManager);
        mRvLatest.addItemDecoration(new RecyclerViewItemDecoration(16));
        mAdapter.setCreateThread(true);
        mRvLatest.setAdapter(mAdapter);
        mSrlLatest.setColorSchemeColors(getResources().getIntArray(R.array.swipeRefreshColors));
        mSrlLatest.setOnRefreshListener(() -> {
            getDataList(0);
            mRefreshing = true;
            mSrlLatest.setRefreshing(true);

        });
        mRvLatest.addOnScrollListener(new EndlessRecyclerOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore() {
                mIsLoadingMore = true;
                getDataList(++mPage);
            }
        });

        getDataList(0);
        checkUpdate();

    }

    private void checkUpdate() {

        UpgradeInfo upgradeInfo = Beta.getUpgradeInfo();
        if (upgradeInfo != null) {
            LogUtil.dd("have a new version");
            PrefUtil.setUpdateAvailable(true);
        } else {
            LogUtil.dd("no new version");
            PrefUtil.setUpdateAvailable(false);
        }
        if (PrefUtil.isUpdateAvailable()) {
            mAdapter.setUpdateAvailable(true);
            mAdapter.addFirst(new LatestEntity());
        }
    }


    @Override
    protected MainPresenter getPresenter() {
        return mPresenter;
    }

    @Override
    public void onGetLatestList(List<LatestEntity> list) {
        if (list != null && list.size() > 0) {
            if (mIsLoadingMore) {
                mIsLoadingMore = false;
//                int size = mAdapter.getDataListSize();
                mAdapter.addList(list);
//                mRvLatest.smoothScrollToPosition(++size);
                return;
            }
            if (mRefreshing) {
                mAdapter.clearAll();
            }
            mAdapter.setCreateThread(true);
            mAdapter.addFirst(new LatestEntity());
            mAdapter.addList(list);
        }
        setRefreshing(false);
        hideLoading();
    }

    @Override
    public void onGetHotList(List<HotEntity> list) {

    }

    @Override
    public void onGotDataFailed(String msg) {
        hideLoading();
        setRefreshing(false);
        SnackBarUtil.notice(this.getActivity(), msg + "\n刷新试试");
        if (mIsLoadingMore) {
            mIsLoadingMore = false;
            mPage--;
        }
    }

    void setRefreshing(boolean b) {
        mRefreshing = b;
        if (mSrlLatest != null)
            mSrlLatest.setRefreshing(b);
    }

    private void hideLoading() {
        if (mPbLoading != null)
            mPbLoading.setVisibility(View.GONE);
    }

    public void getDataList(int page) {
        mPresenter.getLatestList(page);
    }
}
