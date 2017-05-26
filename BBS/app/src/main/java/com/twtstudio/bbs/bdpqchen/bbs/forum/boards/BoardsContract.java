package com.twtstudio.bbs.bdpqchen.bbs.forum.boards;

import com.twtstudio.bbs.bdpqchen.bbs.commons.base.BasePresenter;
import com.twtstudio.bbs.bdpqchen.bbs.commons.base.BaseView;

import java.util.List;

/**
 * Created by bdpqchen on 17-5-11.
 */

interface BoardsContract {

    interface View extends BaseView{
        void setBoardList(List<PreviewThreadModel> previewThreadList);

        void failedToGetBoardList(String msg);
    }

    interface Presenter extends BasePresenter<View>{

        void getBoardList(int forumId);


    }
}
