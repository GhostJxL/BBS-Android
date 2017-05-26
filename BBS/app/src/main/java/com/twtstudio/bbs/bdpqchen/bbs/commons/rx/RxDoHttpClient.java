package com.twtstudio.bbs.bdpqchen.bbs.commons.rx;

import android.os.Bundle;

import com.twtstudio.bbs.bdpqchen.bbs.auth.login.LoginModel;
import com.twtstudio.bbs.bdpqchen.bbs.auth.register.RegisterModel;
import com.twtstudio.bbs.bdpqchen.bbs.auth.renew.identify.IdentifyModel;
import com.twtstudio.bbs.bdpqchen.bbs.auth.renew.identify.retrieve.RetrieveActivity;
import com.twtstudio.bbs.bdpqchen.bbs.auth.renew.identify.retrieve.RetrieveModel;
import com.twtstudio.bbs.bdpqchen.bbs.commons.model.BaseModel;
import com.twtstudio.bbs.bdpqchen.bbs.commons.support.Constants;
import com.twtstudio.bbs.bdpqchen.bbs.commons.utils.PrefUtil;
import com.twtstudio.bbs.bdpqchen.bbs.forum.ForumModel;
import com.twtstudio.bbs.bdpqchen.bbs.forum.boards.BoardsModel;
import com.twtstudio.bbs.bdpqchen.bbs.forum.boards.thread.ThreadModel;
import com.twtstudio.bbs.bdpqchen.bbs.forum.boards.thread_list.ThreadListModel;
import com.twtstudio.bbs.bdpqchen.bbs.individual.message.MessageModel;
import com.twtstudio.bbs.bdpqchen.bbs.individual.model.IndividualInfoModel;
import com.twtstudio.bbs.bdpqchen.bbs.main.content.ContentModel;
import com.twtstudio.bbs.bdpqchen.bbs.main.content.post.IndexPostModel;
import com.twtstudio.bbs.bdpqchen.bbs.main.historyHot.HistoryHotModel;
import com.twtstudio.bbs.bdpqchen.bbs.main.latestPost.LatestPostModel;
import com.twtstudio.bbs.bdpqchen.bbs.main.topTen.TopTenModel;
import com.twtstudio.bbs.bdpqchen.bbs.individual.my_release.MyReleaseModel;

import java.io.File;
import java.security.cert.CertificateException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import io.reactivex.Observable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by bdpqchen on 17-4-27.
 */

public class RxDoHttpClient<T> {

    //    public static final String BASE_URL = "http://202.113.13.162:8080/";
    //将会遇到证书 CA 问题
    public static final String BASE_URL = "https://bbs.twtstudio.com/api/";
    private Retrofit mRetrofit;
    public BaseApi mApi;
    public ResponseTransformer<T> mTransformer;
    public SchedulersHelper mSchedulerHelper;

    // 由于https在连接的过程中会遇到
    //javax.net.ssl.SSLHandshakeException: java.security.cert.CertPathValidatorException: Trust anchor for certification path not found.
    //由于无证书的连接是不可信的，在此，建立Okhttp3连接时，选择信任所有的证书。参照
    //https://blog.ijustyce.win/post/retrofit2%E4%B9%8Bhttps.html
    public static OkHttpClient.Builder getUnsafeBuilder() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };
            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final javax.net.ssl.SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory);

            builder.hostnameVerifier((hostname, session) -> true);
            return builder;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static OkHttpClient.Builder getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();
            okHttpClient.sslSocketFactory(sslSocketFactory);
            okHttpClient.protocols(Collections.singletonList(Protocol.HTTP_1_1));
            okHttpClient.hostnameVerifier((hostname, session) -> true);
            return okHttpClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public RxDoHttpClient() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

//        OkHttpClient client = new OkHttpClient.Builder()
        OkHttpClient client = getUnsafeBuilder()
                .addInterceptor(interceptor)
                .retryOnConnectionFailure(true)
                .connectTimeout(10, TimeUnit.SECONDS)
                .build();


        mRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(DirtyJsonConverter.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mApi = mRetrofit.create(BaseApi.class);
        mTransformer = new ResponseTransformer<>();
        mSchedulerHelper = new SchedulersHelper();

    }

    private String getLatestAuthentication() {
        return PrefUtil.getAuthUid() + "|" + PrefUtil.getAuthToken();
    }


    public Observable<BaseResponse<LoginModel>> doLogin(String username, String password) {
        return mApi.doLogin(username, password);
    }

    public Observable<BaseResponse<List<ForumModel>>> getForumList() {
        return mApi.getForums();
    }


    public Observable<BaseResponse<LatestPostModel.DataBean>> getLatestPost() {

        return mApi.getLatestPost();
    }

    public Observable<BaseResponse<TopTenModel.DataBean>> getTopTen() {

        return mApi.getTopTen();
    }

   /* public Observable<BaseResponse<HistoryHotModel.DataBean>> getHistoryHot() {
        return mApi.getHistoryHot();
    }*/

    public Observable<BaseResponse<RegisterModel>> doRegister(Bundle bundle) {
        return mApi.doRegister(
                bundle.getString(Constants.BUNDLE_REGISTER_USERNAME),
                bundle.getString(Constants.BUNDLE_REGISTER_CID),
                bundle.getString(Constants.BUNDLE_REGISTER_PASSWORD),
                bundle.getString(Constants.BUNDLE_REGISTER_STU_NUM),
                bundle.getString(Constants.BUNDLE_REGISTER_REAL_NAME)
        );
    }

    public Observable<BaseResponse<IndividualInfoModel>> getIndividualInfo() {
        return mApi.getIndividualInfo(getLatestAuthentication());
    }

    public Observable<BaseResponse<BaseModel>> doUpdateInfo(Bundle bundle, int type) {

        if (type == 1) {
            return mApi.doUpdateInfoNickname(getLatestAuthentication(), bundle.getString(Constants.BUNDLE_NICKNAME, ""));
        } else if (type == 2) {
            return mApi.doUpdateInfoSignature(getLatestAuthentication(), bundle.getString(Constants.BUNDLE_SIGNATURE, ""));
        }
        return mApi.doUpdateInfoAll(getLatestAuthentication(), bundle.getString(Constants.BUNDLE_NICKNAME, ""), bundle.getString(Constants.BUNDLE_SIGNATURE, ""));

    }


    public Observable<BaseResponse<BaseModel>> doUpdateAvatar(String imagePath) {
        File file = new File(imagePath);//filePath 图片地址
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);//表单类型
        RequestBody imageBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        builder.addFormDataPart("img_file", file.getName(), imageBody);//imgfile 后台接收图片流的参数名
        List<MultipartBody.Part> parts = builder.build().parts();
        return mApi.doUpdateAvatar(getLatestAuthentication(), parts);
    }

    public Observable<BaseResponse<BoardsModel>> getBoardList(int forumId) {
        return mApi.getBoardList(String.valueOf(forumId));
    }

    public Observable<BaseResponse<ThreadListModel>> getThreadList(int threadId, int page) {
        return mApi.getThreadList(getLatestAuthentication(), "Mobile", String.valueOf(threadId), String.valueOf(page));
    }

    public Observable<BaseResponse<List<MessageModel>>> getMessageList(int page) {
        return mApi.getMessageList(getLatestAuthentication(), String.valueOf(page));
    }

    public Observable<BaseResponse<ContentModel.DataBean>> getIndexContent(String threadid) {
        return mApi.getIndexContent(threadid);
    }

    public Observable<BaseResponse<IndexPostModel>> putComment(String threadid, String comment) {
        IndexPostModel indexPostModel = new IndexPostModel();
        indexPostModel.setContent(comment);
        return mApi.postIndexPost(threadid, indexPostModel, PrefUtil.getAuthToken());

    }

    public Observable<BaseResponse<IdentifyModel>> doIdentifyOldUser(String username, String password) {
        return mApi.getIdentifyContent(username, password);

    }

    public Observable<BaseResponse<RetrieveModel>> doRetrieveUsername(Bundle bundle) {
        return mApi.doRetrieveUsername(bundle.getString(RetrieveActivity.BUNDLE_STU_NUM),
                bundle.getString(RetrieveActivity.BUNDLE_USERNAME),
                bundle.getString(RetrieveActivity.BUNDLE_REAL_NAME),
                bundle.getString(RetrieveActivity.BUNDLE_CID));

    }

    public Observable<BaseResponse<BaseModel>> resetPassword(Bundle bundle) {
        return mApi.resetPassword(bundle.getString(Constants.BUNDLE_UID), Constants.BUNDLE_TOKEN, Constants.PASSWORD);
    }

    public Observable<BaseResponse<BaseModel>> appealPassport(Bundle bundle) {
        return mApi.appealPassport(bundle.getString(Constants.BUNDLE_REGISTER_USERNAME),
                bundle.getString(Constants.BUNDLE_REGISTER_CID),
                bundle.getString(Constants.BUNDLE_REGISTER_REAL_NAME),
                bundle.getString(Constants.BUNDLE_REGISTER_STU_NUM),
                bundle.getString(Constants.BUNDLE_EMAIL),
                bundle.getString(Constants.BUNDLE_MESSAGE),
                bundle.getString(Constants.CAPTCHA_ID),
                bundle.getString(Constants.CAPTCHA_VALUE));
    }

    public Observable<BaseResponse<ThreadModel>> getThread(int threadId, int postPage) {
        return mApi.getThread(threadId + "", postPage + "");
    }

    public Observable<BaseResponse<List<MyReleaseModel>>> getMyReleaseList(int page) {
        return mApi.getMyReleaseList(getLatestAuthentication(), String.valueOf(page));
    }


}
