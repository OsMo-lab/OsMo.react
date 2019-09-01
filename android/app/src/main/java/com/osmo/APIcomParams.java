package com.osmo;
import java.io.File;

import android.app.Notification;
import android.support.v4.app.NotificationCompat.Builder;
public class APIcomParams
    {
        String action;
        String post;
        String command;
        File uploadfile;
        Builder notificationBuilder;
        int notification;
        public APIcomParams(String action, String post, String command, File uploadfile, Builder notificationBuilder, int notificationid)
            {
                this.action = action;
                this.post = post;
                this.command = command;
                this.uploadfile = uploadfile;
                this.notificationBuilder = notificationBuilder;
                this.notification = notificationid;
            }
        public APIcomParams(String action, String post, String command)
            {
                this.action = action;
                this.post = post;
                this.command = command;
            }

    }
