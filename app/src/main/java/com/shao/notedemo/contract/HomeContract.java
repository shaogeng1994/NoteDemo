package com.shao.notedemo.contract;

import com.shao.notedemo.model.Note;

import java.util.List;

/**
 * Created by root on 16-12-26.
 */

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
