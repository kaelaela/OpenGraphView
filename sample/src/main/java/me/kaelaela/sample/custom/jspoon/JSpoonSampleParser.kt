package me.kaelaela.sample.custom.jspoon

import me.kaelaela.opengraphview.Parser
import me.kaelaela.opengraphview.network.model.OGData
import pl.droidsonroids.jspoon.Jspoon
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

class JSpoonSampleParser : Parser {
    private val DECODE_UTF8 = "UTF-8"

    @Throws(IOException::class)
    override fun parse(inputStream: InputStream): OGData {
        val jspoon = Jspoon.create()
        val htmlAdapter = jspoon.adapter<JSpoonOGData>(JSpoonOGData::class.java)
        val inputStreamReader = InputStreamReader(inputStream, DECODE_UTF8)
        val html = readHtml(BufferedReader(inputStreamReader))
        val data = htmlAdapter.fromHtml(html)
        val og = OGData()
        og.setTitle(data.title)
        og.setImage(data.image)
        og.setDescription(data.desc)
        og.setUrl(data.url)
        return og
    }

    @Throws(IOException::class)
    private fun readHtml(br: BufferedReader): String {
        val sb = StringBuilder()
        for (line in br.readLine()) {
            sb.append(line).append("\n")
        }
        return sb.toString()
    }
}
