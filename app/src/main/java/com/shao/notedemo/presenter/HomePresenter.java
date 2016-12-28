package com.shao.notedemo.presenter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.shao.notedemo.contract.HomeContract;
import com.shao.notedemo.model.Note;
import com.shao.notedemo.model.NoteDao;
import com.shao.notedemo.util.DAO;

import java.util.List;

/**
 * Created by root on 16-12-26.
 */

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
