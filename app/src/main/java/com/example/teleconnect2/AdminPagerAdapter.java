package com.example.teleconnect2;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class AdminPagerAdapter extends FragmentStateAdapter {

    public AdminPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new LogsFragment();
            case 1:
                return new AssignTaskFragment();
            case 2:
                return new ViewAssignedTasksFragment();
            case 3:
                return new DownloadTaskInfoFragment();
            default:
                return new LogsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4; // Number of tabs
    }
}
