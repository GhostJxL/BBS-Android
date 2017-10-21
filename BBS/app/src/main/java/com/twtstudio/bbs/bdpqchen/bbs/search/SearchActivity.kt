package com.twtstudio.bbs.bdpqchen.bbs.search

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.View.GONE
import android.widget.ProgressBar
import android.widget.TextView
import com.twtstudio.bbs.bdpqchen.bbs.R
import com.twtstudio.bbs.bdpqchen.bbs.commons.base.BaseActivity
import com.twtstudio.bbs.bdpqchen.bbs.commons.helper.RecyclerViewItemDecoration
import com.twtstudio.bbs.bdpqchen.bbs.commons.support.Constants
import com.twtstudio.bbs.bdpqchen.bbs.commons.support.Constants.MODE_SEARCH_USER
import com.twtstudio.bbs.bdpqchen.bbs.commons.utils.LogUtil
import com.twtstudio.bbs.bdpqchen.bbs.individual.release.EndlessRecyclerOnScrollListener
import com.twtstudio.bbs.bdpqchen.bbs.search.model.AdapterModel
import com.twtstudio.bbs.bdpqchen.bbs.search.model.SearchUserModel

/**
 * Created by bdpqchen on 17-10-18.
 */

class SearchActivity : BaseActivity(), SearchContract.View {

    private val mPresenter: SearchPresenter = SearchPresenter(this)
    override fun getPresenter(): SearchPresenter {
        return mPresenter
    }

    override fun getLayoutResourceId(): Int {
        return R.layout.activity_search
    }

    override fun getToolbarView(): Toolbar {
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        toolbar.title = "搜索结果"
        return toolbar
    }

    private lateinit var mAdapter: SearchAdapter
    private lateinit var mTvEmptyResult: TextView
    private lateinit var mPbLoading: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mTvEmptyResult = findViewById(R.id.tv_search_result_empty) as TextView
        mPbLoading = findViewById(R.id.pb_search_result) as ProgressBar
        val rvSearchResult = findViewById(R.id.rv_search_result) as RecyclerView
        mAdapter = SearchAdapter(mContext)
        val layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
        rvSearchResult.adapter = mAdapter
        rvSearchResult.layoutManager = layoutManager
        rvSearchResult.addItemDecoration(RecyclerViewItemDecoration(5))
        rvSearchResult.addOnScrollListener(object : EndlessRecyclerOnScrollListener(layoutManager) {
            override fun onLoadMore() {
                LogUtil.d("search result onLoadMore")
            }
        })
        doSearch()
    }

    private fun doSearch(){
        val keyUser = intent.getStringExtra(Constants.INTENT_SEARCH_USER)
        if (keyUser != null && !keyUser.isEmpty()) {
            mPresenter.searchUser(keyUser)
        }
        val keyThread = intent.getStringExtra(Constants.INTENT_SEARCH_THREAD)
        if (keyThread != null && !keyThread.isEmpty()) {
            mPresenter.searchUser(keyThread)
        }
    }

    override fun onGotUserList(userList: List<SearchUserModel>) {
        if (userList.isEmpty()) {
            emptyResult()
            return
        }
        LogUtil.dd("onGotUserList")
        val adapterList = ArrayList<AdapterModel>()
        userList.mapTo(adapterList) { AdapterModel(it.name, MODE_SEARCH_USER, it.id) }
        mAdapter.addList(adapterList)

    }

    private fun emptyResult() {
        hideLoading()
        showEmptyText()
    }

    private fun showEmptyText() {
        mTvEmptyResult.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        mPbLoading.visibility = GONE
    }

    override fun onGotUserFailed(msg: String) {
        LogUtil.dd("onGotUserListFailed()", msg)
    }

    override fun onGotThreadList() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onGotThreadFailed(msg: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}
