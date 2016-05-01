package com.suman.news_reader.older_news;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.suman.news_reader.R;
import com.suman.news_reader.media_controllers.NRMusicPlayerActivity;

import java.io.File;

/**
 * Created by sumansucharitdas on 4/17/16.
 */
public class NROlderNewsList  extends AppCompatActivity{


    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;
    private ListView thumbnailList;
    CoordinatorLayout rootLayout;
    NavigationView navView;
    NewsAdapter newsAdapter;
    TextView emptyTextView;

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

        rootLayout = (CoordinatorLayout) findViewById(R.id.rootLayoutOlderNews);
        navView = (NavigationView) findViewById(R.id.navigationOlderNews);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                drawerLayout.closeDrawers();
                return true;
            }
        });

        thumbnailList = (ListView) findViewById(R.id.list_view);
        emptyTextView = (TextView) findViewById(R.id.emptyText);
        newsAdapter = new NewsAdapter(thumbnailList.getContext(), OlderNewsFileNamesPOJO.fileNames);
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
        File dir  = new File(Environment.getExternalStorageDirectory() + "/NewsReader/");
        for (File file : dir.listFiles()) {
            //file.delete();
        }
    }
}
