package com.twtstudio.bbs.bdpqchen.bbs.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;

import com.jaeger.library.StatusBarUtil;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.BottomBarTab;
import com.twtstudio.bbs.bdpqchen.bbs.R;
import com.twtstudio.bbs.bdpqchen.bbs.auth.login.LoginActivity;
import com.twtstudio.bbs.bdpqchen.bbs.commons.base.BaseActivity;
import com.twtstudio.bbs.bdpqchen.bbs.commons.manager.ActivityManager;
import com.twtstudio.bbs.bdpqchen.bbs.commons.utils.AuthUtil;
import com.twtstudio.bbs.bdpqchen.bbs.commons.utils.HandlerUtil;
import com.twtstudio.bbs.bdpqchen.bbs.commons.utils.ImageUtil;
import com.twtstudio.bbs.bdpqchen.bbs.commons.utils.LogUtil;
import com.twtstudio.bbs.bdpqchen.bbs.commons.utils.PrefUtil;
import com.twtstudio.bbs.bdpqchen.bbs.commons.utils.ResourceUtil;
import com.twtstudio.bbs.bdpqchen.bbs.commons.utils.SnackBarUtil;
import com.twtstudio.bbs.bdpqchen.bbs.forum.ForumFragment;
import com.twtstudio.bbs.bdpqchen.bbs.individual.IndividualFragment;
import com.twtstudio.bbs.bdpqchen.bbs.main.MainFragment;

import org.piwik.sdk.extra.CustomVariables;

import butterknife.BindView;
import me.yokeyword.fragmentation.SupportFragment;

import static com.twtstudio.bbs.bdpqchen.bbs.commons.support.Constants.USERNAME;


public class HomeActivity extends BaseActivity<HomePresenter> implements HomeContract.View {

    @BindView(R.id.bottom_bar)
    BottomBar mBottomBar;
//    @BindView(R.id.mask_home)
//    View mMask;

    BottomBarTab mNearBy;
    private Context mContext;
    private HomeActivity mHomeActivity;
    private SupportFragment[] mFragments = new SupportFragment[3];
    private static final int FIRST = 0;
    private static final int SECOND = 1;
    //    private static final int THIRD = 2;
    private static final int FORTH = 2;
    private int mShowingFragment = FIRST;
    private int mHidingFragment = FIRST;
    private boolean mIsExit = false;
    // 用于判定是否自动检查更新
    private boolean isCheckedUpdate = false;

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
//        mMask.setVisibility(View.VISIBLE);
//        ObjectAnimator animator = ObjectAnimator.ofFloat(findViewById(R.id.mask_home), "alpha", 0f).setDuration(600);
//        ObjectAnimator animator = ObjectAnimator.ofFloat(findViewById(R.id.mask_home), "alpha", 0f).setDuration(600);
//        animator.setStartDelay(400);
//        animator.start();

//        SnackBarUtil.error(mActivity, "新版本");

        if (savedInstanceState == null) {
            mFragments[FIRST] = MainFragment.newInstance();
            mFragments[SECOND] = ForumFragment.newInstance();
            mFragments[FORTH] = IndividualFragment.newInstance();
//            mFragments[FORTH] = MessageFragment.newInstance();
            loadMultipleRootFragment(R.id.fl_main_container, FIRST,
                    mFragments[FIRST],
                    mFragments[SECOND],
//                    mFragments[THIRD],
                    mFragments[FORTH]);
        } else {
            mFragments[FIRST] = findFragment(MainFragment.class);
            mFragments[SECOND] = findFragment(ForumFragment.class);
//            mFragments[THIRD] = findFragment(MessageFragment.class);
            mFragments[FORTH] = findFragment(IndividualFragment.class);
        }

        mNearBy = mBottomBar.getTabWithId(R.id.bottom_bar_tab_individual);
        mBottomBar.setOnTabSelectListener(i -> {
            LogUtil.dd("onTabSelected()", String.valueOf(i));
            if (PrefUtil.hadLogin()) {
                if (i == R.id.bottom_bar_tab_main) {
                    mShowingFragment = FIRST;
                    clearFullScreen();
                } else if (i == R.id.bottom_bar_tab_forum) {
                    mShowingFragment = SECOND;
                    clearFullScreen();
                } else if (i == R.id.bottom_bar_tab_individual) {
                    mShowingFragment = FORTH;
                    StatusBarUtil.setTranslucentForImageView(this, 0, null);

//                } else if (i == R.id.bottom_bar_tab_message){
//                    mShowingFragment = THIRD;
                }
                loadFragment();
            } else if (i == R.id.bottom_bar_tab_individual && !PrefUtil.hadLogin()) {
                startActivity(new Intent(this, LoginActivity.class));
            }
        });

        isCheckedUpdate = true;
        if (PrefUtil.thisVersionFirstOpen()){
            // TODO: 17-7-6 统一清理缓存
            ImageUtil.clearMemory(mContext);
            ImageUtil.clearDiskCache(mContext);
            PrefUtil.setIsThisVersionFirstOpen(false);
            PrefUtil.setIsSimpleBoardList(true);
        }

        pkTracker();
        //startActivity(new Intent(this, TestActivity.class));
    }

    private void pkTracker(){
        getTrackerHelper().screen("").title("首页").with(getTracker());
        getTrackerHelper().visitVariables(10, "OS version", "test var, android 5.0").screen("").title("test android variables").with(getTracker());
        CustomVariables variables = new CustomVariables();
        variables.put(6, "android", "test custom var");
        variables.put(12, "OS version", "test custom var version");
        getTrackerHelper().visitVariables(variables).screen("test CustomVariables").title("test CustomVariables").with(getTracker());
    }

    private void clearFullScreen() {
        StatusBarUtil.setColor(this, ResourceUtil.getColor(this, R.color.colorPrimaryDark), 0);
    }

    private void loadFragment() {
        showHideFragment(mFragments[mShowingFragment], mFragments[mHidingFragment]);
        mHidingFragment = mShowingFragment;
    }

    @Override
    public void onGotMessageCount(final int count) {
        int c = count > 0 ? count : 0;
        mNearBy.setBadgeCount(c);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mIsExit) {
                finishMe();
            } else {
                SnackBarUtil.notice(this, "再按一次退出");
                mIsExit = true;
                new Handler().postDelayed(() -> mIsExit = false, 2000);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onGetMessageFailed(String m) {
        LogUtil.dd("onGetMessageFailed()");
        if (PrefUtil.isNoAccountUser()) {
            return;
        }
        if (m.contains("token") || m.contains("UID") || m.contains("过期") || m.contains("无效")) {
            SnackBarUtil.error(mActivity, "当前账户的登录信息已过期，请重新登录", true);
            HandlerUtil.postDelay(() -> {
                AuthUtil.logout();
                Intent intent = new Intent(mActivity, LoginActivity.class);
                intent.putExtra(USERNAME, PrefUtil.getAuthUsername());
                startActivity(intent);
                ActivityManager.getActivityManager().finishAllActivity();
            }, 3000);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.dd("home onResume", "getUnreadMessage");
        if (mPresenter != null) {
            mPresenter.getUnreadMessageCount();
        }
    }


}
