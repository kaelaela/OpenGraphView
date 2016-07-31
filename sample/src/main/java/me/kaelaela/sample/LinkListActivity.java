package me.kaelaela.sample;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class LinkListActivity extends AppCompatActivity {

    private LinkListAdapter mAdapter;

    public static void launch(Context context) {
        Intent intent = new Intent(context, LinkListActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_list);
        initRecyclerView();
    }

    private void initRecyclerView() {
        mAdapter = new LinkListAdapter();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.link_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);
    }
}
