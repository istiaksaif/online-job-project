package com.istiaksaif.highlymotavated.Utils;

import com.istiaksaif.highlymotavated.Model.NotificationData;

public class PushNotification {
    private NotificationData data;
    private String to;

    public PushNotification(NotificationData data, String to) {
        this.data = data;
        this.to = to;
    }

    public NotificationData getData() {
        return data;
    }

    public void setData(NotificationData data) {
        this.data = data;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
