package com.shao.notedemo.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

import java.io.Serializable;

/**
 * Created by root on 16-12-26.
 */
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
