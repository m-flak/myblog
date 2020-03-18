package org.m_flak.myblog.server.data;

import java.io.Serializable;

public class PostBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private long postID;
    private long posterID;
    private String title;
    private String datePosted;
    private String contents;

    public PostBean() {
    }

    public PostBean(long postID, long posterID, String title, String datePosted,
                        String contents) {
        this.postID = postID;
        this.posterID = posterID;
        this.title = title;
        this.datePosted = datePosted;
        this.contents = contents;
    }

    public void setPostID(long postID) {
        this.postID = postID;
    }
    public long getPostID() {
        return postID;
    }

    public void setPosterID(long posterID) {
        this.posterID = posterID;
    }
    public long getPosterID() {
        return posterID;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public String getTitle() {
        return title;
    }

    public void setDatePosted(String datePosted) {
        this.datePosted = datePosted;
    }
    public String getDatePosted() {
        return datePosted;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }
    public String getContents() {
        return contents;
    }
}
