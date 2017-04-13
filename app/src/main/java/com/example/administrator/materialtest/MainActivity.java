package com.example.administrator.materialtest;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefresh;

    private DrawerLayout mDrawerLayout;

    private Fruit[] fruits = {new Fruit("Apple",R.drawable.apple)
            ,new Fruit("Banana",R.drawable.banana)
            ,new Fruit("Orange",R.drawable.orange)
            ,new Fruit("Watermelon",R.drawable.watermelon)
            ,new Fruit("Pear",R.drawable.pear)
            ,new Fruit("pineapple",R.drawable.pineapple)
            ,new Fruit("Strawberry",R.drawable.strawberry)
            ,new Fruit("Cheer",R.drawable.cheer)
    };
    private List<Fruit> fruitList = new ArrayList<>();
    private FruitAdapter adapter;


    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.backup:
                Toast.makeText(this," you clicked backup ",Toast.LENGTH_LONG).show();
                break;
            case R.id.delete:
                Toast.makeText(this, " you clicked delete", Toast.LENGTH_LONG).show();
                break;
            case R.id.setting:
                Toast.makeText(this, " you clicked setting", Toast.LENGTH_LONG).show();
                break;

            case R.id.home:
                //调用DrawerLayout的openDrawer方法将滑动菜单打印出来，为了保证这里的行为跟xml的一致，传入GravityCompat.START
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            default:

        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //获取swipeRefreshlayout的实例，调用设置颜色，监听器，触发操作是回调onRefresh，基本上onRefresh是与网络进行交互的，而这里用refreshFruit进行
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeColors(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshFruits();
            }

            //先开启一个线程，沉睡2秒后，使用runonuithread方法将线程切换回主线程，调用initfruit重新生成数据
            private void refreshFruits() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                initFruits();
                                //调用FruitAdaper的notifyDatasetchanged方法通知数据发生变化，
                                //调用swiperefreshlayout的setRefreshing方法传入false
                                adapter.notifyDataSetChanged();
                                swipeRefresh.setRefreshing(false);
                            }
                        });
                    }
                }).start();
            }
        });

        initFruits();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycle_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new FruitAdapter(fruitList);
        recyclerView.setAdapter(adapter);


        //只是在下角实现一个悬浮按钮的效果,允许在提示中加入一个可交互按钮，当用户点击按钮的时候可以执行一些额外的逻辑操作，比toast多一步动作，可以进行交互
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view,"Data deleted",Snackbar.LENGTH_SHORT)
                        .setAction("Undo", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Toast.makeText(MainActivity.this,"Data restored",Toast.LENGTH_LONG).show();
                            }
                        })
                        .show();
            }
        });


        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //为了让导航按钮显示出来
            actionBar.setDisplayHomeAsUpEnabled(true);
            //来设置一个导航按钮图标
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }


        //获取实例，调用setcheckItem方法将call菜单项设置为默认选中，调用setNavigationItemselectedlistener方法设置一个菜单项选中事件的监听器
        //点击任意菜单项，都会回调会onNavigarionItemselected方法，
        navView.setCheckedItem(R.id.nav_call);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {

                //将滑动菜单关闭
                mDrawerLayout.closeDrawers();
                return true;
            }
        });

    }

    private void initFruits() {
        fruitList.clear();
        for (int i= 0 ;i<50; i++) {
            Random random = new Random();
            int index = random.nextInt(fruits.length);
            fruitList.add(fruits[index]);
        }
    }


}
