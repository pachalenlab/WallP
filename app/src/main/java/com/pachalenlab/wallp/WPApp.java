package com.pachalenlab.wallp;

import android.annotation.SuppressLint;
import android.app.Application;

import com.pachalenlab.wallp.module.WPCore;
import com.pachalenlab.wallp.module.WPLogger;

import org.androidannotations.annotations.EApplication;
import org.apache.log4j.Logger;

/**
 * Application class for WallP
 * Created by Niklane on 2016-01-12.
 */
@SuppressLint("Registered")
@EApplication
public class WPApp extends Application {
    private final Logger logger = Logger.getLogger(WPApp.class);

    @Override
    public void onCreate() {
        super.onCreate();
        WPLogger.configure(getApplicationContext());
        WPCore.getInstance();
        WPCore.imageLoaderConfig(this);
        WPCore.getInstance().loadData();
        /*
        WPService_.intent(getApplicationContext())
                .extra("runState", WPService.INIT)
                .start();
                */
        WPCore.scheduleWallpaperAlarm(getApplicationContext(), WPCore.getAppData().getTimeInterval());
        logger.info("App Initialized!!");
    }

    @Override
    public void onTerminate() {
        logger.info("App Terminated!!");
        super.onTerminate();
    }
}
