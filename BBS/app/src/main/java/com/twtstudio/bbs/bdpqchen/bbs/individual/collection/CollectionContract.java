package com.twtstudio.bbs.bdpqchen.bbs.individual.collection;

import com.twtstudio.bbs.bdpqchen.bbs.commons.base.BasePresenter;
import com.twtstudio.bbs.bdpqchen.bbs.commons.base.BaseView;
import com.twtstudio.bbs.bdpqchen.bbs.individual.model.CollectionBean;
import com.twtstudio.bbs.bdpqchen.bbs.individual.model.IndividualInfoModel;

/**
 * Created by HP on 2017/5/12.
 */

interface CollectionContract  {
    interface View extends BaseView{
        void setCollectionAdapter(CollectionBean collectionDate);
        void makeDeleteSuccessToast();

    }
    interface Presenter extends BasePresenter<View>{
        void setCollectionDate(CollectionBean collectionDate);
        void loadCollections();
        void deleteCollection(int tid);
        void makeDeleteSuccessToast();
    }
}
