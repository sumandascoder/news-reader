package com.suman.news_reader.navigation_older_news;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by sumansucharitdas on 5/5/16.
 */
public class TrashFilesTask extends AsyncTask<Void, Integer, Void> {

    private File dir;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        NROlderNewsList.progressDialogFileDelete.setProgress(0);
        NROlderNewsList.progressDialogFileDelete.setIndeterminate(false);
        NROlderNewsList.progressDialogFileDelete.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        NROlderNewsList.progressDialogFileDelete.setCancelable(false);
        NROlderNewsList.progressDialogFileDelete.show();
    }

    @Override
    protected Void doInBackground(Void... params) {

        dir = new File(Environment.getExternalStorageDirectory() + "/NewsReader/");
        int i = 0;
        NROlderNewsList.progressDialogFileDelete.setMax(dir.listFiles().length);
        for (File file : dir.listFiles()) {
            file.delete();
            publishProgress(i++);
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        NROlderNewsList.progressDialogFileDelete.setProgress(values[0]);
    }

    @Override
    protected void onPostExecute(Void result) {
        NROlderNewsList.progressDialogFileDelete.dismiss();
        NROlderNewsList.newsAdapter.refreshFileNames(new ArrayList<String>());
    }
}