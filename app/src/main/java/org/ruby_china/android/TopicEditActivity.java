package org.ruby_china.android;

import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.basecamp.turbolinks.TurbolinksSession;
import com.basecamp.turbolinks.TurbolinksView;

public class TopicEditActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_edit);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.topic_edit_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        turbolinksView = (TurbolinksView) findViewById(R.id.topic_edit_turbolinks_view);

        TurbolinksSession.getDefault(this)
                .activity(this)
                .adapter(this)
                .view(turbolinksView)
                .visit(location);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.topic_edit_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_topic_update:
                topicUpdate();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void topicUpdate() {
        TurbolinksSession.getDefault(this).getWebView().evaluateJavascript(
                "$('form[tb=\"edit-topic\"]').submit();",
                null
        );
    }

}
