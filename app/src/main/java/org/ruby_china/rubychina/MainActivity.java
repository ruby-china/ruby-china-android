package org.ruby_china.rubychina;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.widget.TextView;

import com.basecamp.turbolinks.TurbolinksSession;
import com.basecamp.turbolinks.TurbolinksView;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationView mNavigationView;
    private SimpleDraweeView mUserAvatarImageView;
    private TextView mUserNameTextView;
    private TextView mUserEmailTextView;

    JSONObject mAppData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        mDrawerLayout.addDrawerListener(mDrawerToggle);

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        View headerView = mNavigationView.getHeaderView(0);
        mUserAvatarImageView = (SimpleDraweeView) headerView.findViewById(R.id.user_avatar);
        mUserNameTextView = (TextView) headerView.findViewById(R.id.user_name);
        mUserEmailTextView = (TextView) headerView.findViewById(R.id.user_email);

        turbolinksView = (TurbolinksView) findViewById(R.id.turbolinks_view);

        TurbolinksSession.getDefault(this).setDebugLoggingEnabled(true);

        WebSettings webSettings = TurbolinksSession.getDefault(this).getWebView().getSettings();
        webSettings.setUserAgentString("turbolinks-app, ruby-china, official, android");

        location = getString(R.string.root_url) + "/topics";

        TurbolinksSession.getDefault(this)
                .activity(this)
                .adapter(this)
                .view(turbolinksView)
                .visit(location);
    }

    public void newTopic(View view) {
        visitProposedToLocationWithAction(getString(R.string.root_url) + "/topics/new", "advance");
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        mDrawerLayout.closeDrawer(GravityCompat.START);
        switch (item.getItemId()) {
            case R.id.nav_settings:
                visitProposedToLocationWithAction(getString(R.string.root_url) + "/account/edit", "advance");
                return true;
            default:
                return true;
        }
    }

    class VisitCompletedCallback implements ValueCallback<String> {
        MainActivity mActivity;

        public VisitCompletedCallback(MainActivity activity) {
            mActivity = activity;
        }

        @Override
        public void onReceiveValue(String value) {
            try {
                JSONObject appData = new JSONObject(value);
                mActivity.setAppData(appData);
                mActivity.updateNavigationView();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void visitCompleted() {
        TurbolinksSession.getDefault(this).getWebView().evaluateJavascript(
                "App;",
                new VisitCompletedCallback(this)
        );

        super.visitCompleted();
    }

    public void setAppData(JSONObject appData) {
        mAppData = appData;
    }

    public void updateNavigationView() {
        if (mAppData.has("current_user_id")) {
            mNavigationView.getMenu().setGroupVisible(R.id.group_guest, false);
            mNavigationView.getMenu().setGroupVisible(R.id.group_user, true);

            try {
                mUserAvatarImageView.setImageURI(mAppData.getString("asset_url") + "/user/avatar/" + mAppData.getInt("current_user_id") + ".jpg!md");
                mUserNameTextView.setText(mAppData.getString("current_user_name"));
                mUserEmailTextView.setText(mAppData.getString("current_user_email"));

            } catch (JSONException e){
                e.printStackTrace();
            }
        } else {
            mNavigationView.getMenu().setGroupVisible(R.id.group_guest, true);
            mNavigationView.getMenu().setGroupVisible(R.id.group_user, false);

            mUserAvatarImageView.setImageResource(R.drawable.ic_account_circle_white_48dp);
            mUserNameTextView.setText("Guest");
            mUserEmailTextView.setText("guest@ruby-china.org");
        }

    }
}
