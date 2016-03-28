package com.pachalenlab.wallp.module;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.UiThread;
import org.apache.log4j.Logger;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Service for WallP
 * Set wallpaper by interval
 * Created by Niklane on 2016-01-12.
 */


@SuppressLint("Registered")
@EService
public class WPService extends Service {
    private final Logger logger = Logger.getLogger(WPService.class);

    /* Constants for Control rpm service status */
    public static final int INIT = 1;
    public static final int START = 2;
    public static final int STOP = 3;

    public enum Action{
        START, STOP
    }

    public enum State{
        INIT{
            public State act(Action action){
                switch (action){
                    case START:
                        return RUNNING;
                    default:
                        return null;
                }
            }

            @Override
            public void onEntry() {
                super.onEntry();
            }
        },
        SUSPEND{
            public State act(Action action){
                switch(action){
                    case START:
                        return RUNNING;
                    default:
                        return null;
                }
            }

            @Override
            public void onEntry() {
                super.onEntry();
                logger.info("Start Service");
                final int INTERVEL_IN_MINIUTE = WPCore.getAppData().getTimeInterval();

                mWallpaperTimer = new Timer();
                mWallpaperTask = new WallpaperTask();
                mWallpaperTimer.schedule(mWallpaperTask, 0, INTERVEL_IN_MINIUTE * 60 * 1000);

                logger.info("Run Timer, Interval = " + INTERVEL_IN_MINIUTE + "min");

                IS_SERVICE_RUNNING = true;
            }
        },
        RUNNING{
            public State act(Action action){
                switch (action){
                    case START:
                        return RUNNING;
                    case STOP:
                        return SUSPEND;
                    default:
                        return null;
                }
            }

            @Override
            public void onEntry() {
                super.onEntry();
            }
        };
        private static final Logger logger = Logger.getLogger(State.class);
        abstract State act(Action action);
        public static State getInitState(){
            return INIT;
        }
        private static Timer mWallpaperTimer;
        private static WallpaperTask mWallpaperTask;

        public void onEntry(){}
        public void onExit(){}
    }
    /* Service Running Indicator*/
    public static boolean IS_SERVICE_RUNNING = false;

    private State currentState;

    class WallpaperTask extends TimerTask {
        @Override
        public void run() {
            if(WPCore.getAppData().mWallpaperPaths.size() != 0) {
                WPCore.setBackGround("file://" + WPCore.getAppData().getWallpaperPaths()
                                .get(WPCore.getAppData().getNextWallpaper())
                        , getApplicationContext());
                WPCore.getAppData().setNextWallpaper();
                logger.info("Wallpaper Changed!");
            }
        }
    }

    private void startWPService() {

    }

    private void stopWPService() {
        logger.info("Stop Service");
        mWallpaperTimer.cancel();

        IS_SERVICE_RUNNING = false;
    }

    private void initWPService() {
        logger.info("Init Service");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        logger.info("onStartCommand Start");

        int runStatus = intent.getIntExtra("runState", INIT);
        logger.info("Command = " + runStatus);
        switch (runStatus) {
            case INIT:
                serviceToast("Service Init");
                initWPService();
                break;
            case START:
                if (!IS_SERVICE_RUNNING) {
                    serviceToast("Service ON");
                } else {
                    serviceToast("Service Restart");
                    stopWPService();
                }
                startWPService();

                break;
            case STOP:
                serviceToast("Service Stop");
                stopWPService();
                break;
            default:
                serviceToast("Invalid Service Command");
                break;
        }

        logger.info("onStartCommand End");
        /**
         START_STICKY : 재생성과 onStartCommand() 호출(with null intent)
         START_NOT_STICKY : 서비스 재 실행하지 않음
         START_REDELIVER_INTENT : 재생성과 onStartCommand() 호출(with same intent)
         */
        return START_REDELIVER_INTENT;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @UiThread
    public void serviceToast(String string) {
        Toast.makeText(getApplicationContext(), string, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        logger.info("onCreate Start");
        serviceToast("WP Service Created!!");

        // Init Service
        currentState = State.getInitState();

        logger.info("onCreate End");
    }

    @Override
    public void onDestroy() {
        logger.info("onDestroy");
        serviceToast("WP Service Destroyed!!");
        super.onDestroy();
    }
}
