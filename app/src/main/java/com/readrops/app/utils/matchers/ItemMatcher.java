package com.readrops.app.utils.matchers;

import com.readrops.app.utils.DateUtils;
import com.readrops.app.utils.Utils;
import com.readrops.db.entities.Feed;
import com.readrops.db.entities.Item;
import com.readrops.api.localfeed.atom.ATOMEntry;
import com.readrops.api.localfeed.json.JSONItem;
import com.readrops.api.localfeed.rss.RSSEnclosure;
import com.readrops.api.localfeed.rss.RSSItem;
import com.readrops.api.localfeed.rss.RSSMediaContent;
import com.readrops.api.utils.ParseException;

import java.util.ArrayList;
import java.util.List;

public final class ItemMatcher {

    public static List<Item> itemsFromRSS(List<RSSItem> items, Feed feed) throws ParseException {
        List<Item> dbItems = new ArrayList<>();

        for (RSSItem item : items) {
            Item newItem = new Item();

            newItem.setAuthor(item.getAuthor());
            newItem.setContent(item.getContent()); // Jsoup.clean(item.getContent(), Whitelist.relaxed())
            newItem.setDescription(item.getDescription());
            newItem.setGuid(item.getGuid() != null ? item.getGuid() : item.getLink());
            newItem.setTitle(Utils.cleanText(item.getTitle()));

            try {
                newItem.setPubDate(DateUtils.stringToLocalDateTime(item.getDate()));
            } catch (Exception e) {
                throw new ParseException();
            }

            newItem.setLink(item.getLink());
            newItem.setFeedId(feed.getId());

            if (item.getMediaContents() != null && !item.getMediaContents().isEmpty()) {
                for (RSSMediaContent mediaContent : item.getMediaContents()) {
                    if (mediaContent.getMedium() != null && Utils.isTypeImage(mediaContent.getMedium())) {
                        newItem.setImageLink(mediaContent.getUrl());
                        break;
                    }
                }
            } else {
                if (item.getEnclosures() != null) {
                    for (RSSEnclosure enclosure : item.getEnclosures()) {
                        if (enclosure.getType() != null && Utils.isTypeImage(enclosure.getType())
                                && enclosure.getUrl() != null) {
                            newItem.setImageLink(enclosure.getUrl());
                            break;
                        }
                    }

                }
            }

            dbItems.add(newItem);
        }

        return dbItems;
    }

    public static List<Item> itemsFromATOM(List<ATOMEntry> items, Feed feed) throws ParseException {
        List<Item> dbItems = new ArrayList<>();

        for (ATOMEntry item : items) {
            Item dbItem = new Item();

            dbItem.setContent(item.getContent()); // Jsoup.clean(item.getContent(), Whitelist.relaxed())
            dbItem.setDescription(item.getSummary());
            dbItem.setGuid(item.getId());
            dbItem.setTitle(Utils.cleanText(item.getTitle()));

            try {
                dbItem.setPubDate(DateUtils.stringToLocalDateTime(item.getUpdated()));
            } catch (Exception e) {
                throw new ParseException();
            }

            dbItem.setLink(item.getUrl());

            dbItem.setFeedId(feed.getId());

            dbItems.add(dbItem);
        }

        return dbItems;
    }

    public static List<Item> itemsFromJSON(List<JSONItem> items, Feed feed) throws ParseException {
        List<Item> dbItems = new ArrayList<>();

        for (JSONItem item : items) {
            Item dbItem = new Item();

            if (item.getAuthor() != null)
                dbItem.setAuthor(item.getAuthor().getName());

            dbItem.setContent(item.getContent()); // Jsoup.clean(item.getContent(), Whitelist.relaxed())
            dbItem.setDescription(item.getSummary());
            dbItem.setGuid(item.getId());
            dbItem.setTitle(Utils.cleanText(item.getTitle()));

            try {
                dbItem.setPubDate(DateUtils.stringToLocalDateTime(item.getPubDate()));
            } catch (Exception e) {
                throw new ParseException();
            }

            dbItem.setLink(item.getUrl());

            dbItem.setFeedId(feed.getId());

            dbItems.add(dbItem);
        }

        return dbItems;
    }
}
