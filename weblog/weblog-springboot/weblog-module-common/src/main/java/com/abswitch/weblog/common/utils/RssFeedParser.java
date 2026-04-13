package com.abswitch.weblog.common.utils;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import java.io.IOException;
import java.net.URL;
/**
 * @Author：abSwitch
 * @url：
 * @date：2026-04-07 18:57
 * @Description：
 */


public class RssFeedParser {
    public static void main(String[] args) {
        try {
            URL feedUrl = new URL("https://tw93.fun/feed.xml");
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(feedUrl));
            System.out.println("Feed Title: " + feed.getTitle());
            System.out.println("Feed Link: " + feed.getLink());
            for (SyndEntry entry : feed.getEntries()) {
                System.out.println("Entry Title: " + entry.getTitle());
                System.out.println("Entry Link: " + entry.getLink());
            }
        } catch (FeedException | IOException e) {
            e.printStackTrace();
        }
    }
}
