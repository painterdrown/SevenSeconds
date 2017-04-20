package com.goldfish.sevenseconds.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2017/4/21.
 */

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
    }

    /**打开的activity**/
    private List<AppCompatActivity> activities = new ArrayList<AppCompatActivity>();
    /**应用实例**/
    private static BaseActivity instance;

    /**
     *  获得实例
     * @return
     */
    public static BaseActivity getInstance(){
        return instance;
    }

    /**
     * 新建了一个activity
     * @param activity
     */
    public void addActivity(AppCompatActivity activity){
        activities.add(activity);
    }

    /**
     *  结束指定的Activity
     * @param activity
     */
    public void finishActivity(AppCompatActivity activity){
        if (activity!=null) {
            this.activities.remove(activity);
            activity.finish();
            activity = null;
        }
    }
    /**
     * 应用退出，结束所有的activity
     */
    public void exit(){
        try {
            if (activities != null) {
                for (AppCompatActivity activity : activities) {
                    if (activity != null)
                        activity.finish();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
        //android.os.Process.killProcess(android.os.Process.myPid());
    }
    /**
     * 关闭Activity列表中的所有Activity*/
    public void finishAllActivity(){
        for (AppCompatActivity activity : activities) {
            if (null != activity) {
                finishActivity(activity);
            }
        }
    }
}
