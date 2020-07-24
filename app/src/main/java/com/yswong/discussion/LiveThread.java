package com.yswong.discussion;

import java.io.Serializable;

//This class is for create threads for firebase
public class LiveThread implements Serializable {
    private String threadContent;
    private String authorId;
    private String authorName;
    private String datetime;

    public LiveThread()
    {

    }

    public LiveThread(String threadContent, String authorId, String authorName, String datetime) {
        this.threadContent = threadContent;
        this.authorId = authorId;
        this.authorName = authorName;
        this.datetime = datetime;
    }


    public String getThreadContent() {
        return threadContent;
    }

    public String getAuthorId() {
        return authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getDatetime() {
        return datetime;
    }
}

