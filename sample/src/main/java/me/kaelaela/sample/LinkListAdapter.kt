package me.kaelaela.sample

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.kaelaela.opengraphview.OpenGraphView
import me.kaelaela.opengraphview.Parser
import me.kaelaela.sample.custom.XMLPullSampleParser
import java.util.*

class LinkListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val linkList = ArrayList<String>()

    init {
        linkList.add("https://github.com/")
        linkList.add("http://blog.kaelae.la/")
        linkList.add("https://twitter.com/kaelaela31/status/774958512438816769")
        linkList.add("http://ogp.me/")
        linkList.add("https://twitter.com/kaelaela31")
        linkList.add("https://about.me/kaelaela")
        linkList.add("https://twitter.com/kaelaela31/status/815744327951392768")
        linkList.add("https://www.reddit.com/r/androiddev/")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return LinkView(inflater.inflate(R.layout.item_link, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val linkView = holder as LinkView
        val random = Random()
        linkView.bind(linkList[random.nextInt(linkList.size)])
    }

    override fun getItemCount(): Int = 50

    private inner class LinkView(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal var parser: Parser = XMLPullSampleParser()
        internal val ogView: OpenGraphView = itemView.findViewById(R.id.og_view) as OpenGraphView

        init {
            //ogView.setCustomParser(parser);
        }

        fun bind(url: String) {
            ogView.clear()
            ogView.loadFrom(url)
        }
    }
}
