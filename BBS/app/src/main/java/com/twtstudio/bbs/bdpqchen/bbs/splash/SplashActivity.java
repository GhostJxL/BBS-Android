package com.twtstudio.bbs.bdpqchen.bbs.splash;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.twtstudio.bbs.bdpqchen.bbs.auth.login.LoginModel;
import com.twtstudio.bbs.bdpqchen.bbs.commons.utils.PrefUtil;
import com.twtstudio.bbs.bdpqchen.bbs.home.HomeActivity;
import com.twtstudio.bbs.bdpqchen.bbs.auth.login.LoginActivity;
import com.twtstudio.bbs.bdpqchen.bbs.welcome.IntroActivity;
import com.twtstudio.bbs.bdpqchen.bbs.welcome.WelcomeActivity;
import com.twtstudio.bbs.bdpqchen.bbs.welcome.WelcomeGuideActivity;

/**
 * Created by bdpqchen on 17-5-2.
 */

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (isFirstOpen)
//        if (PrefUtil.hadLogin() || PrefUtil.isNoAccountUser()){

        if (PrefUtil.hadLogin()) {
            startActivity(new Intent(this, HomeActivity.class));
        } else {
            if (!PrefUtil.isFirstOpen()) {
                startActivity(new Intent(this, LoginActivity.class));
            } else {
                startActivity(new Intent(this, IntroActivity.class));
            }
        }
        finish();

    }
}
