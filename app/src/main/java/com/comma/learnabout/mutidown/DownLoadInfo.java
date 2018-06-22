package com.comma.learnabout.mutidown;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by fanqi on 2018/6/22.
 * Description:每个子线程下载信息实体类
 */

@Entity
public class DownLoadInfo {

    @Id
    private long id;

    @Property(nameInDb = "DOWNURL")
    private String downUrl;

    @Property(nameInDb = "STARTLOCATION")
    private int startLocation;

    @Property(nameInDb = "ENDLOCATION")
    private int endLocation;

    @Property(nameInDb = "FILESIZE")
    private int fileSize;

    @Property(nameInDb = "FINISEDSIZE")
    private int finisedSize;

    @Generated(hash = 761654235)
    public DownLoadInfo(long id, String downUrl, int startLocation, int endLocation,
            int fileSize, int finisedSize) {
        this.id = id;
        this.downUrl = downUrl;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.fileSize = fileSize;
        this.finisedSize = finisedSize;
    }

    @Generated(hash = 1743687477)
    public DownLoadInfo() {
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDownUrl() {
        return this.downUrl;
    }

    public void setDownUrl(String downUrl) {
        this.downUrl = downUrl;
    }

    public int getStartLocation() {
        return this.startLocation;
    }

    public void setStartLocation(int startLocation) {
        this.startLocation = startLocation;
    }

    public int getEndLocation() {
        return this.endLocation;
    }

    public void setEndLocation(int endLocation) {
        this.endLocation = endLocation;
    }

    public int getFileSize() {
        return this.fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public int getFinisedSize() {
        return this.finisedSize;
    }

    public void setFinisedSize(int finisedSize) {
        this.finisedSize = finisedSize;
    }
}
