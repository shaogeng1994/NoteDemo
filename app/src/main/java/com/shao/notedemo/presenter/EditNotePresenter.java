package com.shao.notedemo.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.shao.notedemo.contract.EditNoteContract;
import com.shao.notedemo.model.Note;
import com.shao.notedemo.model.NoteDao;
import com.shao.notedemo.util.DAO;

/**
 * Created by root on 16-12-26.
 */

public class EditNotePresenter implements EditNoteContract.Presenter {
    private Context context;
    private EditNoteContract.View mView;
    private Long id;
    private NoteDao noteDao;

    public EditNotePresenter(Context context,EditNoteContract.View mView) {
        this.context = context;
        this.mView = mView;
        noteDao = DAO.getInstance(context).getSession().getNoteDao();
    }


    @Override
    public void editNote(String title, String content) {
        if(TextUtils.isEmpty(title)) {
            mView.showToast("输入标题");
            return;
        }
        if(TextUtils.isEmpty(content)) {
            mView.showToast("输入内容");
            return;
        }
        if(id == null || id ==-1) {
            Note note = new Note(null,System.currentTimeMillis(),title,content);
            Long id = noteDao.insert(note);
            if(id!=null) {
                mView.showToast(id+"");
                mView.editNoteSuccess();
            }else {
                mView.showToast("添加失败");
            }
        }else {
            Note note = noteDao.queryBuilder().where(NoteDao.Properties.Id.eq(id)).build().unique();
            note.setContent(content);
            note.setTitle(title);
            noteDao.update(note);
            mView.editNoteSuccess();
        }
    }

    @Override
    public void start() {
        Intent intent = ((Activity) context).getIntent();
        if(intent.hasExtra("id")) {
            id = intent.getLongExtra("id",-1);
            Note note = noteDao.queryBuilder().where(NoteDao.Properties.Id.eq(id)).build().unique();
            mView.setContent(note.getContent());
            mView.setTitle(note.getTitle());
        }
    }
}
