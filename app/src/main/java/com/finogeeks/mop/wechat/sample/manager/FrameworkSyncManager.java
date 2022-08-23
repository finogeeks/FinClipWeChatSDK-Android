package com.finogeeks.mop.wechat.sample.manager;


import static com.finogeeks.mop.wechat.sample.util.FileUtilKt.deleteFile;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.util.Log;

import com.finogeeks.lib.applet.client.FinAppTrace;
import com.finogeeks.lib.applet.client.FinStoreConfig;
import com.finogeeks.mop.wechat.sample.util.IOUtilKt;
import com.finogeeks.mop.wechat.sample.util.ZipUtilKt;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * 负责同步基础库的类
 */
public class FrameworkSyncManager {

    private static final String TAG = "FrameworkSyncManager";

    private FrameworkSyncManager() {
    }

    /**
     * 解压本地基础库（src/main/assets/framework4camera.zip）
     *
     * @param context  上下文
     * @param callback 回调接口
     */
    public static void unzipAssetsFramework(Context context, SyncCallback callback) {
        new UnzipAssetsFrameworkTask(context, callback).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * 解压本地基础库（src/main/assets/framework4camera.zip）的Task
     */
    private static class UnzipAssetsFrameworkTask extends AsyncTask<String, Void, Boolean> {

        private String frameworkPath;
        private AssetManager assetManager;
        private SyncCallback callback;

        UnzipAssetsFrameworkTask(Context context, SyncCallback callback) {
            this.frameworkPath = context.getFilesDir().getAbsolutePath() + File.separator +
                    "finapplet/" + FinStoreConfig.Companion.getLOCAL_ASSETS_FIN_STORE_NAME_MD5() +
                    "/js/environment-0.0.0/framework/";
            File frameworkDir = new File(frameworkPath);
            if (!frameworkDir.exists()) {
                //noinspection ResultOfMethodCallIgnored
                frameworkDir.mkdirs();
            }
            this.assetManager = context.getAssets();
            this.callback = callback;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            boolean unzipSuccess = false;
            InputStream in = null;
            try {
                in = assetManager.open("framework.zip");
                unzipSuccess = ZipUtilKt.unzipFile(in, frameworkPath);
            } catch (IOException e) {
                FinAppTrace.e(TAG, e.getMessage());
            } finally {
                IOUtilKt.closeAll(in);
            }

            return unzipSuccess;
        }

        @Override
        protected void onPostExecute(Boolean res) {
            boolean isFrameworkExists = false;
            File frameworkDir = new File(frameworkPath);
            if (frameworkDir.exists()) {
                File[] files = frameworkDir.listFiles();
                isFrameworkExists = files != null && files.length > 0;
            }

            Log.d(TAG, "unzip task is done: " + res + " && " + isFrameworkExists);
            boolean result = res && isFrameworkExists;
            if (callback != null) {
                callback.onResult(result);
            }
        }
    }

    public interface SyncCallback {

        void onResult(boolean result);
    }

    public static void clearFramework(Context context) {
        String frameworkArchivePath = context.getFilesDir().getAbsolutePath() + File.separator + "finapplet/localAssetsApplets/archives/framework/";
        deleteFile(frameworkArchivePath);
        String frameworkJsPath = context.getFilesDir().getAbsolutePath() + File.separator +
                "finapplet/" + FinStoreConfig.Companion.getLOCAL_ASSETS_FIN_STORE_NAME_MD5() +
                "/js/environment-0.0.0/framework/";
        deleteFile(frameworkJsPath);
    }
}