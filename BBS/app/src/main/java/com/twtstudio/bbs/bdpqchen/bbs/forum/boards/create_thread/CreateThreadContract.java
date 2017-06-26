package com.twtstudio.bbs.bdpqchen.bbs.forum.boards.create_thread;

import android.os.Bundle;

import com.twtstudio.bbs.bdpqchen.bbs.commons.base.BasePresenter;
import com.twtstudio.bbs.bdpqchen.bbs.commons.base.BaseView;
import com.twtstudio.bbs.bdpqchen.bbs.forum.boards.thread.model.UploadImageModel;

/**
 * Created by bdpqchen on 17-5-27.
 */

interface CreateThreadContract {
    interface View extends BaseView{
        void onPublished();
        void onPublishFailed(String msg);
        void onUploadFailed(String m);
        void onUploaded(UploadImageModel model);

    }
    interface Presenter extends BasePresenter<View>{
        void doPublishThread(Bundle bundle);
        void uploadImages(String uri);
    }
}
