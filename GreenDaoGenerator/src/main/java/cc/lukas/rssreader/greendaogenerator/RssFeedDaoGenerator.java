package cc.lukas.rssreader.greendaogenerator;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;
import de.greenrobot.daogenerator.ToMany;

public class RssFeedDaoGenerator {
    private static final int SCHEMA_VERSION = 1;
    private Schema schema = new Schema(SCHEMA_VERSION, "cc.lukas.rssreader");

    private void generateRssFeedSchema(String outputDirectory) {
        Entity rssFeed = schema.addEntity("RssFeed");
        rssFeed.addIdProperty().autoincrement();
        rssFeed.addStringProperty("title").notNull();
        rssFeed.addStringProperty("link").notNull();
        rssFeed.addDateProperty("updatedAt");

        Entity rssItem = this.schema.addEntity("RssItem");
        rssItem.addIdProperty().autoincrement();
        rssItem.addStringProperty("title").notNull();
        rssItem.addStringProperty("link").notNull();
        rssItem.addStringProperty("description");
        Property pubDate = rssItem.addDateProperty("pubDate").notNull().getProperty();
        Property feedId = rssItem.addLongProperty("feedId").notNull().getProperty();

        // Item belongs to ONE feed
        rssItem.addToOne(rssFeed, feedId);

        // Feed has multiple items
        ToMany feedToItems = rssFeed.addToMany(rssItem, feedId);
        feedToItems.setName("items");
        feedToItems.orderAsc(pubDate);

        try {
            new DaoGenerator().generateAll(schema, outputDirectory);
        } catch (Throwable ignored) { }
    }

    public static void main(String args[]) throws Exception {
        RssFeedDaoGenerator generator = new RssFeedDaoGenerator();
        generator.generateRssFeedSchema(args[0]);
    }
}