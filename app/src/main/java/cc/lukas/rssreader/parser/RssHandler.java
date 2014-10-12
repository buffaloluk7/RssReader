package cc.lukas.rssreader.parser;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Lukas on 28.09.2014.
 */
public class RssHandler extends DefaultHandler {

    private RssFeed rssFeed;
    private RssItem rssItem;
    private StringBuilder stringBuilder;

    @Override
    public void startDocument() {
        rssFeed = new RssFeed();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        stringBuilder = new StringBuilder();

        if (qName.equals("item") && rssFeed != null) {
            rssItem = new RssItem();
            rssItem.setFeed(rssFeed);
            rssFeed.addRssItem(rssItem);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        stringBuilder.append(ch, start, length);
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        // rssFeed needs to be set , qualified name needs to be set
        if (rssFeed == null || qName == null || qName.length() == 0) {
            return;
        }

        if (rssItem == null) {
            // Parse feed properties
            try {
                String methodName = createMethodName(qName);
                Method method = rssFeed.getClass().getMethod(methodName, String.class);
                method.invoke(rssFeed, stringBuilder.toString());
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        } else {
            // Parse item properties
            try {
                if (qName.equals("content:encoded")) {
                    qName = "content";
                }

                String methodName = createMethodName(qName);
                Method method = rssItem.getClass().getMethod(methodName, String.class);
                method.invoke(rssItem, stringBuilder.toString());
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public RssFeed getResult() {
        return rssFeed;
    }

    // Build the method name we want to invoke on either the RSSFeed or RssItem class
    private String createMethodName(String name) {
        return "set" + name.substring(0, 1).toUpperCase() + name.substring(1);
    }

}
