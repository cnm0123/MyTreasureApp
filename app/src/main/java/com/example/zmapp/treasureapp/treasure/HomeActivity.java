package com.example.zmapp.treasureapp.treasure;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.zmapp.treasureapp.R;
import com.example.zmapp.treasureapp.user.UserPrefs;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.drawerLayout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.navigation)
    NavigationView mNavigationView;
    private ImageView mIvIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ButterKnife.bind(this);

        // toolbar
        setSupportActionBar(mToolbar);
        if (getSupportActionBar()!=null){
            // 不显示默认的标题，而是显示布局中自己加的TextView
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // DrawerLayout的侧滑监听
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                mToolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        toggle.syncState();// 同步状态
        mDrawerLayout.addDrawerListener(toggle);

        // 侧滑菜单item的选择监听
        mNavigationView.setNavigationItemSelectedListener(listener);

        // 找到侧滑的头布局里面的头像
        mIvIcon = (ImageView) mNavigationView.getHeaderView(0).findViewById(R.id.iv_usericon);
        mIvIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HomeActivity.this, "头像", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // 更新头像信息
        String photo = UserPrefs.getInstance().getPhoto();
        if (photo!=null){
            // 加载头像：采用Picasso实现
            Picasso.with(this)
                    .load(photo)
                    .into(mIvIcon);
        }
    }

    private NavigationView.OnNavigationItemSelectedListener listener =
            new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()){
                case R.id.menu_hide://埋藏宝藏的时候
                    Toast.makeText(HomeActivity.this, "埋藏宝藏", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.menu_logout:// 退出的时候
                    Toast.makeText(HomeActivity.this, "退出", Toast.LENGTH_SHORT).show();
                    break;
            }
            mDrawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }
    };
}
