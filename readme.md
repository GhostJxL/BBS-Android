# 求实BBS-Android

## 应用介绍
  天津大学求实BBS社区客户端

## 项目介绍
1. 架构 ： MVP+RxJava2+Retrofit2+Dagger2+..
2. 基础设施：滑动返回、夜间模式、顶部提示..

## TODO-List
### Coding
- 公告的获取，有数据再提示
- 热门帖子等有数据再完善
- 用户验证模块需要测试一遍
- 提交申诉后跳转到哪里
- 获取关注的 _板块_ 列表与删除、添加
- 我的发布优化
- 我的收藏优化
- 获取消息列表的优化工作
- ~~修改密码（编辑资料的）~~
- 获取分区封面图
- 发帖
- ~~帖子内容以及回复内容~~
- 发表评论
- 帖子详情Item详细区分



### Preparing
- 获取验证码
- 个人信息（别人的
- 发送站内信
- 加好友机制
- 获取好友列表
- 个人回帖列表
- 修改帖子
- 删除帖子
- 修改评论
- 删除评论
- 下载图片、附件
- 上传图片、附件
-


### Debugging
- 夜间模式下的网络请求问题
- fragment的懒加载模式
- 帖子详情页的滑动和Behavior

## 项目结构


#### 关于模块
> 采用类模块化结构，所有功能模块统一依赖于commons，包括网络层，基础UI层，本地数据库，统一使用commons所提供的实现。添加模块时，可直接创建模块文件夹，继承/实现commons提供的基类/接口，

#### 关于夜间模式
   - 对于 *未* 自定义color/background...的View采用系统默认的配色
   - 对于 *已* 定义的color/background...的View需要在values-night添加相应的配色

#### 关于惯用手模式


#### 快速添加模块
```
添加任一功能模块时都需要在commons同级目录下新建对应package，任意两个package（功能模块）不应该相互依赖，
功能模块下仍可以创建所需的子package
```

##### 新建
- Activity : 任意可见的Activity都应列入对应的manifests、di依赖注入， 继承BaseActivity， 提供对应的Contract接口，Presenter 例如：
```
public class HomeActivity extends BaseActivity<HomePresenter> HomeContract.View {
    ......
}
```
```
public interface HomeContract {

    interface View extends BaseView{
        void showUpdateDialog(int versionCode);
    }

    interface Presenter extends BasePresenter<View>{
        void checkUpdate(int currentVersionCode);
    }
}
```
```
public class HomePresenter extends RxPresenter<HomeContract.View> implements HomeContract.Presenter{

    private RxDoHttpClient mHttpClient;

    @Inject
    public HomePresenter(RxDoHttpClient httpClient){
        LogUtil.d("HomePresenter is injected");
        this.mHttpClient = httpClient;
        mHttpClient.getDataList();
    }

    ...
}
```


- Fragment（作为容器）: 容器Fragment需要继承SimpleFragment重写对应函数
```
public class MainFragment extends SimpleFragment {
  ...
}
```

- Fragment（作为内容）: 作为内容提供者，需要继承BaseFragment, 并重写相应函数，类似与Activity需要提供Presenter，Contract
```
public class IndividualFragment extends BaseFragment<IndividualPresenter> implements IndividualContract.View {
  ....
}
```
- Presenter : 使用Dagger2依赖注入，任一Presneter 需要提供一个可注入的构造函数
```
public class IndividualPresenter extends RxPresenter<IndividualContract.View> implements IndividualContract.Presenter {

    @Inject
    public IndividualPresenter(){

    }
}
```
如果有参数，比如
```
@Inject
    public HomePresenter(RxDoHttpClient httpClient){
        LogUtil.d("HomePresenter is injected");
        this.mHttpClient = httpClient;
        mHttpClient.getDataList();
    }
```
需要在AppModule里提供对应Scope的对应实例
```
@Provides
 @Singleton
 RxDoHttpClient provideRxDoHttpClient(){
     return new RxDoHttpClient();
 }
```



- Adapter ：BaseAdapter目前只支持RecyclerView，并不完善，如有需要，可以使用。5.5

- Network ：


###

## 新特性

1. 惯用手模式

  即可支持左右单手模式。

  #### 定义
  > 我们把整个屏幕垂直等分3个区域，如果一个可操作的View处于两边的区域内，我们称这两个区域为单手盲区，
  那么在单手模式下，将进行对应的反方向定位该View, 使得用户在单手握手机的情况下，也能轻松操作该View.
  如果在同一水平方向上同时出现两个对称View分别处在两个盲区内，那么将考虑这两个View的操作频率而定，
  或者，在设计阶段尽力避免这样的情况。

  #### 优化事项
  - BottomBar、Toolbar上的操作对象暂不加入该功能。
2.
