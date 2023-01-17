package com.android.jarvis.jacoco;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class JacocoHelper extends BroadcastReceiver {

    private static final String TAG = "JacocoHelper";
    public static final String ACTION = "intent.END_COVERAGE";
    //ec文件的路径
    private static String DEFAULT_COVERAGE_FILE_PATH;

    @Override
    public void onReceive(Context context, Intent intent) {
        DEFAULT_COVERAGE_FILE_PATH = context.getExternalFilesDir(null) + "/coverage.ec";
        // /sdcard/Android/data/<package_name>/files/coverage.ec
        generateEcFile(true);
        Toast.makeText(context, "myReceiver receive", Toast.LENGTH_SHORT)
                .show();
    }

    /**
     * 生成ec文件
     *
     * @param isNew 是否重新创建ec文件
     */
    public static void generateEcFile(boolean isNew) {
        OutputStream out = null;
        File mCoverageFilePath = new File(DEFAULT_COVERAGE_FILE_PATH);
        Log.e(TAG, "DUMP to " + DEFAULT_COVERAGE_FILE_PATH);
        try {
            if (isNew && mCoverageFilePath.exists()) {
                Log.e(TAG, "清除旧的ec文件");
                mCoverageFilePath.delete();
            }
            if (!mCoverageFilePath.exists()) {
                Log.e(TAG, "创建新的ec文件");
                mCoverageFilePath.createNewFile();
            }
            out = new FileOutputStream(mCoverageFilePath.getPath(), true);
            Log.e(TAG, "开始定位class");
            Object agent = Class.forName("org.jacoco.agent.rt.RT")
                    .getMethod("getAgent")
                    .invoke(null);
            Log.e(TAG, "agent已找到");
            if (agent != null) {
                Log.e(TAG, "开始写文件");
                out.write((byte[]) agent.getClass().getMethod("getExecutionData", boolean.class)
                        .invoke(agent, false));
                Log.e(TAG, "结束写文件");
            }
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.e(TAG, "完成。");
    }

}