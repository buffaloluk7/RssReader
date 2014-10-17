package cc.lukas.rssreader.parser;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import cc.lukas.rssreader.RssFeed;

public class RssReader {
    public RssFeed read(URL url) {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            XMLReader reader = parser.getXMLReader();
            RssHandler handler = new RssHandler();
            InputSource input = new InputSource(url.openStream());
            reader.setContentHandler(handler);
            reader.parse(input);

            return handler.getResult();
        } catch (ParserConfigurationException e) {
            return null;
        } catch (SAXException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
    }
}