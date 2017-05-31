package com.twtstudio.bbs.bdpqchen.bbs.main.latestPost;

import com.twtstudio.bbs.bdpqchen.bbs.commons.presenter.RxPresenter;
import com.twtstudio.bbs.bdpqchen.bbs.commons.rx.RxDoHttpClient;
import com.twtstudio.bbs.bdpqchen.bbs.commons.rx.SimpleObserver;
import com.twtstudio.bbs.bdpqchen.bbs.main.model.LatestPostModel;


import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by bdpqchen on 17-5-11.
 */

class LatestPostPresenter extends RxPresenter<LatestPostContract.View> implements LatestPostContract.Presenter {

    public RxDoHttpClient<LatestPostModel.DataBean> mHttpClient;

    @Inject
    LatestPostPresenter(RxDoHttpClient client) {
        mHttpClient = client;
    }

    @Override
    public void refreshAnnounce() {
        SimpleObserver<LatestPostModel.DataBean> observer = new SimpleObserver<LatestPostModel.DataBean>() {
            @Override
            public void _onError(String msg) {
                if (mView != null) {
                    mView.failedToGetLatestPost(msg);
                }
            }

            @Override
            public void _onNext(LatestPostModel.DataBean latestPostModel) {
                if (mView != null) {
                    mView.refreshAnnounce(latestPostModel.getLatest());
                }
            }

        };
        addSubscribe(mHttpClient.getLatestPost()
                .map(mHttpClient.mTransformer)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(observer)
        );
    }

    public void addAnnounce() {

        SimpleObserver<LatestPostModel.DataBean> observer = new SimpleObserver<LatestPostModel.DataBean>() {
            @Override
            public void _onError(String msg) {
                mView.failedToGetLatestPost(msg);
            }

            @Override

            public void _onNext(LatestPostModel.DataBean latestPostModel) {
                mView.refreshAnnounce(latestPostModel.getLatest());

            }

        };
        addSubscribe(mHttpClient.getLatestPost()
                .map(mHttpClient.mTransformer)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(observer)
        );
    }


}
