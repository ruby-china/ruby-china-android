package org.ruby_china.rubychina;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.basecamp.turbolinks.TurbolinksSession;
import com.basecamp.turbolinks.TurbolinksView;

public class TopicFormActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_form);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.topic_form_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setTitle("Topic");

        turbolinksView = (TurbolinksView) findViewById(R.id.topic_form_turbolinks_view);
        location = getIntent().getStringExtra(INTENT_URL);

        TurbolinksSession.getDefault(this)
                .activity(this)
                .adapter(this)
                .view(turbolinksView)
                .visit(location);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.topic_form_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_topic_save:
                topicSave();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void topicSave() {
        TurbolinksSession.getDefault(this).getWebView().evaluateJavascript(
                "$('#main form').submit();",
                null
        );
    }
}
