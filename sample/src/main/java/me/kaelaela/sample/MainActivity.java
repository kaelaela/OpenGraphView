package me.kaelaela.sample;

import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
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
                openGraphView.loadFrom("https://about.me/kaelaela");
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
                openGraphView.loadFrom("http://blog.kaelae.la/");
            }
        });

        Button loadButton = (Button) findViewById(R.id.load_button);
        final EditText editText = (EditText) findViewById(R.id.url_form);
        editText.setSelection(7);
        if (loadButton != null) {
            loadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openGraphView.clear();
                    openGraphView.loadFrom(String.valueOf(editText.getText()));
                    editText.setText("http://");
                    editText.setSelection(7);
                }
            });
        }

        findViewById(R.id.left_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGraphView.setImagePosition(OpenGraphView.IMAGE_POSITION.LEFT);
            }
        });
        findViewById(R.id.right_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGraphView.setImagePosition(OpenGraphView.IMAGE_POSITION.RIGHT);
            }
        });

        final float density = getResources().getDisplayMetrics().density;
        ((SeekBar) findViewById(R.id.radius_seek_bar)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                openGraphView.setCornerRadius(progress * density);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        findViewById(R.id.open_list_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinkListActivity.launch(v.getContext());
            }
        });
    }
}
