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
    private HomeContract.View mView;
    private int index = 0;
    private int size = 5;
    private boolean isEnd = false;
    private static Handler handler = new Handler();
    private NoteDao mNoteDao;

    public HomePresenter(Context context, HomeContract.View mView) {
        this.context = context;
        this.mView = mView;
        mNoteDao = DAO.getInstance(context).getSession().getNoteDao();
    }

    @Override
    public void refreshNote() {
        index = 0;
        isEnd = false;
        mView.showSwipe(true);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                List<Note> list = mNoteDao.queryBuilder().offset(index*size).limit(size).orderDesc(NoteDao.Properties.AddTime).list();
                mView.getNoteSuccess(list);
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
        if(isEnd) return;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                List<Note> list = mNoteDao.queryBuilder().offset((++index)*size).limit(size).orderDesc(NoteDao.Properties.AddTime).list();
                mView.loadMoreNoteSuccess(list);
                if(list.size()<size) {
                    isEnd = true;
                    mView.loadEnd(!isEnd);
                }
            }
        },1000);
    }

    @Override
    public void removeNote(Long id) {
        mNoteDao.deleteByKey(id);
    }
}
