package com.comma.learnabout.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by fanqi on 2018/6/21.
 * Description:
 */

@Entity
public class ThreadInfo {

    @Id
    private long id;

    @Property(nameInDb = "URL")
    private String url;

    @Property(nameInDb = "START")
    private int start;

    @Property(nameInDb = "END")
    private int end;

    @Property(nameInDb = "FINISEDSIZE")
    private int finisedSize;

    @Generated(hash = 2130233517)
    public ThreadInfo(long id, String url, int start, int end, int finisedSize) {
        this.id = id;
        this.url = url;
        this.start = start;
        this.end = end;
        this.finisedSize = finisedSize;
    }

    @Generated(hash = 930225280)
    public ThreadInfo() {
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getStart() {
        return this.start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return this.end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getFinisedSize() {
        return this.finisedSize;
    }

    public void setFinisedSize(int finisedSize) {
        this.finisedSize = finisedSize;
    }

    @Override
    public String toString() {
        return "ThreadInfo{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", start=" + start +
                ", end=" + end +
                ", finisedSize=" + finisedSize +
                '}';
    }
}
