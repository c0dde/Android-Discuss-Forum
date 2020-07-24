package com.yswong.discussion;

import java.io.Serializable;

//This class is for create/retrieve threads for firebase
public class Thread implements Serializable {
    private String threadSubject;
    private String threadContent;
    private String authorId;
    private String authorName;
    private String datetime;

    public Thread()
    {

    }

    public Thread(String threadSubject, String threadContent, String authorId, String authorName, String datetime) {
        this.threadSubject = threadSubject;
        this.threadContent = threadContent;
        this.authorId = authorId;
        this.authorName = authorName;
        this.datetime = datetime;
    }

    public String getThreadSubject() {
        return threadSubject;
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

