package com.twtstudio.bbs.bdpqchen.bbs.forum.boards.thread;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.twtstudio.bbs.bdpqchen.bbs.R;
import com.twtstudio.bbs.bdpqchen.bbs.commons.base.BaseActivity;
import com.twtstudio.bbs.bdpqchen.bbs.commons.helper.RecyclerViewItemDecoration;
import com.twtstudio.bbs.bdpqchen.bbs.commons.model.BaseModel;
import com.twtstudio.bbs.bdpqchen.bbs.commons.utils.CastUtil;
import com.twtstudio.bbs.bdpqchen.bbs.commons.utils.DialogUtil;
import com.twtstudio.bbs.bdpqchen.bbs.commons.utils.HandlerUtil;
import com.twtstudio.bbs.bdpqchen.bbs.commons.utils.ImageFormatUtil;
import com.twtstudio.bbs.bdpqchen.bbs.commons.utils.ImagePickUtil;
import com.twtstudio.bbs.bdpqchen.bbs.commons.utils.IntentUtil;
import com.twtstudio.bbs.bdpqchen.bbs.commons.utils.LogUtil;
import com.twtstudio.bbs.bdpqchen.bbs.commons.utils.PathUtil;
import com.twtstudio.bbs.bdpqchen.bbs.commons.utils.PrefUtil;
import com.twtstudio.bbs.bdpqchen.bbs.commons.utils.SnackBarUtil;
import com.twtstudio.bbs.bdpqchen.bbs.commons.utils.TextUtil;
import com.twtstudio.bbs.bdpqchen.bbs.forum.boards.thread.model.PostModel;
import com.twtstudio.bbs.bdpqchen.bbs.forum.boards.thread.model.ThreadModel;
import com.twtstudio.bbs.bdpqchen.bbs.forum.boards.thread.model.UploadImageModel;
import com.zhihu.matisse.Matisse;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;
import static com.twtstudio.bbs.bdpqchen.bbs.commons.rx.RxDoHttpClient.BASE;
import static com.twtstudio.bbs.bdpqchen.bbs.commons.support.Constants.INTENT_BOARD_ID;
import static com.twtstudio.bbs.bdpqchen.bbs.commons.support.Constants.INTENT_BOARD_TITLE;
import static com.twtstudio.bbs.bdpqchen.bbs.commons.support.Constants.INTENT_EDITOR_CONTENT;
import static com.twtstudio.bbs.bdpqchen.bbs.commons.support.Constants.INTENT_THREAD_ID;
import static com.twtstudio.bbs.bdpqchen.bbs.commons.support.Constants.INTENT_THREAD_TITLE;
import static com.twtstudio.bbs.bdpqchen.bbs.commons.support.Constants.MAX_LENGTH_POST;
import static com.twtstudio.bbs.bdpqchen.bbs.commons.support.Constants.PK_THREAD;
import static com.twtstudio.bbs.bdpqchen.bbs.commons.support.Constants.REQUEST_CODE_EDITOR;
import static com.twtstudio.bbs.bdpqchen.bbs.commons.support.Constants.REQUEST_CODE_IMAGE_SELECTED;


/**
 * Created by bdpqchen on 17-5-12.
 */

public class ThreadActivity extends BaseActivity<ThreadPresenter> implements ThreadContract.View, PostAdapter.OnPostClickListener {
    @BindView(R.id.toolbar_thread)
    Toolbar mToolbar;
    @BindView(R.id.rv_thread_post)
    RecyclerView mRvThreadPost;
    @BindView(R.id.pb_thread_loading)
    ProgressBar mPbThreadLoading;
    @BindView(R.id.srl_thread_list)
    SwipeRefreshLayout mSrlThreadList;
    @BindView(R.id.et_comment)
    EditText mEtComment;
    @BindView(R.id.ll_comment)
    LinearLayout mLlComment;
    @BindView(R.id.iv_comment_send)
    ImageView mIvCommentSend;
    @BindView(R.id.iv_comment_out)
    ImageView mIvCommentOut;
    @BindView(R.id.iv_stared_thread)
    ImageView mIvStaredThread;
    @BindView(R.id.iv_star_thread)
    ImageView mIvStarThread;
    @BindView(R.id.tv_dynamic_hint)
    TextView mTvDynamicHint;
    @BindView(R.id.cb_anonymous_comment)
    AppCompatCheckBox mCbAnonymousComment;
    @BindView(R.id.collapsing_toolbar_layout)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.toolbar_title_thread)
    TextView mToolbarTitleThread;
    @BindView(R.id.toolbar_title_board)
    TextView mToolbarTitleBoard;
    @BindView(R.id.tv_select_image)
    TextView mTvSelectImage;
    @BindView(R.id.tv_open_editor)
    TextView mTvOpenEditor;
    @BindView(R.id.fab_thread_comment)
    FloatingActionButton mFabComment;

    public static final String INTENT_THREAD_FLOOR = "intent_thread_floor";
    private String mThreadTitle = "";
    private int mThreadId = 0;
    private Context mContext;
    private PostAdapter mAdapter;
    private String mComment = "";
    private MaterialDialog mProgress;
    private boolean mRefreshing = false;
    private int postPosition = 0;
    private int mReplyId = 0;
    private int mAuthorId = 0;
    private boolean mIsAnonymous = false;
    private boolean mCanAnonymous = false;
    private int mBoardId = 0;
    private String mBoardName = "";
    private LinearLayoutManager mLayoutManager;
    private int lastVisibleItemPosition = 0;
    private int mPage = 0;
    private boolean mIsLoadingMore;
    private boolean mIsCommentAfter = false;
    private int mPostCount = 0;
    private boolean showingThreadTitle = false;
    private boolean showingBoardName = true;
    private boolean mIsFindingFloor = false;
    private int mFindingPage = 0;
    private int mFindingFloor = 0;
    private int mInputFloor = 0;
    private int mPossibleIndex = 0;

    private ImageFormatUtil mImageFormatUtil;
    private boolean mIsFindEnd = false;
    private boolean mFabShowing = true;
    private boolean isLastPage = false;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_thread;
    }

    @Override
    protected Toolbar getToolbarView() {
        return mToolbar;
    }

    @Override
    protected boolean isShowBackArrow() {
        return true;
    }

    @Override
    protected void inject() {
        getActivityComponent().inject(this);
    }

    @Override
    protected Activity supportSlideBack() {
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        mThreadId = intent.getIntExtra(INTENT_THREAD_ID, 0);
        mFindingFloor = intent.getIntExtra(INTENT_THREAD_FLOOR, 0);
        mThreadTitle = intent.getStringExtra(INTENT_THREAD_TITLE);
        mBoardName = intent.getStringExtra(INTENT_BOARD_TITLE);
        mBoardId = intent.getIntExtra(INTENT_BOARD_ID, 0);
        super.onCreate(savedInstanceState);
        mContext = this;
        mImageFormatUtil = new ImageFormatUtil();
        if (mFindingFloor != 0) {
            mIsFindingFloor = true;
        }

        mAdapter = new PostAdapter(mContext, this);
        mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mRvThreadPost.setAnimation(null);
        mRvThreadPost.addItemDecoration(new RecyclerViewItemDecoration(1));
        mRvThreadPost.setLayoutManager(mLayoutManager);
        mRvThreadPost.setAdapter(mAdapter);
        mRvThreadPost.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!isLastPage && newState == SCROLL_STATE_IDLE && lastVisibleItemPosition + 1 == mAdapter.getItemCount()) {
                    mPage++;
                    mIsLoadingMore = true;
                    mPresenter.getThread(mThreadId, mPage);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItemPosition = mLayoutManager.findLastVisibleItemPosition();
                if (dy > 0 && mFabShowing) {
                    mFabShowing = false;
                    hideFab();
                } else if (dy < 0 && !mFabShowing) {
                    mFabShowing = true;
                    showFab();
                }
                int d = 300;
                if (lastVisibleItemPosition != 0 && mLayoutManager.findFirstCompletelyVisibleItemPosition() != 0) {
                    if (!showingThreadTitle) {
                        //出现标题
                        if (mToolbarTitleBoard != null) {
                            YoYo.with(Techniques.SlideOutUp).duration(d).playOn(mToolbarTitleBoard);
                        }
                        if (mToolbarTitleThread != null) {
                            YoYo.with(Techniques.SlideInUp).duration(d).playOn(mToolbarTitleThread);
                            mToolbarTitleThread.setText(mThreadTitle);
                        }
                        showingThreadTitle = true;
                        showingBoardName = false;
                    }
                } else {
                    //出现板块名称
                    if (!showingBoardName) {
                        if (mToolbarTitleThread != null) {
                            YoYo.with(Techniques.SlideOutUp).duration(d).playOn(mToolbarTitleThread);
                        }
                        if (mToolbarTitleBoard != null) {
                            mToolbarTitleBoard.setText(TextUtil.getLinkHtml(mBoardName));
                            YoYo.with(Techniques.SlideInUp).duration(d).playOn(mToolbarTitleBoard);
                        }
                        showingBoardName = true;
                        showingThreadTitle = false;
                    }
                }
            }
        });

        mToolbarTitleThread.setOnClickListener(v -> {
            toTop();
        });
        mFabComment.setOnClickListener(v -> {
            showCommentInput();
            resetReply();
        });
        mEtComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mEtComment != null && mEtComment.getText().toString().length() > 0) {
                    mIvCommentOut.setVisibility(View.GONE);
                    mIvCommentSend.setVisibility(View.VISIBLE);
                } else {
                    mIvCommentOut.setVisibility(View.VISIBLE);
                    mIvCommentSend.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mIvCommentOut.setOnClickListener(v -> hideCommentInput());
        mIvCommentSend.setOnClickListener(v -> sendComment(mReplyId));
        if (PrefUtil.hadLogin()) {
            mIvStaredThread.setOnClickListener(v -> {
                mPresenter.unStarThread(mThreadId);
            });
            mIvStarThread.setOnClickListener(v -> {
                mPresenter.starThread(mThreadId);
            });
        }
        mSrlThreadList.setColorSchemeColors(getResources().getIntArray(R.array.swipeRefreshColors));
        mSrlThreadList.setRefreshing(true);
        mSrlThreadList.setOnRefreshListener(() -> {
            mRefreshing = true;
            mPage = 0;
            mPresenter.getThread(mThreadId, mPage);
//                mSrlThreadList.setRefreshing(false);
        });
        mCbAnonymousComment.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mIsAnonymous = isChecked;
        });
        mTvSelectImage.setOnClickListener(v -> {
            ImagePickUtil.commonPickImage(this);
        });
        mTvOpenEditor.setOnClickListener(v -> {
            startActivityForResult(IntentUtil.toEditor(mContext, mTvDynamicHint.getText().toString(), mEtComment.getText().toString(), 1), REQUEST_CODE_EDITOR);
        });
        mPresenter.getThread(mThreadId, 0);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_IMAGE_SELECTED && resultCode == RESULT_OK) {
            if (data != null) {
                List<Uri> mSelected = Matisse.obtainResult(data);
                mPresenter.uploadImages(PathUtil.getRealPathFromURI(mContext, mSelected.get(0)));
                startProgress("正在添加图片，请稍后..");
                // TODO: 17-6-9  支持多张图片
//                mSelectedCount = mSelected.size();
//                for (int i = 0; i < mSelected.size(); i++) {
//                    LogUtil.dd("Matisse", "mSelected: " + mSelected.get(i));
//                    mPresenter.uploadImages(PathUtil.getRealPathFromURI(mContext, mSelected.get(i)));
//                }
            }
        }
        if (requestCode == REQUEST_CODE_EDITOR && resultCode == RESULT_OK) {
            if (data != null) {
                String resultContent = data.getStringExtra(INTENT_EDITOR_CONTENT);
                mEtComment.setText(resultContent);
                mEtComment.setSelection(resultContent.length());
                LogUtil.dd("resultContent", resultContent);
            }

        }
    }

    @Override
    public void onGotThread(ThreadModel model) {
        int canAnonymous = mCanAnonymous ? 1 : 0;
        if (model.getThread() != null) {
            ThreadModel.ThreadBean entity = model.getThread();
            mPostCount = entity.getC_post();
            if (mPage == 0) {
                canAnonymous = model.getBoard().getAnonymous();
            }
            pkTracker(entity.getTitle(), mPage + 1);
        }
        List<ThreadModel.PostBean> postList = new ArrayList<>();
        //将帖主重组成一个回复
        if (mPage == 0 && model.getThread() != null) {
            mAuthorId = model.getThread().getAuthor_id();
            ThreadModel.ThreadBean thread = model.getThread();
            showStarOrNot(thread.getIn_collection());
            if (thread.getAnonymous() == 1) {
                thread.setAuthor_name("匿名用户");
            }
            ThreadModel.PostBean post = new ThreadModel.PostBean();
            post.setAnonymous(thread.getAnonymous());
            post.setAuthor_name(thread.getAuthor_name());
            post.setAuthor_nickname(thread.getAuthor_nickname());
            post.setContent(thread.getContent());
            post.setFloor(1);
            post.setAuthor_id(thread.getAuthor_id());
            post.setId(thread.getId());
            post.setTitle(thread.getTitle());
            post.setT_create(thread.getT_create());
            post.setT_modify(thread.getT_modify());
            post.setContent_converted(thread.getContent_converted());
            postList.add(post);
        }
        if (model.getPost() == null || model.getPost().size() == 0) {
            LogUtil.dd("no more post");
            isLastPage = true;
            pageSS();
        }
        if (mIsCommentAfter) {
            mIsCommentAfter = false;
            if (model.getPost() != null) {
                postList.addAll(model.getPost());
                mAdapter.refreshThisPage(postList, mPage);
            }
        } else {
            if (mRefreshing) {
                mRefreshing = false;
                postList.addAll(model.getPost());
                mAdapter.refreshList(postList);
            } else {
                if (!isLastPage) {
                    postList.addAll(model.getPost());
                }
                mAdapter.updateThreadPost(postList, mPage);
            }
        }
        //跳楼
        if (mIsFindingFloor) {
            if (model.getPost() == null || model.getPost().size() == 0) {
                //到了帖子的尽头
                LogUtil.dd("model is null");
                cannotFindIt();
            } else {
                if (isInside()) {
                    findIt();
                } else {
                    mPresenter.getThread(mThreadId, ++mFindingPage);
                }
            }
            mPage = mFindingPage;
        }
        if (mPage == 0 && model.getBoard() != null) {
            mBoardId = model.getBoard().getId();
            mBoardName = model.getBoard().getName();
            mToolbarTitleBoard.setText(TextUtil.getLinkHtml(mBoardName));
            int finalCanAnonymous = canAnonymous;
            mToolbarTitleBoard.setOnClickListener(v -> {
                startActivity(IntentUtil.toThreadList(mContext, mBoardId, mBoardName, finalCanAnonymous));
            });
        }
        setRefreshing(false);
        showAnonymousOrNot(canAnonymous);
        hideProgressBar();
        setCheckBox(false);
    }

    @Override
    public void onGetThreadFailed(String m) {
        SnackBarUtil.error(this, m);
        hideProgressBar();
        mRefreshing = false;
        if (mIsLoadingMore) {
            mIsLoadingMore = false;
            mPage--;
        }
        setRefreshing(false);
        pageSS();
    }

    @Override
    public void onCommentFailed(String m) {
        hideProgress();
        SnackBarUtil.error(this, m, true);
    }

    @Override
    public void onCommented(PostModel model) {
        hideProgress();
        hideCommentInput();
        clearCommentData();
        SnackBarUtil.normal(this, "评论成功");
        mIsCommentAfter = true;
        mPresenter.getThread(mThreadId, mPage);
    }

    @Override
    public void onStarFailed(String m) {
        SnackBarUtil.error(this, m);
    }

    @Override
    public void onUnStarFailed(String m) {
        SnackBarUtil.error(this, m);
    }

    @Override
    public void onStarred() {
        SnackBarUtil.normal(this, "收藏成功");
        showStarOrNot(1);
    }

    @Override
    public void onUnStarred() {
        SnackBarUtil.normal(this, "已取消收藏");
        showStarOrNot(0);
    }

    @Override
    public void onUploadFailed(String m) {
        LogUtil.dd("error msg upload", m);
        SnackBarUtil.error(this, "图片添加失败\n" + m);
        hideProgress();
    }

    @Override
    public void onUploaded(UploadImageModel model) {
        if (mEtComment != null) {
            String added = mEtComment.getText() + mImageFormatUtil.getShowImageCode(model.getId());
            mEtComment.setText(added);
            mEtComment.setSelection(mEtComment.getText().length());
        }
        hideProgress();
        SnackBarUtil.normal(this, "图片已添加");
    }

    @Override
    public void onLike(BaseModel model, int id) {
        SnackBarUtil.normal(this, "点赞成功");
    }

    @Override
    public void onLikeFailed(String m, int id) {
        SnackBarUtil.notice(this, m);
    }

    @Override
    public void onUnlike(BaseModel entity, int id) {
        SnackBarUtil.normal(this, "点赞已取消");
    }

    @Override
    public void onUnlikeFailed(String m, int id) {
        SnackBarUtil.notice(this, "取消点赞失败");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_thread, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finishMe();
                break;
            case R.id.action_thread_share:
                String url = BASE + "/forum/thread/" + mThreadId;
                shareText(url);
                break;
            case R.id.action_thread_refresh:
                mRefreshing = true;
                setRefreshing(true);
                mPresenter.getThread(mThreadId, 0);
                break;
            case R.id.action_thread_top:
                toTop();
                break;
            case R.id.action_thread_jump:
                if (!mRefreshing) {
                    DialogUtil.inputDialog(mContext,
                            "输入楼层,最大可能是" + mPostCount + "左右",
                            (dialog, input) -> {
                                mInputFloor = CastUtil.parse2intWithMax(input.toString());
                                if (mInputFloor != 0) {
                                    mFindingPage = mPage;
                                    startProgress("正在前往..");
                                    findFloor(mInputFloor);
                                }
                            });
                }
                break;
            case R.id.action_thread_bottom:
                if (!mRefreshing) {
                    startProgress("正在潜入..");
                    toEnd();
                }
                break;
        }
        return false;
    }

    private void stopFinding() {
        mFindingFloor = 0;
        mIsFindingFloor = false;
        mRvThreadPost.scrollToPosition(mPossibleIndex);
        setRefreshing(false);
        hideProgress();
    }

    private void cannotFindIt() {
        if (!mIsFindEnd) {
            SnackBarUtil.notice(this, "该楼层不存在,可能已经被删除\n已经帮你跳转到附近楼层", true);
        } else {
            mIsFindEnd = false;
        }
        stopFinding();
    }

    private void findIt() {
        stopFinding();
    }

    private void findFloor(int floor) {
        mFindingFloor = floor;
        if (isInside()) {
            mRvThreadPost.scrollToPosition(mPossibleIndex);
            mPage = mFindingPage;
            hideProgress();
        } else {
            mIsFindingFloor = true;
            mFindingPage = ++mPage;
            mPresenter.getThread(mThreadId, mFindingPage);
        }
    }

    private boolean isInside() {
        mPossibleIndex = 0;
        boolean isFindIt = false;
        if (mAdapter.getPostList() == null) {
            return false;
        }
        List<ThreadModel.PostBean> posts = mAdapter.getPostList();
        int size = posts.size();
        int start = 0;
        if (mIsFindingFloor) {
            start = mFindingPage * MAX_LENGTH_POST;
        }
        if (size > 0) {
            for (int i = start; i < size; i++) {
                int floor = posts.get(i).getFloor();
                if (floor == mFindingFloor) {
                    isFindIt = true;
                    LogUtil.dd("find it", String.valueOf(i));
                    mPossibleIndex = i;
                    break;
                }
                if (floor > mFindingFloor) {
                    mPossibleIndex = i;
                    break;
                }
                mPossibleIndex = i;
            }
        }
        return isFindIt;
    }

    private void toEnd() {
        findFloor(2147483647);
        mIsFindEnd = true;
    }

    private void toTop() {
        if (mPage == 0) {
            mRvThreadPost.smoothScrollToPosition(0);
        } else {
            mRvThreadPost.scrollToPosition(0);
        }
    }

    private void showAnonymousOrNot(int canAnonymous) {
        if (mCbAnonymousComment == null) {
            return;
        }
        if (canAnonymous == 1) {
            mCanAnonymous = true;
            if (PrefUtil.isAlwaysAnonymous()) {
                HandlerUtil.postDelay(() -> {
                    if (mCbAnonymousComment != null) {
                        mCbAnonymousComment.setChecked(true);
                    }
                }, 600);
            }
            mCbAnonymousComment.setVisibility(View.VISIBLE);
        } else {
            mCbAnonymousComment.setVisibility(View.GONE);
        }
    }

    private void sendComment(int replyId) {
        if (mEtComment == null) {
            return;
        }
        mComment = mImageFormatUtil.replaceImageFormat(mEtComment.getText().toString());
        if (mEtComment != null && mComment.length() > 0) {
            if (replyId != 0) {
                mComment = mAdapter.comment2reply(postPosition, mComment);
            }
            mPresenter.doComment(mThreadId, mComment, replyId, mIsAnonymous);
            startProgress("正在发送，稍后..");
        }
    }

    private void startProgress(String msg) {
        mProgress = DialogUtil.showProgressDialog(this, msg);
    }

    private void hideProgress() {
        LogUtil.dd("hideProgress");
        if (mProgress != null) {
            mProgress.dismiss();
        }
    }

    private void hideFab() {
        int d = 500;
        YoYo.with(Techniques.SlideOutDown)
                .duration(d)
                .playOn(mFabComment);
    }

    private void showFab() {
        int d = 500;
        YoYo.with(Techniques.SlideInUp)
                .duration(d)
                .playOn(mFabComment);
    }

    private void hideCommentInput() {
        resetReply();
        int d = 100;
        YoYo.with(Techniques.SlideInRight)
                .duration(d)
                .playOn(mFabComment);
        YoYo.with(Techniques.SlideOutDown)
                .duration(d)
                .playOn(mLlComment);
        View view = getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void showCommentInput() {
        int d = 100;
        YoYo.with(Techniques.SlideOutRight)
                .duration(d)
                .playOn(mFabComment);
        YoYo.with(Techniques.SlideInUp)
                .duration(d)
                .playOn(mLlComment);
        mLlComment.setVisibility(View.VISIBLE);
        mTvDynamicHint.setText(mAdapter.getDynamicHint(postPosition));
        mEtComment.setFocusable(true);
        mEtComment.setFocusableInTouchMode(true);
        mEtComment.requestFocus();
        InputMethodManager imm = (InputMethodManager) mLlComment
                .getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        HandlerUtil.postDelay(() -> {
            if (imm != null) {
                imm.showSoftInput(mEtComment, 0);
            }
        }, d + 100);
    }

    private void showStarOrNot(int in_collection) {
//        LogUtil.dd("incollection", String.valueOf(in_collection));
        if (in_collection == 1) {
            if (mIvStaredThread != null) {
                mIvStaredThread.setVisibility(View.VISIBLE);
            }
            if (mIvStarThread != null) {
                mIvStarThread.setVisibility(View.GONE);
            }
        } else {
            if (mIvStaredThread != null) {
                mIvStaredThread.setVisibility(View.GONE);
            }
            if (mIvStarThread != null) {
                mIvStarThread.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setCheckBox(boolean b) {
        if (mCbAnonymousComment != null) {
            mCbAnonymousComment.setChecked(b);
        }
    }

    private void hideProgressBar() {
        if (mPbThreadLoading != null) {
            mPbThreadLoading.setVisibility(View.GONE);
        }
    }

    private void setRefreshing(boolean refreshing) {
        if (mSrlThreadList != null) {
            mRefreshing = refreshing;
            mSrlThreadList.setRefreshing(refreshing);
        }
    }

    private void clearCommentData() {
        mComment = "";
        mEtComment.setText("");
        resetReply();
    }

    private void resetReply() {
        postPosition = 0;
        mReplyId = 0;
    }

    private void pageSS() {
        if (mPage != 0) {
            mPage--;
        }
    }

    public void shareText(String url) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
//        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "这个是content");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "BBS分享的标题");
        shareIntent.putExtra(Intent.EXTRA_TEXT, url);
//        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setType("text/plain");
//        shareIntent.setType()
//        shareIntent.setType("image/*");
        //设置分享列表的标题，并且每次都显示分享列表
        startActivity(Intent.createChooser(shareIntent, "分享到"));
    }

    private void pkTracker(String title, int page) {
        getTrackerHelper()
                .screen(PK_THREAD + mThreadId + "/page/" + page + "/")
                .title(title).with(getTracker());
    }

    @Override
    public void onLikeClick(int position, boolean isLike, boolean isPost) {
        mPresenter.like(mAdapter.getPostId(position), isLike, isPost);
        mAdapter.likeItem(position, isLike);
//        mAdapter.notifyItemChanged(position);

    }

    @Override
    public void onReplyClick(int position) {
        postPosition = position;
        mReplyId = mAdapter.getPostId(position);
        showCommentInput();
    }
}
