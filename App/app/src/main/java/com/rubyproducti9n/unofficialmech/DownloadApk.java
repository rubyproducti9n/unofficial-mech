package com.rubyproducti9n.unofficialmech;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.webkit.URLUtil;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadApk {

    public static void startDownloadingApk(Context context, String url, String fileName) {
        if (URLUtil.isValidUrl(url)) {
            new DownloadNewVersion(context, url, fileName).execute();
        }
    }

    public static class DownloadNewVersion extends AsyncTask<String, Integer, Boolean> {
        private final Context context;
        private final String downloadUrl;
        private final String fileName;
        private ProgressDialog bar;


        public DownloadNewVersion(Context context, String downloadUrl, String fileName) {
            this.context = context;
            this.downloadUrl = downloadUrl;
            this.fileName = fileName;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            bar = new ProgressDialog(context);
            bar.setCancelable(false);
            bar.setMessage("Downloading...");
            bar.setIndeterminate(true);
            bar.setCanceledOnTouchOutside(false);
            bar.show();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            int progress = values[0];
            String msg;
            if (progress > 99) {
                msg = "Finishing...";
            } else {
                msg = "Downloading... " + progress + "%";
            }
            bar.setIndeterminate(false);
            bar.setCancelable(false);
            bar.setMax(100);
            bar.setProgress(progress);
            bar.setMessage(msg);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            bar.dismiss();
            if (result != null && result) {
                Toast.makeText(context, "Processing update...", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Error: Try Again", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            boolean flag = false;
            try {
                String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/Updates/";
                File outputFile = new File(path + fileName + ".apk");
                int repetition = 1;
                while (outputFile.exists()) {
                    outputFile = new File(path + fileName + " (" + repetition + ")" + ".apk");
                    repetition++;
                }

                File directory = new File(path);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                URL url = new URL(downloadUrl);
                HttpURLConnection c = (HttpURLConnection) url.openConnection();
                c.setRequestMethod("GET");
                c.connect();

                FileOutputStream fos = new FileOutputStream(outputFile);
                InputStream inputStream = c.getInputStream();
                int totalSize = c.getContentLength();

                byte[] buffer = new byte[1024];
                int len1;
                float per;
                float downloaded = 0f;
                while ((len1 = inputStream.read(buffer)) != -1) {
                    fos.write(buffer, 0, len1);
                    downloaded += len1;
                    per = (downloaded * 100 / totalSize);
                    publishProgress((int) per);
                }
                fos.close();
                inputStream.close();
                openNewVersion(outputFile.getPath());
                flag = true;
            } catch (MalformedURLException e) {
                Log.e("DownloadApk", "Update Error: " + e.getMessage());
                flag = false;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return flag;
        }

        private void openNewVersion(String location) {
            Uri uri = getUriFromFile(location);
//            Uri uri1 = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", new File(location));
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try{
                context.getApplicationContext().startActivity(intent);
            }catch (ActivityNotFoundException e){
                Toast.makeText(context, "Unable to open file, please install the file manually from Downloads/Updates", Toast.LENGTH_SHORT).show();
                Log.e("AC---------", "----" + e);
            }
        }

        private Uri getUriFromFile(String filePath) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                return Uri.fromFile(new File(filePath));
            } else {
                return FileProvider.getUriForFile(context, context.getPackageName() + ".provider", new File(filePath));
            }
        }

    }

}
