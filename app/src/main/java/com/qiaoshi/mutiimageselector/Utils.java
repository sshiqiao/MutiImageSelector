package com.qiaoshi.mutiimageselector;

import android.content.Context;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import java.io.File;

public class Utils {
    public static String getApplicationFolderPath(){
        return Environment.getExternalStorageDirectory() + File.separator +"/MutilImageSelector";
    }
    public static void checkApplicationFolder(){
        File file = new File(getApplicationFolderPath());
        if (!file.exists()) {
            file.mkdir();
        }
    }
    public static boolean fileExists(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }
    public static int dp2px(int dpValue) {
        final float scale = MyApplication.getGlobalContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5);
    }
    public static int screenWidth(){
        WindowManager wm = (WindowManager) MyApplication.getGlobalContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metric = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metric);
        return metric.widthPixels;
    }
    public static int screenHeight(){
        WindowManager wm = (WindowManager) MyApplication.getGlobalContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metric = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metric);
        return metric.heightPixels;
    }


    /**
     * Animation
     */
    public static class AnimationListener implements Animation.AnimationListener{
        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }
    public static TranslateAnimation translateAnimation(float fromXDelta, float toXDelta, float fromYDelta, float toYDelta, long duration){
        TranslateAnimation translateAnimation = new TranslateAnimation(fromXDelta, toXDelta, fromYDelta, toYDelta);
        translateAnimation.setDuration(duration);
        translateAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        return translateAnimation;
    }
    public static AlphaAnimation alphaAnimation(float fromAlpha, float toAlpha, long duration){
        AlphaAnimation alphaAnimation = new AlphaAnimation(fromAlpha, toAlpha);
        alphaAnimation.setDuration(duration);
        return alphaAnimation;
    }
    public static ScaleAnimation scaleAnimation(float fromX, float toX, float fromY, float toY, long duration){
        ScaleAnimation scaleAnimation = new ScaleAnimation(fromX, toX, fromY, toY, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(duration);
        return scaleAnimation;
    }
}
