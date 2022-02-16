package com.istiaksaif.testapp.Model;

public class FeedComment {
    String commentPersonName;
    String commentText;
    Long commentTime;

    public Long getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(Long commentTime) {
        this.commentTime = commentTime;
    }

    public String getCommentPersonName() {
        return commentPersonName;
    }

    public void setCommentPersonName(String commentPersonName) {
        this.commentPersonName = commentPersonName;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }
}
