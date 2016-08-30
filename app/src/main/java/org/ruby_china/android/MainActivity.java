package org.ruby_china.android;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationView mNavigationView;
    private SimpleDraweeView mUserAvatarImageView;
    private TextView mUserNameTextView;
    private TextView mUserEmailTextView;
    private boolean mSearched = false;

    JSONObject mCurrenetUserMeta;

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

    class SearchExpandListener implements MenuItemCompat.OnActionExpandListener {
        private MainActivity mActivity;

        public SearchExpandListener(MainActivity activity) {
            mActivity = activity;
        }

        @Override
        public boolean onMenuItemActionExpand(MenuItem item) {
            return true;
        }

        @Override
        public boolean onMenuItemActionCollapse(MenuItem item) {
            if (mSearched) {
                mActivity.searchCLose();
            }

            return true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView =
                (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(this);

        MenuItemCompat.setOnActionExpandListener(searchItem, new SearchExpandListener(this));

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        try {
            mSearched = true;
            location = getString(R.string.root_url) + "/search?q=" + URLEncoder.encode(query, "UTF-8");
            TurbolinksSession.getDefault(this).visit(location);
        } catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    private void searchCLose() {
        location = getString(R.string.root_url) + "/topics";
        TurbolinksSession.getDefault(this).visit(location);
        mSearched = false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }
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
            case R.id.nav_sign_up:
                visitProposedToLocationWithAction(getString(R.string.root_url) + "/account/sign_up", "advance");
                return true;
            case R.id.nav_sign_in:
                visitProposedToLocationWithAction(getString(R.string.root_url) + "/account/sign_in", "advance");
                return true;
            case R.id.nav_sign_out:
                signOut();
                return true;
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
                if (value.equals("null")) {
                    mActivity.setAppData(null);
                } else {
                    mActivity.setAppData(new JSONObject(value));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mActivity.updateNavigationView();
        }
    }

    @Override
    public void visitCompleted() {
        TurbolinksSession.getDefault(this).getWebView().evaluateJavascript(
                "$('meta[name=\"current-user\"]').data()",
                new VisitCompletedCallback(this)
        );

        super.visitCompleted();
    }

    public void setAppData(JSONObject userMeta) {
        mCurrenetUserMeta = userMeta;
    }

    public void updateNavigationView() {
        if (mCurrenetUserMeta != null) {
            mNavigationView.getMenu().setGroupVisible(R.id.group_guest, false);
            mNavigationView.getMenu().setGroupVisible(R.id.group_user, true);

            try {
                mUserAvatarImageView.setImageURI(mCurrenetUserMeta.getString("userAvatarUrl"));
                mUserNameTextView.setText(mCurrenetUserMeta.getString("userLogin"));
                mUserEmailTextView.setText(mCurrenetUserMeta.getString("userEmail"));
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

    public void signOut() {
        TurbolinksSession.getDefault(this).getWebView().evaluateJavascript(
                "$.ajax({ url: '/account/sign_out', method: 'DELETE' });",
                null
        );
    }
}
