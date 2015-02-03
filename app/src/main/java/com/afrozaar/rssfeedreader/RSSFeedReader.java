package com.afrozaar.rssfeedreader;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by jay on 1/29/15.
 */
public class RSSFeedReader {
    private static HelperHandler mHandler;
    private CallbackListener mCallbackListener;

    public RSSFeedReader(String url){
        mHandler = new HelperHandler();
        GetRssFeedTask tsk = new GetRssFeedTask();
        tsk.execute(url);
    }

    private class HelperHandler extends Handler{ //Simply used for learning, can be avoided.
        @Override
        public void handleMessage(Message inputMessage){
            String temp = (String) inputMessage.obj;
            mCallbackListener.onFeedReady(temp);
        }
    }

    public interface CallbackListener{
        void onFeedReady(String temp);
    }

    public void setCallBackListener(CallbackListener callBackListener){
        mCallbackListener = callBackListener;
    }

    private String getRssFeed(String url){
        InputStream in = null;
        String rssFeed = "Could not download RSS Feed from "+ url;
        try {
            URL mUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection)mUrl.openConnection();
            in = conn.getInputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            for (int count; (count = in.read(buffer)) != -1; ) {
                out.write(buffer, 0, count);
            }
            byte[] response = out.toByteArray();
            rssFeed = new String(response,"UTF-8");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rssFeed;
    }

    private class GetRssFeedTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String result = "";
            result = getRssFeed(params[0]);
            return result;
        }

        @Override
        protected void onPostExecute(String rssFeed){
            Message msg = new Message();
            msg.obj = rssFeed;
            msg.setTarget(mHandler);
            msg.sendToTarget();
        }
    }
}
