package me.kaelaela.sample.custom.jspoon

import pl.droidsonroids.jspoon.annotation.Selector

class JSpoonOGData {
    @Selector("head > title")
    var title: String? = null
    @Selector(value = "meta[name=\"description\"]", attr = "content")
    var desc: String? = null
    @Selector(value = "meta[property='og:image']", attr = "content")
    var image: String? = null
    @Selector(value = "meta[property='og:url']", attr = "content")
    var url: String? = null
}
