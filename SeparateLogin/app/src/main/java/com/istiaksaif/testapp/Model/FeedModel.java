package com.istiaksaif.testapp.Model;

import java.io.Serializable;

public class FeedModel implements Serializable {
    String feedPerson;
    String feedType;
    String feedText;
    String feedImg;
    Long feedDate;
    int numberofComments;
    String feedId;
    String thumbPath;

    public String getThumbPath() {
        return thumbPath;
    }

    public void setThumbPath(String thumbPath) {
        this.thumbPath = thumbPath;
    }

    public String getFeedId() {
        return feedId;
    }

    public void setFeedId(String feedId) {
        this.feedId = feedId;
    }

    public int getNumberofComments() {
        return numberofComments;
    }

    public void setNumberofComments(int numberofComments) {
        this.numberofComments = numberofComments;
    }

    public Long getFeedDate() {
        return feedDate;
    }

    public void setFeedDate(Long feedDate) {
        this.feedDate = feedDate;
    }

    public String getFeedPerson() {
        return feedPerson;
    }

    public void setFeedPerson(String feedPerson) {
        this.feedPerson = feedPerson;
    }

    public String getFeedType() {
        return feedType;
    }

    public void setFeedType(String feedType) {
        this.feedType = feedType;
    }

    public String getFeedText() {
        return feedText;
    }

    public void setFeedText(String feedText) {
        this.feedText = feedText;
    }

    public String getFeedImg() {
        return feedImg;
    }

    public void setFeedImg(String feedImg) {
        this.feedImg = feedImg;
    }
}
