package com.afrozaar.rssfeedreader;

import android.text.format.Time;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jay on 1/29/15.
 */
public class RSSFeedParser {

    //PLEASE NOTE: This is a very generic parser class. It must be customised for the type of RSS Feed you are trying to read. Get an example of the RSS feed and check the tags you want read.

    private static final int TAG_ID = 1;
    private static final int TAG_TITLE = 2;
    private static final int TAG_DESCRIPTION = 3;
    private static final int TAG_LINK = 4;
    private static final int TAG_CATEGORY = 5;
    private static final int TAG_PUBDATE = 6;

    private static String TAG = "com.afrozaar.rssfeedreader";

    private static final String ns = null;


    //Parse an Atom feed, returning a collection of Entry objects.
    public List<Entry> parse (InputStream in) throws XmlPullParserException, IOException, ParseException {
        XmlPullParser parser = Xml.newPullParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        parser.setInput(in, null);
        parser.nextTag();
        return readFeed(parser);

    }

    //Decode a feed attached to an XmlPullParser.
    private List<Entry> readFeed(XmlPullParser parser) throws IOException, XmlPullParserException {
        List<Entry> entries = new ArrayList<Entry>();

        parser.require(XmlPullParser.START_TAG, ns, "rss"); //search rss tags because these enclose the rss feed document
        parser.nextTag(); //I added an extra parser.nextTag() here for my own personal use but in most cases you wouldn't need this. It is only used if your feed is encased in two tags before it gets to the main content
        while (parser.next() != XmlPullParser.END_TAG) {
            if(parser.getEventType() != XmlPullParser.START_TAG){
                continue;
            }
            String name = parser.getName();
            Log.d(TAG, "Name found: "+name);
            if(name.equals("item")){
                entries.add(readEntry(parser));
            }else{
                skip(parser);
            }
        }
        return entries;
    }

    /**
     * Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them
     * off to their respective "read" methods for processing. Otherwise, skips the tag.
     */
    private Entry readEntry(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "item");
        String id = null;
        String title = null;
        String descrip = null;
        String category = null;
        String link = null;
        String pubDate = null;

        while (parser.next() != XmlPullParser.END_TAG){
            if(parser.getEventType() != XmlPullParser.START_TAG){
                continue;
            }
            Log.d(TAG, "readEntry entered");
            String name = parser.getName();
            if(name.equals("title")){
                title = readTag(parser, TAG_TITLE);
            }else if(name.equals("description")){
                descrip = readTag(parser,TAG_DESCRIPTION);
            }else if(name.equals("category")){
                category = readTag(parser,TAG_CATEGORY);
            }else if(name.equals("link")){
                String tempLink = readTag(parser,TAG_LINK);
                if(tempLink != null){
                    link = tempLink;
                }
            }else if(name.equals("pubDate")){
                /*Time t = new Time();
                t.parse3339(readTag(parser, TAG_PUBDATE));
                pubDate = t.toMillis(false);
*/
                pubDate = readTag(parser,TAG_PUBDATE);
            }else{
                skip(parser);
            }
        }
        return new Entry(title,descrip,link,category,pubDate);
    }


    //skip is used to skip tags that the user does not care about. You feed the start tag then it goes until it finds the end tag then returns to looking for what you want.
    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if(parser.getEventType() != XmlPullParser.START_TAG){
            throw new IllegalStateException();
        }
        Log.d(TAG, "skip entered");
        int depth = 1;
        while(depth !=0){
            switch (parser.next()){
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

    //Processes incoming tag and reads the selected value from it.
    private String readTag(XmlPullParser parser, int tagType) throws IOException, XmlPullParserException {
        Log.d(TAG, "readTag entered");
        String tag = null;
        String endTag = null;

        switch(tagType){
            case TAG_TITLE:
                return readBasicTag(parser, "title");
            case TAG_DESCRIPTION:
                return readBasicTag(parser,"description");
            case TAG_CATEGORY:
                return readBasicTag(parser,"category");
            case TAG_PUBDATE:
                return readBasicTag(parser,"pubDate");
            case TAG_LINK:
                return readBasicTag(parser, "link");
            default:
                throw new IllegalArgumentException("Unknown Tag Type: "+ tagType);
        }
    }




    //Reads the body of a basic XML tag, which is guaranteed not to contain any nested elements.
    private String readBasicTag(XmlPullParser parser, String tag) throws IOException, XmlPullParserException {
        Log.d(TAG, "readBasicTag entered");
        parser.require(XmlPullParser.START_TAG,ns,tag);
        String result = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, tag);
        return result;
    }

    //This extracts the text values of the tags
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = null;
        Log.d(TAG, "readText entered");
        if(parser.next() == XmlPullParser.TEXT){
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }


    //Entry class of the data we are retrieving from the XML RSS feed
    public static class Entry {
        //public final String id;
        public final String title;
        public final String descrip;
        public final String category;
        public final String link;
        public final String published;

        Entry(String title, String descrip, String link, String category, String published) {
            //this.id = id;
            this.title = title;
            this.descrip = descrip;
            this.category = category;
            this.link = link;
            this.published = published;
        }

        public String toString(){
            String temp = "Title : " + title + "\nDescription : "+descrip+"\nCategory : "+category+"\nLink : "+link+"\nPublished : "+published;
            return temp;
        }
    }

}
