package me.kaelaela.sample;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import me.kaelaela.opengraphview.OpenGraphView;

public class LinkListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<String> linkList = new ArrayList<>();

    public LinkListAdapter() {
        linkList.add("https://github.com/");
        linkList.add("http://yuichi31.hatenablog.com/");
        linkList.add("http://ncode.syosetu.com/n2267be/");
        linkList.add("http://ogp.me/");
        linkList.add("https://twitter.com/kaelaela31");
        linkList.add("https://design.google.com/");
        linkList.add("http://connpass.com/user/yuichi_maekawa/");
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new LinkView(inflater.inflate(R.layout.item_link, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        LinkView linkView = (LinkView) holder;
        Random random = new Random();
        linkView.bind(linkList.get(random.nextInt(linkList.size())));
    }

    @Override
    public int getItemCount() {
        return 50;
    }

    private class LinkView extends RecyclerView.ViewHolder {

        public LinkView(View itemView) {
            super(itemView);
        }

        public void bind(String url) {
            OpenGraphView ogView = (OpenGraphView) itemView.findViewById(R.id.og_view);
            ogView.clear();
            ogView.loadFrom(url);
        }
    }
}
