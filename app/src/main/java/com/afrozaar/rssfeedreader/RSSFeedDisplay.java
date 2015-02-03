package com.afrozaar.rssfeedreader;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.List;


public class RSSFeedDisplay extends ActionBarActivity implements RSSFeedReader.CallbackListener {

    private TextView mRssDisplay;
    private Button mButton;
    private List<RSSFeedParser.Entry> mList;
    private String mRssString;

    private static String TAG = "com.afrozaar.rssfeedreader";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_rssfeed_reader);

        RSSFeedReader rssFeedReader = new RSSFeedReader("INSERT RSS FEED URL HERE");
        rssFeedReader.setCallBackListener(this);
        mRssDisplay = (TextView)findViewById(R.id.tv_rssdisplay);
        mButton = (Button)findViewById(R.id.btn_parse);
        mButton.setEnabled(false);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"Onclick entered");
                InputStream stream = new ByteArrayInputStream((mRssString).getBytes(Charset.forName("UTF-8")));
                Log.d(TAG,"Stream data : "+ mRssString);
                try {
                    mList = new RSSFeedParser().parse(stream);
                    String temp = "";
                    Log.d(TAG,"mList size : "+ mList.size());
                    if(!mList.isEmpty()){
                        Log.d(TAG,"mList isnt empty");
                        for (int i = 0; i < mList.size() ; i++){
                            Log.d(TAG,"mList entry number: "+i + " DATA: "+ mList.get(i).toString());
                            temp = temp + "\n" + mList.get(i).toString();
                        }
                        mRssDisplay.setText(temp);
                    }

                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_rssfeed_reader, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFeedReady(String temp) {
        mRssDisplay.setText(temp);
        mRssString = temp;
        mButton.setEnabled(true);
    }
}
