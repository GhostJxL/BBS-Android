package com.twtstudio.bbs.bdpqchen.bbs.commons.rx;

import com.twtstudio.bbs.bdpqchen.bbs.auth.login.LoginModel;
import com.twtstudio.bbs.bdpqchen.bbs.auth.register.RegisterModel;
import com.twtstudio.bbs.bdpqchen.bbs.auth.register.old.RegisterOldModel;
import com.twtstudio.bbs.bdpqchen.bbs.auth.renew.identify.IdentifyModel;
import com.twtstudio.bbs.bdpqchen.bbs.auth.renew.identify.retrieve.RetrieveActivity;
import com.twtstudio.bbs.bdpqchen.bbs.auth.renew.identify.retrieve.RetrieveModel;
import com.twtstudio.bbs.bdpqchen.bbs.commons.model.BaseModel;
import com.twtstudio.bbs.bdpqchen.bbs.commons.support.Constants;
import com.twtstudio.bbs.bdpqchen.bbs.forum.ForumModel;
import com.twtstudio.bbs.bdpqchen.bbs.forum.boards.BoardsModel;
import com.twtstudio.bbs.bdpqchen.bbs.forum.boards.create_thread.CreateThreadModel;
import com.twtstudio.bbs.bdpqchen.bbs.forum.boards.thread.PostModel;
import com.twtstudio.bbs.bdpqchen.bbs.forum.boards.thread.ThreadModel;
import com.twtstudio.bbs.bdpqchen.bbs.forum.boards.thread_list.ThreadListModel;
import com.twtstudio.bbs.bdpqchen.bbs.individual.message.model.MessageModel;
import com.twtstudio.bbs.bdpqchen.bbs.individual.model.IndividualInfoModel;
import com.twtstudio.bbs.bdpqchen.bbs.individual.my_release.my_reply.MyReplyModel;
import com.twtstudio.bbs.bdpqchen.bbs.main.content.ContentModel;
import com.twtstudio.bbs.bdpqchen.bbs.main.content.post.IndexPostModel;
import com.twtstudio.bbs.bdpqchen.bbs.main.model.LatestPostModel;
import com.twtstudio.bbs.bdpqchen.bbs.main.model.MainModel;
import com.twtstudio.bbs.bdpqchen.bbs.individual.my_release.MyReleaseModel;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

import static com.twtstudio.bbs.bdpqchen.bbs.commons.support.Constants.ANONYMOUS;
import static com.twtstudio.bbs.bdpqchen.bbs.commons.support.Constants.CONTENT;
import static com.twtstudio.bbs.bdpqchen.bbs.commons.support.Constants.REPLY_ID;
import static com.twtstudio.bbs.bdpqchen.bbs.commons.support.Constants.TITLE;

/**
 * Created by bdpqchen on 17-4-27.
 */

public interface BaseApi {

    String header = Constants.NET_RETROFIT_HEADER_TITLE;

    @FormUrlEncoded
    @POST("passport/login")
    Observable<BaseResponse<LoginModel>> doLogin(@Field("username") String username, @Field("password") String password);

    @GET("forum")
    Observable<BaseResponse<List<ForumModel>>> getForums();

    @FormUrlEncoded
    @POST("passport/register/new")
    Observable<BaseResponse<RegisterModel>> doRegister(@Field(Constants.BUNDLE_REGISTER_USERNAME) String string,
                                                       @Field(Constants.BUNDLE_REGISTER_CID) String string1,
                                                       @Field(Constants.BUNDLE_REGISTER_PASSWORD) String string2,
                                                       @Field(Constants.BUNDLE_REGISTER_STU_NUM) String string3,
                                                       @Field(Constants.BUNDLE_REGISTER_REAL_NAME) String string4);

    @FormUrlEncoded
    @POST("passport/register/old")
    Observable<BaseResponse<RegisterOldModel>> doRegisterOld(
            @Field(Constants.TOKEN) String token,
            @Field(Constants.BUNDLE_REGISTER_USERNAME) String string,
            @Field(Constants.BUNDLE_REGISTER_PASSWORD) String string2,
            @Field(Constants.BUNDLE_REGISTER_CID) String string1,
            @Field(Constants.BUNDLE_REGISTER_REAL_NAME) String string4);

    @GET("home")
    Observable<BaseResponse<IndividualInfoModel>> getIndividualInfo(@Header(Constants.NET_RETROFIT_HEADER_TITLE) String idAndToken);

    @FormUrlEncoded
    @PUT("home")
    Observable<BaseResponse<BaseModel>> doUpdateInfoNickname(
            @Header(Constants.NET_RETROFIT_HEADER_TITLE) String idAndToken,
            @Field(Constants.BUNDLE_NICKNAME) String nickname
    );

    @FormUrlEncoded
    @PUT("home")
    Observable<BaseResponse<BaseModel>> doUpdateInfoSignature(
            @Header(Constants.NET_RETROFIT_HEADER_TITLE) String idAndToken,
            @Field(Constants.BUNDLE_SIGNATURE) String signature
    );

    @FormUrlEncoded
    @PUT("home")
    Observable<BaseResponse<BaseModel>> doUpdateInfoAll(
            @Header(Constants.NET_RETROFIT_HEADER_TITLE) String idAndToken,
            @Field(Constants.BUNDLE_SIGNATURE) String signature,
            @Field(Constants.BUNDLE_NICKNAME) String nickname
    );


    @Multipart
    @PUT("home/avatar")
    Observable<BaseResponse<BaseModel>> doUpdateAvatar(
            @Header(Constants.NET_RETROFIT_HEADER_TITLE) String latestAuthentication,
            @Part List<MultipartBody.Part> partList);


    @GET("index")
    Observable<BaseResponse<LatestPostModel.DataBean>> getLatestPost();

    @GET("forum/{forumId}")
    Observable<BaseResponse<BoardsModel>> getBoardList(@Path("forumId") String forumId);

    @GET("board/{boardId}/page/{page}")
    Observable<BaseResponse<ThreadListModel>> getThreadList(
            @Header(Constants.NET_RETROFIT_HEADER_TITLE) String idAndToken,
            @Header(Constants.NET_RETROFIT_HEADER_REQUEST) String requestedWith,
            @Path("boardId") String boardId,
            @Path("page") String page);

    @GET("home/message/page/{page}")
    Observable<BaseResponse<List<MessageModel>>> getMessageList(
            @Header(Constants.NET_RETROFIT_HEADER_TITLE) String latestAuthentication,
            @Path("page") String page);

    @GET("index")
    Observable<BaseResponse<MainModel>> getTopTen();

    @GET("thread/{threadid}/page/0")
    Observable<BaseResponse<ContentModel.DataBean>> getIndexContent(@Path("threadid") String threadid);


    @PUT("thread/{threadid}/page/0")
    Observable<BaseResponse<IndexPostModel>> postIndexPost(@Path("threadid") String threadid, @Body IndexPostModel indexPostModel, @Header(Constants.NET_RETROFIT_HEADER_TITLE) String idAndToken);

    @FormUrlEncoded
    @POST("passport/login/old")
    Observable<BaseResponse<IdentifyModel>> getIdentifyContent(@Field(Constants.BUNDLE_REGISTER_USERNAME) String username,
                                                               @Field(Constants.BUNDLE_REGISTER_PASSWORD) String password);

    @FormUrlEncoded
    @POST("passport/retrieve")
    Observable<BaseResponse<RetrieveModel>> doRetrieveUsername(@Field(RetrieveActivity.BUNDLE_STU_NUM) String string,
                                                               @Field(RetrieveActivity.BUNDLE_USERNAME) String string1,
                                                               @Field(RetrieveActivity.BUNDLE_REAL_NAME) String string2,
                                                               @Field(RetrieveActivity.BUNDLE_CID) String string3);

    @FormUrlEncoded
    @POST("passport/reset-pass")
    Observable<BaseResponse<BaseModel>> resetPassword(@Field(Constants.BUNDLE_UID) String uid,
                                                      @Field(Constants.BUNDLE_TOKEN) String token,
                                                      @Field(Constants.PASSWORD) String password);

    @FormUrlEncoded
    @POST("passport/appeal")
    Observable<BaseResponse<BaseModel>> appealPassport(String string, String string1, String string2, String string3, String string4, String string5, String string6, String string7);

    @GET("thread/{thread}/page/{page}")
    Observable<BaseResponse<ThreadModel>> getThread(@Path("thread") String threadId,
                                                    @Path("page") String postPage);

    @GET("home/publish/thread/page/{page}")
    Observable<BaseResponse<List<MyReleaseModel>>> getMyReleaseList(@Header(Constants.NET_RETROFIT_HEADER_TITLE) String idAndToken, @Path("page") String page);

    @GET("home/publish/post/page/{page}")
    Observable<BaseResponse<List<MyReplyModel>>> getMyReplyList(@Header(Constants.NET_RETROFIT_HEADER_TITLE) String idAndToken, @Path("page") String page);

    @FormUrlEncoded
    @POST("board/{bid}")
    Observable<BaseResponse<CreateThreadModel>> doPublishThread(
            @Header(Constants.NET_RETROFIT_HEADER_TITLE) String latestAuthentication,
            @Path("bid") int anInt,
            @Field(TITLE) String string,
            @Field(Constants.CONTENT) String string1);

    @FormUrlEncoded
    @POST("thread/{tid}")
    Observable<BaseResponse<PostModel>> doComment(
            @Header(Constants.NET_RETROFIT_HEADER_TITLE) String latestAuthentication,
            @Path("tid") int threadId,
            @Field(CONTENT) String comment,
            @Field(REPLY_ID) int reply);

    @FormUrlEncoded
    @POST("thread/{tid}")
    Observable<BaseResponse<PostModel>> doCommentAnonymous(
            @Path("tid") int threadId,
            @Field(CONTENT) String comment,
            @Field(REPLY_ID) int reply,
            @Field(ANONYMOUS) int is);

    @FormUrlEncoded
    @POST("home/collection")
    Observable<BaseResponse<BaseModel>> starThread(
            @Header(Constants.NET_RETROFIT_HEADER_TITLE) String head,
            @Field(Constants.TID) int id);

    @DELETE("home/collection/{tid}")
    Observable<BaseResponse<BaseModel>> unStarThread(
            @Header(header) String latestAuthentication,
            @Path(Constants.TID) int id);

    @POST("home/message/read")
    Observable<BaseResponse<BaseModel>> doClearUnreadMessage();

    @FormUrlEncoded
    @POST("board/{bid}")
    Observable<BaseResponse<CreateThreadModel>> doPublishThreadAnonymous(
            @Path("bid") int anInt,
            @Field(TITLE) String string,
            @Field(CONTENT) String string1,
            @Field(ANONYMOUS) int is);


}

