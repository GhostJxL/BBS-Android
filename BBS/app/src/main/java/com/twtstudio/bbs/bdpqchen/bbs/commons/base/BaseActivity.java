package com.twtstudio.bbs.bdpqchen.bbs.commons.base;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.jaeger.library.StatusBarUtil;
import com.oubowu.slideback.SlideBackHelper;
import com.oubowu.slideback.SlideConfig;
import com.oubowu.slideback.widget.SlideBackLayout;
import com.twtstudio.bbs.bdpqchen.bbs.R;
import com.twtstudio.bbs.bdpqchen.bbs.commons.App;
import com.twtstudio.bbs.bdpqchen.bbs.commons.di.component.ActivityComponent;
import com.twtstudio.bbs.bdpqchen.bbs.commons.di.component.DaggerActivityComponent;
import com.twtstudio.bbs.bdpqchen.bbs.commons.di.module.ActivityModule;
import com.twtstudio.bbs.bdpqchen.bbs.commons.manager.ActivityManager;
import com.twtstudio.bbs.bdpqchen.bbs.commons.utils.LogUtil;
import com.twtstudio.bbs.bdpqchen.bbs.commons.utils.PrefUtil;
import com.twtstudio.bbs.bdpqchen.bbs.commons.utils.ResourceUtil;

import org.piwik.sdk.Tracker;
import org.piwik.sdk.extra.CustomVariables;
import org.piwik.sdk.extra.TrackHelper;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.yokeyword.fragmentation.SupportActivity;


/**
 * Created by bdpqchen on 17-4-18.
 */

public abstract class BaseActivity<T extends BasePresenter> extends SupportActivity implements BaseView {

    @Inject
    protected T mPresenter;

    protected Activity mActivity;
    protected Context mContext;
    private Toolbar mToolbar;
    private Unbinder mUnBinder;
    public SlideBackLayout mSlideBackLayout;

    protected abstract int getLayoutResourceId();

    protected abstract Toolbar getToolbarView();

    protected abstract boolean isShowBackArrow();


    protected abstract void inject();

    protected abstract Activity supportSlideBack();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(PrefUtil.isNightMode() ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(getLayoutResourceId());
        mUnBinder = ButterKnife.bind(this);
        mActivity = this;
        mContext = this;
        inject();
        Activity activity = supportSlideBack();
        if (activity != null) {
            mSlideBackLayout = SlideBackHelper.attach(this, App.getActivityHelper(), getSlideConfig(), null);
        }
        if (mPresenter != null) {
            mPresenter.attachView(this);
        } else {

            LogUtil.d("mPresenter is null!!!");
        }

        mToolbar = getToolbarView();
        if (null != mToolbar) {
            setSupportActionBar(mToolbar);
            if (isShowBackArrow()) {
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                } else {

                }
            }
        }


        StatusBarUtil.setColor(this, ResourceUtil.getColor(this, R.color.colorPrimaryDark), 0);

        ActivityManager.getActivityManager().addActivity(this);
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        LogUtil.dd("onWindowFocusChanged()");
        /*if (hasFocus && mSlideBackLayout == null) {
            Activity activity = supportSlideBack();
            if (activity != null) {
                mSlideBackLayout = SlideBackHelper.attach(this, App.getActivityHelper(), getSlideConfig(), null);
            }
        }*/
    }

    private SlideConfig getSlideConfig() {
        return new SlideConfig.Builder()
                .rotateScreen(false)
                .edgeOnly(true)
                .edgePercent(0.3f)
                .slideOutPercent(0.2f)
                .create();
    }


    protected Tracker getTracker() {
        return ((App) getApplication()).getTracker();
    }

    protected TrackHelper getTrackerHelper() {
        CustomVariables variables = new CustomVariables();
        variables.put(1000, "android_app_version", getResources().getString(R.string.version_name));
        variables.put(1001, "android_os_version", Build.VERSION.RELEASE);
        variables.put(1002, "android_device_model", Build.MODEL);
        variables.put(1003, "android", "y");
        return TrackHelper.track(variables.toVisitVariables()).visitVariables(10, "android", "yes");
    }

    protected ActivityComponent getActivityComponent() {
        return DaggerActivityComponent.builder()
                .appComponent(App.getAppComponent())
                .activityModule(new ActivityModule(this))
                .build();
    }

    private void finishThisActivity() {
        ActivityManager.getActivityManager().finishActivity(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.detachView();
        }
        if (mUnBinder != null) {
            mUnBinder.unbind();
        }
//        finishMe();
    }

    @Override
    public void onBackPressedSupport() {
        super.onBackPressedSupport();
        finishThisActivity();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finishThisActivity();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void finishMe() {
        finishThisActivity();
    }


}
