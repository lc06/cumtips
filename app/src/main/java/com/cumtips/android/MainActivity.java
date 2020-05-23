package com.cumtips.android;

import androidx.annotation.NonNull;

import com.cumtips.android.Fragments.Fragment1;
import com.cumtips.android.Fragments.Fragment2;
import com.cumtips.android.Fragments.Fragment3;
import com.cumtips.android.Fragments.webView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements Fragment1.OnFragmentInteractionListener,Fragment2.OnFragmentInteractionListener,Fragment3.OnFragmentInteractionListener,webView.OnFragmentInteractionListener {

    private DrawerLayout mDrawerLayout;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ArrayList<String> tab_title_list = new ArrayList<>();//存放标签页标题
    private ArrayList<Fragment> fragment_list = new ArrayList<>();//存放ViewPager下的Fragment
    private Fragment fragment1, fragment2, fragment3, fragment4;
    private MyFragmentPagerAdapter pagerAdapter;//适配器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        setTabLayout();
        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        }
        navView.setCheckedItem(R.id.nav_call);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
    }

    private void setTabLayout(){
        tabLayout = (TabLayout) findViewById(R.id.my_tablayout);
        viewPager = (ViewPager) findViewById(R.id.my_viewpager);
        tab_title_list.add("定位");
        tab_title_list.add("停车");
        //tab_title_list.add("寻车");
        tab_title_list.add("更多");
        tabLayout.addTab(tabLayout.newTab().setText(tab_title_list.get(0)).setIcon(R.drawable.ic_my_location_white_24dp));
        tabLayout.addTab(tabLayout.newTab().setText(tab_title_list.get(1)).setIcon(R.drawable.ic_local_parking_white_24dp));
        //tabLayout.addTab(tabLayout.newTab().setText(tab_title_list.get(2)).setIcon(R.drawable.ic_navigation_white_24dp));
        tabLayout.addTab(tabLayout.newTab().setText(tab_title_list.get(2)).setIcon(R.drawable.ic_more_horiz_white_24dp));
        fragment1 = new webView();
        fragment2 = new Fragment2();
        fragment3 = new webView();
        fragment4 = new Fragment3();
        fragment_list.add(fragment1);
        fragment_list.add(fragment2);
        //fragment_list.add(fragment3);
        fragment_list.add(fragment4);
        pagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), tab_title_list, fragment_list);
        viewPager.setAdapter(pagerAdapter);//给ViewPager设置适配器
        tabLayout.setupWithViewPager(viewPager);//将TabLayout与Viewpager联动起来
        tabLayout.setTabsFromPagerAdapter(pagerAdapter);//给TabLayout设置适配器
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar,menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.backup:
                Toast.makeText(this, "You clicked Backup", Toast.LENGTH_SHORT).show();
                break;
            case R.id.delete:
                Toast.makeText(this, "You clicked Delete", Toast.LENGTH_SHORT).show();
                break;
            case R.id.settings:
                Toast.makeText(this, "You clicked Settings", Toast.LENGTH_SHORT).show();
                break;
            default:
        }
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
