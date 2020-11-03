package student.app.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.google.android.material.tabs.TabLayout;

import student.app.ui.dashboard.AttendanceFragment;
import student.app.ui.dashboard.CanteenFragment;
import student.app.ui.dashboard.TemamFragment;

public class Pager extends FragmentStatePagerAdapter {
    int tabCount;
    TabLayout tabLayout;
    public Pager(FragmentManager fm,  int tabCount){
        super(fm);
        this.tabCount = tabCount;
    }

    /**
     * Return the Fragment associated with a specified position.
     *
     * @param position
     */
    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment tab;
        switch (position){
            case 0:
                tab = new AttendanceFragment();
                break;
            case 1:
                tab = new CanteenFragment();
                break;
            default:
                tab = new TemamFragment();
        }
        return tab;
    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return tabCount;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return (new String[]{"Attendance","Canteen","Temam"})[position];
    }
}
