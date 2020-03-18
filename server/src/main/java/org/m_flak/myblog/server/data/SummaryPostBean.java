package org.m_flak.myblog.server.data;

import java.io.Serializable;

public class SummaryPostBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private long postID;
    private long posterID;
    private String title;
    private String datePosted;

    public SummaryPostBean() {
    }

    public SummaryPostBean(long postID, long posterID, String title, String datePosted) {
        this.postID = postID;
        this.posterID = posterID;
        this.title = title;
        this.datePosted = datePosted;
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
}
