package me.kaelaela.sample.custom.jspoon;

import pl.droidsonroids.jspoon.annotation.Selector;

public class JSpoonOGData {
    @Selector("head > title")
    public String title;
    @Selector(value = "meta[name=\"description\"]", attr = "content")
    public String desc;
    @Selector(value = "meta[property='og:image']", attr = "content")
    public String image;
    @Selector(value = "meta[property='og:url']", attr = "content")
    public String url;
}
