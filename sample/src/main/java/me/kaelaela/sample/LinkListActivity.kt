package me.kaelaela.sample

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

class LinkListActivity : AppCompatActivity() {

    private val mAdapter: LinkListAdapter = LinkListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_link_list)
        initRecyclerView()
    }

    private fun initRecyclerView() {
        val recyclerView = findViewById(R.id.link_list) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = mAdapter
    }

    companion object {
        fun launch(context: Context) {
            val intent = Intent(context, LinkListActivity::class.java)
            context.startActivity(intent)
        }
    }
}
