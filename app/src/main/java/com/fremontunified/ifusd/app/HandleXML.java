package com.fremontunified.ifusd.app;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HandleXML {

    private List<String> titleList = new ArrayList<String>();
    private List<String> linkList = new ArrayList<String>();
    private List<String> descriptionList = new ArrayList<String>();
    private List<String> imageList = new ArrayList<String>();

    private String urlString = null;
    private XmlPullParserFactory xmlFactoryObject;
    public volatile boolean parsingComplete = true;

    public HandleXML(String url) {
        this.urlString = url;
    }

    public List<String> getTitles() {
        return titleList;
    }

    public List<String> getLinks() {
        return linkList;
    }

    public List<String> getDescriptions() {
        return descriptionList;
    }

    public List<String> getImages() {
        return imageList;
    }

    public void parseXMLAndStoreIt(XmlPullParser myParser) {
        int event;
        String text = null;
        try {
            event = myParser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {
                String name = myParser.getName();
                switch (event) {
                    case XmlPullParser.START_TAG:
                        break;
                    case XmlPullParser.TEXT:
                        text = myParser.getText();
                        break;
                    case XmlPullParser.END_TAG:
                        if (name.equals("title")) {
                            text = text.replace("&#39;", "'"); // fixes apostrophes
                            titleList.add(text);
                            descriptionList.add("");
                        } else if (name.equals("link")) {
                            linkList.add(text);
                            imageList.add(getImage(text));
                        } else if (name.equals("description")) {
                            text = text.replace("&#39;", "'");
                            descriptionList.set(descriptionList.size() - 1, text);

                        } else {
                        }
                        break;
                }
                event = myParser.next();
            }

            parsingComplete = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getImage(String url) {
        Document doc;
        try {
            doc = Jsoup.connect(url).get();
            Elements images = doc.select("img[src~=(?i)\\.(JPG)]");
            for (Element image : images) {
                if (image.attr("src").contains(".JPG") && !image.attr("src").contains(" ")) {
                    String string = image.attr("src");
                    int index = string.indexOf("/cms");
                    string = string.substring(index);
                    return "http://www.fremont.k12.ca.us" + string;
                }

            }
            return "";
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public void fetchXML() {
        Thread thread = new Thread(new Runnable() {

            // Creates parser, feeds it source from RSS webpage, parses,
            @Override
            public void run() {
                try {
                    URL url = new URL(urlString);
                    HttpURLConnection conn = (HttpURLConnection) url
                            .openConnection();
                    conn.setReadTimeout(10000 /* milliseconds */);
                    conn.setConnectTimeout(15000 /* milliseconds */);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    // Starts the query
                    conn.connect();
                    InputStream stream = conn.getInputStream();
                    xmlFactoryObject = XmlPullParserFactory.newInstance();
                    XmlPullParser myparser = xmlFactoryObject.newPullParser();
                    myparser.setFeature(
                            XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                    myparser.setInput(stream, null);

                    parseXMLAndStoreIt(myparser);

                    stream.close();
                } catch (Exception e) {
                }
            }
        });
        thread.start();
    }
}
