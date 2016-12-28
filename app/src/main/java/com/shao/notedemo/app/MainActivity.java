package com.shao.notedemo.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.icu.text.SimpleDateFormat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnItemSwipeListener;
import com.shao.notedemo.R;
import com.shao.notedemo.contract.HomeContract;
import com.shao.notedemo.model.Note;
import com.shao.notedemo.presenter.HomePresenter;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
