package com.shao.notedemo.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
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

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener,
        HomeContract.View{
    public final static int REQUEST_EDIT = 1000;
    @BindView(R.id.home_recycler)
    RecyclerView recyclerView;
    @BindView(R.id.home_add)
    Button addButton;
    @BindView(R.id.home_swipe)
    SwipeRefreshLayout swipeRefreshLayout;
    private HomeContract.Presenter mPresenter;
    private Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new HomePresenter(this,this);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        swipeRefreshLayout.setOnRefreshListener(this);
        initRecycler();

        mPresenter.start();
    }

    private void initRecycler() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new Adapter(this,null);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                mPresenter.loadMoreNote();
            }
        });

        ItemDragAndSwipeCallback itemDragAndSwipeCallback = new ItemDragAndSwipeCallback(mAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemDragAndSwipeCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        mAdapter.enableSwipeItem();

        OnItemSwipeListener onItemSwipeListener = new OnItemSwipeListener() {
            @Override
            public void onItemSwipeStart(RecyclerView.ViewHolder viewHolder, int pos) {}
            @Override
            public void clearView(RecyclerView.ViewHolder viewHolder, int pos) {}
            @Override
            public void onItemSwiped(RecyclerView.ViewHolder viewHolder, int pos) {
                mPresenter.removeNote(mAdapter.getItem(pos).getId());
            }

            @Override
            public void onItemSwipeMoving(Canvas canvas, RecyclerView.ViewHolder viewHolder, float v, float v1, boolean b) {

            }
        };


        mAdapter.setOnItemSwipeListener(onItemSwipeListener);

        recyclerView.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                Note note = (Note) baseQuickAdapter.getItem(i);
                Intent intent = new Intent(getApplicationContext(),EditNoteActivity.class);
                intent.putExtra("id",note.getId());
                startActivityForResult(intent,REQUEST_EDIT);
            }
        });
    }

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
        swipeRefreshLayout.setRefreshing(isShow);
    }

    @Override
    public void getNoteSuccess(List<Note> notes) {
        mAdapter.setNewData(notes);
    }

    @Override
    public void loadMoreNoteSuccess(List<Note> notes) {
        mAdapter.addData(notes);
        mAdapter.loadMoreComplete();
    }

    @Override
    public void loadEnd(boolean canLoad) {
        if(canLoad) {

        }else {
//            mAdapter.setEnableLoadMore(canLoad);
            mAdapter.loadMoreEnd();
        }
    }

    private class Adapter extends BaseItemDraggableAdapter<Note,BaseViewHolder> {
        private Context context;
        public Adapter(Context context,List<Note> data) {
            super(R.layout.home_note_item, data);
            this.context = context;
        }

        @Override
        protected void convert(BaseViewHolder baseViewHolder, final Note note) {
            baseViewHolder.setText(R.id.home_note_item_title,note.getTitle());
            baseViewHolder.setText(R.id.home_note_item_content,note.getContent());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_EDIT && resultCode == RESULT_OK) {
            mPresenter.refreshNote();
        }
    }
}
