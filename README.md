# Android开发之ButterKnife，GreenDao简单运用，以及推荐并使用一个好用的开源库BaseRecyclerViewAdapterHelper。


##### 在实习的这段时间里，完成了几个app的快速开发，最近闲下来，无聊写了一个NoteDemo，主要使用了一些三方框架,并使用了MVP模式，经验不足，写下第一篇博客和大家交流学习。
> 1. 一个好用的依赖注入框架[ButterKnife](https://github.com/JakeWharton/butterknife)
> 2. 一个ORM框架[GreenDao](https://github.com/greenrobot/greenDAO)
> 3. 一个快速强大的recycler帮助框架[BaseRecyclerViewAdapterHelper](https://github.com/CymChad/BaseRecyclerViewAdapterHelper)


## ButterKnife
##### 在之前的项目中，findviewbyid什么的，写到手断，一个界面有十几二十个控件绑定。在这次的demo中也是第一次使用，发现实在是太简洁了不少。

> 在gradle中添加
> ```
>  apply plugin: 'com.jakewharton.butterknife'
>  dependencies {
>    compile 'com.jakewharton:butterknife:8.4.0'
>    annotationProcessor 'com.jakewharton:butterknife-compiler:8.4.0'
>  }
> ```
> 在root中添加
> ```
> buildscript {
>  repositories {
>    mavenCentral()
>   }
>  dependencies {
>    classpath 'com.jakewharton:butterknife-gradle-plugin:8.4.0'
>  }
>}
> ```

##### 本来绑定是：
```
private EditText username;
username = findViewById(R.id.user);
```
##### 使用了ButterKnife：

```
@BindView(R.id.user) EditText username;
```

##### **还可以绑定onClick监听：**

```
@OnClick(R.id.home_add)
    void add() {
        Intent intent = new Intent(this,EditNoteActivity.class);
        startActivityForResult(intent,REQUEST_EDIT);
    }
```


## GreenDao

> greenDAO is a light & fast ORM for Android that maps objects to SQLite databases. Being highly optimized for Android, greenDAO offers great performance and consumes minimal memory.
>
> greenDAO是一个将对象映射到SQLite数据库的轻量、快速的ORM框架。对Android高度优化，greenDAO拥有高性能低内存消耗。

##### 在gradle添加
```
  buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath 'org.greenrobot:greendao-gradle-plugin:3.2.1'
    }
  }
```
```
  apply plugin: 'org.greenrobot.greendao'
  dependencies {
    compile 'org.greenrobot:greendao:3.2.0'
  }
```

##### 创建一个Note的实体类
```
 @Entity
public class Note {
    @Id
    private Long id;

    private Long addTime;

    private String title;

    private String content;
}
```
##### build一下，自动编译生成一些get、set方法和NoteDao类。
```
 @Entity
public class Note {
    @Id
    private Long id;

    private Long addTime;

    private String title;

    private String content;

    @Generated(hash = 128193520)
    public Note(Long id, Long addTime, String title, String content) {
        this.id = id;
        this.addTime = addTime;
        this.title = title;
        this.content = content;
    }

    @Generated(hash = 1272611929)
    public Note() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAddTime() {
        return this.addTime;
    }

    public void setAddTime(Long addTime) {
        this.addTime = addTime;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

```
##### 于是可以开始做简单的查询操作
```
//获取一个Session对象，即和数据库的连接。
DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(context, "note.db", null);
DaoMaster daoMaster = new DaoMaster(devOpenHelper.getWritableDb());
daoSession = daoMaster.newSession();

//获取NoteDao，就可以直接对NoteDao进行增删改查操作了。
NoteDao mNoteDao = daoSession.getNoteDao();

//查询Note，按addtime降序查询。
List<Note> list = mNoteDao.queryBuilder().offset(index*size).limit(size).orderDesc(NoteDao.Properties.AddTime).list();
```

##### **其他相关操作可以查看 [greenDao的文档](http://greenrobot.org/greendao/documentation/)**


## BaseRecyclerViewAdapterHelper

#### 主要特性
1. 优化Adapter代码
2. 添加Item事件
3. 添加列表加载动画
4. 添加头部、尾部
5. 上拉加载
6. 添加分组
7. 自定义不同的item类型
8. 设置空布局
9. 添加拖拽、滑动删除
10. 分组的伸缩栏
11. 自定义ViewHolder

#### 使用
> ##### 在gradle添加 
>  ```
>    allprojects {
>        repositories {
>            ...
>            maven { url "https://jitpack.io" }
>        }
>    }
>  ```
> ```
> dependencies {
>            compile 'com.github.CymChad:BaseRecyclerViewAdapterHelper:VERSION_CODE'
> }
> ```
##### 把VERSION_CODE替换成版本号，我这里用的是2.6.7
##### 使用方法可以查看代码，或者查看他们的 [文档](https://github.com/CymChad/BaseRecyclerViewAdapterHelper/wiki/%E9%A6%96%E9%A1%B5)


## 具体代码

##### 我这里主要采用了MVP模式，类比较多，直放上个别代码，具体看[这里](https://github.com/shaogeng1994/NoteDemo)。

#### HomeContract类,包含View和Presenter
```
public interface HomeContract {
    interface View {
        /**
         * 设置SwipeRefreshLayout的刷新动画显示
         * @param isShow 是否显示
         */
        void showSwipe(boolean isShow);

        /**
         * 获取Note成功
         * @param notes note列表
         */
        void getNoteSuccess(List<Note> notes);

        /**
         * 加载更多note成功
         * @param notes note列表
         */
        void loadMoreNoteSuccess(List<Note> notes);

        /**
         * 列表到底
         * @param canLoad 能否加载更多
         */
        void loadEnd(boolean canLoad);
    }
    interface Presenter {
        /**
         * 刷新note列表
         */
        void refreshNote();

        /**
         * 初始化方法
         */
        void start();

        /**
         * 加载更多note
         */
        void loadMoreNote();

        /**
         * 删除note
         * @param id note id
         */
        void removeNote(Long id);
    }
}
```

#### HomePresenter类,实现HomeContract.Presenter接口 

```
public class HomePresenter implements HomeContract.Presenter {
    private Context context;
    /**
     * view 控制视图
     */
    private HomeContract.View mView;
    /**
     * 分页的当前页
     */
    private int index = 0;
    /**
     * 每页的数量
     */
    private int size = 8;
    /**
     * 是否到底
     */
    private boolean isEnd = false;
    /**
     * 加载线程
     */
    private static Handler handler = new Handler();
    /**
     * 用来操作数据库的对象
     */
    private NoteDao mNoteDao;

    public HomePresenter(Context context, HomeContract.View mView) {
        this.context = context;
        this.mView = mView;
        //初始化NoteDao
        mNoteDao = DAO.getInstance(context).getSession().getNoteDao();
    }

    @Override
    public void refreshNote() {
        //刷新重置页面
        index = 0;
        isEnd = false;

        //开启刷新动画
        mView.showSwipe(true);

        //延时1秒执行查询并传送数据到view中
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                List<Note> list = mNoteDao.queryBuilder().offset(index*size).limit(size).orderDesc(NoteDao.Properties.AddTime).list();
                mView.getNoteSuccess(list);

                //关闭刷新动画
                mView.showSwipe(false);
            }
        },1000);
    }

    @Override
    public void start() {
        refreshNote();
    }

    @Override
    public void loadMoreNote() {
        //如果到底则不加载
        if(isEnd) return;

        //延时1秒执行查询并传送数据到view中
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //查询
                List<Note> list = mNoteDao.queryBuilder().offset((++index)*size).limit(size).orderDesc(NoteDao.Properties.AddTime).list();

                //传送数据到view中
                mView.loadMoreNoteSuccess(list);

                //如果查询结果小于每页数量，则到底，告诉view不能加载了。
                if(list.size()<size) {
                    isEnd = true;
                    mView.loadEnd(!isEnd);
                }
            }
        },1000);
    }

    @Override
    public void removeNote(Long id) {
        //根据id删除note
        mNoteDao.deleteByKey(id);
    }
}
```

#### MainActivity类
```
public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener,
        HomeContract.View{
    /**
     * 编辑或添加的activity的请求flag
     */
    public final static int REQUEST_EDIT = 1000;
    /**
     * 绑定recyclerview
     */
    @BindView(R.id.home_recycler)
    RecyclerView recyclerView;
    /**
     * 绑定添加按钮
     */
    @BindView(R.id.home_add)
    Button addButton;
    /**
     * 绑定SwipeRefreshLayout，下拉刷新控件
     */
    @BindView(R.id.home_swipe)
    SwipeRefreshLayout swipeRefreshLayout;
    /**
     * 声明Presenter
     */
    private HomeContract.Presenter mPresenter;
    /**
     * recycler的adapter
     */
    private Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化presenter
        mPresenter = new HomePresenter(this,this);
        setContentView(R.layout.activity_main);
        //绑定控件
        ButterKnife.bind(this);
        //绑定刷新监听
        swipeRefreshLayout.setOnRefreshListener(this);
        initRecyclerView();

        mPresenter.start();
    }

    private void initRecyclerView() {
        //设置recyclerview的layoutManager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //初始化adapter
        mAdapter = new Adapter(this,null);
        recyclerView.setAdapter(mAdapter);

        //设置加载更多监听
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                //加载更多
                mPresenter.loadMoreNote();
            }
        });

        //设置侧滑、拖拽
        ItemDragAndSwipeCallback itemDragAndSwipeCallback = new ItemDragAndSwipeCallback(mAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemDragAndSwipeCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        mAdapter.enableSwipeItem();

        //初始化侧滑监听
        OnItemSwipeListener onItemSwipeListener = new OnItemSwipeListener() {
            @Override
            public void onItemSwipeStart(RecyclerView.ViewHolder viewHolder, int pos) {}
            @Override
            public void clearView(RecyclerView.ViewHolder viewHolder, int pos) {}
            @Override
            public void onItemSwiped(RecyclerView.ViewHolder viewHolder, int pos) {
                //删除note
                mPresenter.removeNote(mAdapter.getItem(pos).getId());
            }

            @Override
            public void onItemSwipeMoving(Canvas canvas, RecyclerView.ViewHolder viewHolder, float v, float v1, boolean b) {

            }
        };

        //绑定侧滑监听
        mAdapter.setOnItemSwipeListener(onItemSwipeListener);

        //添加点击监听
        recyclerView.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                //把note的id传入intent，启动编辑的activity
                Note note = (Note) baseQuickAdapter.getItem(i);
                Intent intent = new Intent(getApplicationContext(),EditNoteActivity.class);
                intent.putExtra("id",note.getId());
                startActivityForResult(intent,REQUEST_EDIT);
            }
        });
    }

    //绑定添加按钮的监听
    @OnClick(R.id.home_add)
    void add() {
        Intent intent = new Intent(this,EditNoteActivity.class);
        startActivityForResult(intent,REQUEST_EDIT);
    }

    @Override
    public void onRefresh() {
        mPresenter.refreshNote();
    }

    @Override
    public void showSwipe(boolean isShow) {
        //设置刷新动画是否开启
        swipeRefreshLayout.setRefreshing(isShow);
    }

    @Override
    public void getNoteSuccess(List<Note> notes) {
        //重新设置新的数据并刷新
        mAdapter.setNewData(notes);
    }

    @Override
    public void loadMoreNoteSuccess(List<Note> notes) {
        //加入更多数据
        mAdapter.addData(notes);
        mAdapter.loadMoreComplete();
    }

    @Override
    public void loadEnd(boolean canLoad) {
        if(canLoad) {

        }else {
//            mAdapter.setEnableLoadMore(canLoad);
            //显示没有更多数据
            mAdapter.loadMoreEnd();
        }
    }



    /**
     * BaseItemDraggableAdapter是一个可以拖拽的适配器，如果不需要可以继承BaseQuickAdapter。
     */
    private class Adapter extends BaseItemDraggableAdapter<Note,BaseViewHolder> {
        private Context context;
        public Adapter(Context context,List<Note> data) {
            //传入布局
            super(R.layout.home_note_item, data);
            this.context = context;
        }

        @Override
        protected void convert(BaseViewHolder baseViewHolder, final Note note) {
            //设置标题
            baseViewHolder.setText(R.id.home_note_item_title,note.getTitle());

            //设置内容
            baseViewHolder.setText(R.id.home_note_item_content,note.getContent());

            //设置时间
            java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date(note.getAddTime());
            baseViewHolder.setText(R.id.home_note_item_time,formatter.format(date));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //请求编辑并且返回成功则刷新note列表
        if(requestCode == REQUEST_EDIT && resultCode == RESULT_OK) {
            mPresenter.refreshNote();
        }
    }

}
```


##### 临时看了markdown，写下这篇，甚是丑陋。简简单单只分享了自己近日来的一些收获，和大家学习分享自己的小小经验，提升一下自己。
##### 代码已经放到github上了：[https://github.com/shaogeng1994/NoteDemo](https://github.com/shaogeng1994/NoteDemo)
##### mail:<1002919029@qq.com>
