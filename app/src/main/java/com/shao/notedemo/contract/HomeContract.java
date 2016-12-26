package com.shao.notedemo.contract;

import com.shao.notedemo.model.Note;

import java.util.List;

/**
 * Created by root on 16-12-26.
 */

public interface HomeContract {
    interface View {
        void showSwipe(boolean isShow);
        void getNoteSuccess(List<Note> notes);
        void loadMoreNoteSuccess(List<Note> notes);
        void loadEnd(boolean canLoad);
    }
    interface Presenter {
        void refreshNote();
        void start();
        void loadMoreNote();
        void removeNote(Long id);
    }
}
