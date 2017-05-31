package com.twtstudio.bbs.bdpqchen.bbs.home;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.pgyersdk.javabean.AppBean;
import com.pgyersdk.update.PgyUpdateManager;
import com.pgyersdk.update.UpdateManagerListener;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.BottomBarTab;
import com.twtstudio.bbs.bdpqchen.bbs.R;
import com.twtstudio.bbs.bdpqchen.bbs.auth.login.LoginActivity;
import com.twtstudio.bbs.bdpqchen.bbs.commons.base.BaseActivity;
import com.twtstudio.bbs.bdpqchen.bbs.commons.support.Constants;
import com.twtstudio.bbs.bdpqchen.bbs.commons.utils.LogUtil;
import com.twtstudio.bbs.bdpqchen.bbs.commons.utils.PrefUtil;
import com.twtstudio.bbs.bdpqchen.bbs.commons.utils.SnackBarUtil;
import com.twtstudio.bbs.bdpqchen.bbs.forum.ForumFragment;
import com.twtstudio.bbs.bdpqchen.bbs.individual.IndividualFragment;
import com.twtstudio.bbs.bdpqchen.bbs.individual.model.IndividualInfoModel;
import com.twtstudio.bbs.bdpqchen.bbs.main.MainFragment;

import butterknife.BindView;
import me.yokeyword.fragmentation.SupportFragment;

import static com.pgyersdk.update.UpdateManagerListener.startDownloadTask;


public class HomeActivity extends BaseActivity<HomePresenter> implements HomeContract.View {

    @BindView(R.id.bottom_bar)
    BottomBar mBottomBar;
    @BindView(R.id.mask_home)
    View mMask;

    MainFragment mMainFragment;
    ForumFragment mForumFragment;
    IndividualFragment mIndividualFragment = null;
    BottomBarTab mNearBy;

    private int mShowingFragment = Constants.FRAGMENT_MAIN;
    private int mHidingFragment = Constants.FRAGMENT_MAIN;
    private Context mContext;
    private HomeActivity mHomeActivity;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_main;
    }

    @Override
    protected Toolbar getToolbarView() {
        return null;
    }

    @Override
    protected boolean isShowBackArrow() {
        return false;
    }

    @Override
    protected boolean isSupportNightMode() {
        return true;
    }

    @Override
    protected void inject() {
        getActivityComponent().inject(this);
    }

    @Override
    protected Activity supportSlideBack() {
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHomeActivity = this;
        mContext = this;
        LogUtil.dd("token", PrefUtil.getAuthToken());
        // TODO: 17-5-3 非登录后跳转到这里，是否渐变
        // 登录后的渐变,
        if (!PrefUtil.isNoAccountUser()) {
            PrefUtil.setHadLogin(true);
        }
        mMask.setVisibility(View.VISIBLE);
        ObjectAnimator animator = ObjectAnimator.ofFloat(findViewById(R.id.mask_home),
                "alpha", 0f).setDuration(600);
        animator.setStartDelay(400);
        animator.start();
//        }

        mMainFragment = MainFragment.newInstance();
        mForumFragment = ForumFragment.newInstance();
        mIndividualFragment = IndividualFragment.newInstance();
        loadMultipleRootFragment(R.id.fl_main_container, 0, mMainFragment, mForumFragment, mIndividualFragment);
        mNearBy = mBottomBar.getTabWithId(R.id.bottom_bar_tab_individual);

        mBottomBar.setOnTabSelectListener(i -> {
            LogUtil.dd("onTabSelected()");
            if (PrefUtil.hadLogin()) {
                if (i == R.id.bottom_bar_tab_main) {
                    mShowingFragment = Constants.FRAGMENT_MAIN;
                } else if (i == R.id.bottom_bar_tab_forum) {
                    mShowingFragment = Constants.FRAGMENT_FORUM;
                } else if (i == R.id.bottom_bar_tab_individual) {
                    mShowingFragment = Constants.FRAGMENT_INDIVIDUAL;
                }
                loadFragment();
            } else if (i == R.id.bottom_bar_tab_individual && !PrefUtil.hadLogin()) {
                startActivity(new Intent(this, LoginActivity.class));
            }
        });

//        if (mIndividualFragment.isVisible()){
        // TODO: 17-5-22 解决夜间模式View的空指针问题
        if (PrefUtil.hadLogin()) {
            mPresenter.initIndividualInfo();
        }
        LogUtil.dd("send a net request");
//        }
//        mPresenter.checkUpdate(1);

        PgyUpdateManager.register(this, "9981",
                new UpdateManagerListener() {
                    @Override
                    public void onNoUpdateAvailable() {
                        LogUtil.dd("not update available");
                    }

                    @Override
                    public void onUpdateAvailable(final String result) {

                        // 将新版本信息封装到AppBean中
                        final AppBean appBean = getAppBeanFromString(result);
                        SnackBarUtil.notice(mHomeActivity, "这是一个新版本 22222", true);
                        new AlertDialog.Builder(HomeActivity.this)
                                .setTitle("新版本更新")
                                .setMessage(appBean.getReleaseNote())
                                .setNegativeButton("立即下载", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        hasPermission(appBean);
                                        LogUtil.d(appBean.getDownloadURL());
                                    }
                                })
                                .show();
                    }

                });

    }

    private void startDownload(AppBean appBean) {
        startDownloadTask(HomeActivity.this, appBean.getDownloadURL());
    }

    private void hasPermission(AppBean appBean) {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        LogUtil.dd("onPermissionGranted");
                        startDownload(appBean);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        LogUtil.dd("onPermissionDenied");
                        SnackBarUtil.error(mHomeActivity, "请赋予我读取存储器内容的权限");
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        SnackBarUtil.error(mHomeActivity, "请赋予我读取存储器内容的权限");
                    }
                })
                .check();

    }

    private void loadFragment() {

        showHideFragment(getTargetFragment(mShowingFragment), getTargetFragment(mHidingFragment));
        mHidingFragment = mShowingFragment;
    }

    private SupportFragment getTargetFragment(int type) {
        switch (type) {
            case Constants.FRAGMENT_MAIN:
                return mMainFragment;
            case Constants.FRAGMENT_FORUM:
                return mForumFragment;
            case Constants.FRAGMENT_INDIVIDUAL:
                return mIndividualFragment;
        }
        return mMainFragment;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PgyUpdateManager.unregister();
    }

    @Override
    public void showUpdateDialog(int versionCode) {

    }

    @Override
    public void showIndividualInfo(IndividualInfoModel info) {
        LogUtil.d("receive a response");
        if (info != null) {
            //设置个人信息，在IndividualFragment 里可直接获取，需判断是否为最新getIsLatestInfo()
            PrefUtil.setInfoNickname(info.getNickname());
            PrefUtil.setInfoSignature(info.getSignature());
            PrefUtil.setInfoOnline(info.getC_online());
            PrefUtil.setInfoPost(info.getC_post());
            PrefUtil.setInfoPoints(info.getPoints());
            PrefUtil.setInfoCreate(info.getT_create());
            PrefUtil.setInfoGroup(info.getGroup());
            PrefUtil.setInfoLevel(info.getLevel());
            PrefUtil.setIsLatestInfo(true);
            int unRead = info.getC_unread();
            PrefUtil.setInfoUnread(unRead);
            LogUtil.dd(String.valueOf(unRead));
            // TODO: 17-5-10 为了测试
//            mNearBy.setBadgeCount(unRead);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        int count = PrefUtil.getInfoUnread();
        if (count > 0) {
//            mNearBy.setBadgeCount(count);
        }

    }


}
