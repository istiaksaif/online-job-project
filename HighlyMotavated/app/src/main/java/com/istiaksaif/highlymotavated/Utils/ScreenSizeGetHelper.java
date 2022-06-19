package com.istiaksaif.highlymotavated.Utils;

import android.app.Activity;
import android.content.Context;
import android.view.Display;
import android.view.WindowManager;

import androidx.fragment.app.Fragment;


public class ScreenSizeGetHelper {
    private static Fragment fragment;
    private static Activity activity;
    private static Context context;

    public ScreenSizeGetHelper(Fragment fragment, Activity activity,Context context) {
        this.fragment = fragment;
        this.activity = activity;
        this.context = context;
    }

//    public void ScreenSize(){
//        if (activity==null) {
//            WindowManager window = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
//            Display display = window.getDefaultDisplay();
////            int displayWidth = display.getWidth();
//            int displayHeight = display.getHeight();
//            activity.startActivityForResult(displayHeight);
//        }else {
//            WindowManager window = (WindowManager) fragment.getActivity().getSystemService(Context.WINDOW_SERVICE);
//            Display display = window.getDefaultDisplay();
////            int displayWidth = display.getWidth();
//            int displayHeight = display.getHeight();
//        }
//    }
    public static int ScreenHeightSize() {
        if (activity!=null) {
            WindowManager window = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
            Display display = window.getDefaultDisplay();
            int displayHeight = display.getHeight();
            return displayHeight;
        }else if(fragment!=null){
            WindowManager window = (WindowManager) fragment.getActivity().getSystemService(Context.WINDOW_SERVICE);
            Display display = window.getDefaultDisplay();
            int displayHeight = display.getHeight();
            return displayHeight;
        }else {
            WindowManager window = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
            Display display = window.getDefaultDisplay();
            int displayHeight = display.getHeight();
            return displayHeight;
        }
    }
    public static int ScreenWidthSize() {
        if (activity==null) {
            WindowManager window = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
            Display display = window.getDefaultDisplay();
            int displayWidth = display.getWidth();
            return displayWidth;
        }else if(fragment==null){
            WindowManager window = (WindowManager) fragment.getActivity().getSystemService(Context.WINDOW_SERVICE);
            Display display = window.getDefaultDisplay();
            int displayWidth = display.getWidth();
            return displayWidth;
        }else {
            WindowManager window = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
            Display display = window.getDefaultDisplay();
            int displayWidth = display.getWidth();
            return displayWidth;
        }
    }
}
