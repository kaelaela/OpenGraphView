package me.kaelaela.sample

import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import me.kaelaela.opengraphview.OnLoadListener
import me.kaelaela.opengraphview.OpenGraphView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val openGraphView = findViewById(R.id.og_view) as OpenGraphView
        //You can change the original parser by following code.
        //        openGraphView.setCustomParser(new XMLPullSampleParser());
        //        openGraphView.setCustomParser(new JSoupSampleParser());
        //        openGraphView.setCustomParser(new JSpoonSampleParser());
        openGraphView.setOnLoadListener(object : OnLoadListener() {
            override fun onLoadError(e: Throwable) {
                Log.d("TAG", e.message)
                openGraphView.visibility = View.GONE
            }
        })
        openGraphView.loadFrom("http://ogp.me/")
        openGraphView.setOnClickListener { v ->
            val intent = CustomTabsIntent.Builder().setShowTitle(true).build()
            intent.launchUrl(this@MainActivity, Uri.parse((v as OpenGraphView).url))
        }

        findViewById(R.id.open_list_button).setOnClickListener { v -> LinkListActivity.launch(v.context) }
        findViewById(R.id.button1).setOnClickListener {
            openGraphView.clear()
            openGraphView.loadFrom("http://ogp.me/")
        }
        findViewById(R.id.button2).setOnClickListener {
            openGraphView.clear()
            openGraphView.loadFrom("https://about.me/kaelaela")
        }
        findViewById(R.id.button3).setOnClickListener {
            openGraphView.clear()
            openGraphView.loadFrom("https://github.com/kaelaela")
        }
        findViewById(R.id.button4).setOnClickListener {
            openGraphView.clear()
            openGraphView.loadFrom("http://blog.kaelae.la/")
        }

        val loadButton = findViewById(R.id.load_button) as Button
        val editText = findViewById(R.id.url_form) as EditText
        editText.setSelection(7)
        loadButton.setOnClickListener {
            openGraphView.clear()
            openGraphView.loadFrom(editText.text.toString())
            editText.setText("http://")
            editText.setSelection(7)
        }

        findViewById(R.id.left_button).setOnClickListener { openGraphView.setImagePosition(OpenGraphView.IMAGE_POSITION.LEFT) }
        findViewById(R.id.right_button).setOnClickListener { openGraphView.setImagePosition(OpenGraphView.IMAGE_POSITION.RIGHT) }

        val density = resources.displayMetrics.density
        (findViewById(R.id.radius_seek_bar) as SeekBar).setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                openGraphView.setCornerRadius(progress * density)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

            }
        })
    }
}
