package org.ruby_china.rubychina;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.ValueCallback;

import com.basecamp.turbolinks.TurbolinksSession;
import com.basecamp.turbolinks.TurbolinksView;

public class TopicActivity extends BaseActivity {

    private static final String INTENT_URL = "intentUrl";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.topic_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        turbolinksView = (TurbolinksView) findViewById(R.id.topic_turbolinks_view);
        location = getIntent().getStringExtra(INTENT_URL);

        TurbolinksSession.getDefault(this)
                .activity(this)
                .adapter(this)
                .view(turbolinksView)
                .visit(location);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.topic_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_topic_share:
                shareTopic();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void shareTopic() {
        TurbolinksSession.getDefault(this).getWebView()
                .evaluateJavascript("document.querySelector('h1').innerText;", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                String title = value.substring(1, value.length() - 1);
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, title + " " + location);
                shareIntent.setType("text/plain");
                startActivity(shareIntent);
            }
        });
    }

}
