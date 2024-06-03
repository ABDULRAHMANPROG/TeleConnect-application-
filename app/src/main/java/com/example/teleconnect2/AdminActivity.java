package com.example.teleconnect2;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class AdminActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private AdminPagerAdapter adminPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        adminPagerAdapter = new AdminPagerAdapter(this);
        viewPager.setAdapter(adminPagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Logs");
                            tab.setContentDescription("Logs Tab");
                            break;
                        case 1:
                            tab.setText("Assign Tasks");
                            tab.setContentDescription("Assign Tasks Tab");
                            break;
                        case 2:
                            tab.setText("Admin View Tasks");
                            tab.setContentDescription("View Tasks Tab");
                            break;
                        case 3:
                            tab.setText("Download Task Info");
                            tab.setContentDescription("Download Task Info Tab");
                            break;
                    }
                }).attach();
    }
}
