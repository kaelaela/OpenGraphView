package me.kaelaela.sample;

import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import me.kaelaela.opengraphview.OpenGraphView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final OpenGraphView openGraphView = (OpenGraphView) findViewById(R.id.og_view);
        if (openGraphView != null) {
            openGraphView.loadFrom("http://ogp.me/");
            openGraphView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final CustomTabsIntent intent = new CustomTabsIntent.Builder().setShowTitle(true).build();
                    intent.launchUrl(MainActivity.this, Uri.parse(((OpenGraphView) v).getUrl()));
                }
            });
        }
        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGraphView.clear();
                openGraphView.loadFrom("http://ogp.me/");
            }
        });
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGraphView.clear();
                openGraphView.loadFrom("https://github.com/trending");
            }
        });
        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGraphView.clear();
                openGraphView.loadFrom("https://github.com/kaelaela");
            }
        });
        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGraphView.clear();
                openGraphView.loadFrom("http://yuichi31.hatenablog.com/");
            }
        });

        Button loadButton = (Button) findViewById(R.id.load_button);
        if (loadButton != null) {
            loadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText editText = (EditText) findViewById(R.id.url_form);
                    if (openGraphView == null || editText == null) {
                        return;
                    }
                    openGraphView.clear();
                    openGraphView.loadFrom(String.valueOf(editText.getText()));
                }
            });
        }
    }
}
