package com.comma.learnabout.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

import java.io.Serializable;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by fanqi on 2018/6/21.
 * Description:
 */

@Entity
public class FileInfo implements Serializable{

    static final long serialVersionUID = 42L;

    @Id
    private long id;

    @Property(nameInDb = "FILENAME")
    private String fileName;

    @Property(nameInDb = "FILEURL")
    private String fileUrl;

    @Property(nameInDb = "LENGTH")
    private int length;

    @Property(nameInDb = "FINISEDSIZE")
    private int finisedSize;

    @Property(nameInDb = "FINISHED")
    private boolean finished;

    @Generated(hash = 1368364996)
    public FileInfo(long id, String fileName, String fileUrl, int length,
            int finisedSize, boolean finished) {
        this.id = id;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.length = length;
        this.finisedSize = finisedSize;
        this.finished = finished;
    }

    @Generated(hash = 1367591352)
    public FileInfo() {
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileUrl() {
        return this.fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public int getLength() {
        return this.length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getFinisedSize() {
        return this.finisedSize;
    }

    public void setFinisedSize(int finisedSize) {
        this.finisedSize = finisedSize;
    }

    public boolean getFinished() {
        return this.finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }


}
