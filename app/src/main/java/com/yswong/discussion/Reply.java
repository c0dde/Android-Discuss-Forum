package com.yswong.discussion;

//This class is for create replies for firebase
public class Reply {
    private String threadContent;
    private String authorId;
    private String authorName;
    private String datetime;

    public Reply(){

    }

    public Reply(String threadContent, String authorId, String authorName, String datetime) {
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

