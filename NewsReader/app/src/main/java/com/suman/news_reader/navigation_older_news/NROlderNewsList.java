package com.suman.news_reader.navigation_older_news;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.suman.news_reader.R;
import com.suman.news_reader.activities.CameraActivity;
import com.suman.news_reader.activities.NRMainActivity;
import com.suman.news_reader.media_controllers.NRMusicPlayerActivity;
import com.suman.news_reader.navigation_informational.AboutActivity;

/**
 * @author sumansucharitdas
 */
public class NROlderNewsList  extends AppCompatActivity{

    // UI elements
    private DrawerLayout            drawerLayout;
    private ActionBarDrawerToggle   drawerToggle;
    public static ListView          thumbnailList;
    private CoordinatorLayout       rootLayout;
    private NavigationView          navView;
    private TextView                emptyTextView;

    public static ProgressDialog    progressDialogFileDelete;
    public static NewsAdapter       newsAdapter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_older_news);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayoutOlderNews);
        drawerToggle = new ActionBarDrawerToggle(NROlderNewsList.this, drawerLayout, R.string.app_name, R.string.app_name);
        drawerLayout.setDrawerListener(drawerToggle);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarOlderNews);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setNavigationBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.colorPrimaryDark);
        tintManager.setNavigationBarTintColor(R.color.colorPrimary);

        rootLayout = (CoordinatorLayout) findViewById(R.id.rootLayoutOlderNews);
        navView = (NavigationView) findViewById(R.id.navigationOlderNews);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                if (menuItem.getItemId() == R.id.nav_older_news){
                    // Same screen do nothing
                }
                else if (menuItem.getItemId() == R.id.nav_capture_image) {
                    Intent cameraActivity = new Intent(NROlderNewsList.this, CameraActivity.class);
                    cameraActivity.putExtra("OtherActivity", "NROlderNewsList");
                    startActivity(cameraActivity);
                    finish();
                }
                else if (menuItem.getItemId() == R.id.nav_load_from_gallery) {
                    Intent mainActivity = new Intent(NROlderNewsList.this, NRMainActivity.class);
                    mainActivity.putExtra("selectedNav", "Gallery");
                    setResult(RESULT_OK);
                    startActivity(mainActivity);
                    finish();
                }
                else if (menuItem.getItemId() == R.id.nav_about) {
                    Intent aboutIntent = new Intent(NROlderNewsList.this, AboutActivity.class);
                    aboutIntent.putExtra("about-page", "AboutNewsReader.html");
                    startActivity(aboutIntent);
                }
                else if (menuItem.getItemId() == R.id.nav_contact){
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("message/rfc822");
                    i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"sumandas.freaky@gmail.com"});
                    i.putExtra(Intent.EXTRA_SUBJECT, "Say hello or let us know whatsup?");
                    i.putExtra(Intent.EXTRA_TEXT   , "Please add details");
                    try {
                        startActivity(Intent.createChooser(i, "Contact Us"));
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(NROlderNewsList.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                    }
                }
                drawerLayout.closeDrawers();
                return true;
            }
        });

        progressDialogFileDelete = new ProgressDialog(this);

        thumbnailList = (ListView) findViewById(R.id.list_view);
        emptyTextView = (TextView) findViewById(R.id.emptyText);
        newsAdapter = new NewsAdapter(thumbnailList.getContext(), OlderNewsFileNamesPOJO.fileNames);
        newsAdapter.notifyDataSetChanged();
        thumbnailList.setAdapter(newsAdapter);
        thumbnailList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent startMusic = new Intent(getApplication(), NRMusicPlayerActivity.class);
                startMusic.putExtra("fileID", OlderNewsFileNamesPOJO.fileNames.get(position));
                startActivity(startMusic);
            }
        });
        thumbnailList.setEmptyView(emptyTextView);
    }

    @TargetApi(19) private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.file_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.trash_menu:
                trashMenu();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void trashMenu(){
        TrashFilesTask trashMenu = new TrashFilesTask();
        trashMenu.execute();
    }
}