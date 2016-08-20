package org.ruby_china.rubychina;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;

import com.basecamp.turbolinks.TurbolinksAdapter;
import com.basecamp.turbolinks.TurbolinksSession;
import com.basecamp.turbolinks.TurbolinksView;

public class BaseActivity extends AppCompatActivity implements TurbolinksAdapter {
    private static final String INTENT_URL = "intentUrl";

    protected String location;
    protected TurbolinksView turbolinksView;

    @Override
    protected void onRestart() {
        super.onRestart();

        TurbolinksSession.getDefault(this)
                .activity(this)
                .adapter(this)
                .restoreWithCachedSnapshot(true)
                .view(turbolinksView)
                .visit(location);
    }

    @Override
    public void onPageFinished() {

    }

    @Override
    public void onReceivedError(int errorCode) {

    }

    @Override
    public void pageInvalidated() {

    }

    @Override
    public void requestFailedWithStatusCode(int statusCode) {

    }

    @Override
    public void visitCompleted() {

    }

    @Override
    public void visitProposedToLocationWithAction(String location, String action) {
        Intent intent;

        if (location.matches("^https://ruby-china\\.org/topics/\\d+")) {
            intent = new Intent(this, TopicActivity.class);
            intent.putExtra(INTENT_URL, location);
        } else if (location.matches("^https://ruby-china\\.org/.+")) {
            intent = new Intent(this, EmptyActivity.class);
            intent.putExtra(INTENT_URL, location);
        } else {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(location));
        }

        this.startActivity(intent);
    }
}