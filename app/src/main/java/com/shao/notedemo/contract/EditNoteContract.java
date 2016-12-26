package com.shao.notedemo.contract;

/**
 * Created by root on 16-12-26.
 */

public interface EditNoteContract {
    interface View {
        void editNoteSuccess();
        void showToast(String msg);
        void setTitle(String title);
        void setContent(String content);
    }
    interface Presenter {
        void editNote(String title,String content);
        void start();
    }
}
