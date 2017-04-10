package kr.co.linsoo.particleinfomation;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    List<Fragment> m_listFragment = null;

    //---------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "새로 고침중... 잠시 기다려 주세요", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                refreshData();
            }
        });
    }

    public void refreshData(){
        if(m_listFragment == null)
            m_listFragment=  getSupportFragmentManager().getFragments();

        int currentFragmentNo = mViewPager.getCurrentItem();
        Log.d("linsoo", "refreshData CurrentFragment="+currentFragmentNo);
        switch (currentFragmentNo){
            case 0:
                ((RealtimePage)m_listFragment.get(currentFragmentNo)).refreshData();
                break;
            default:
                ((RealtimePage)m_listFragment.get(currentFragmentNo)).refreshData();
                break;
        }

    }


    private class SectionsPagerAdapter extends FragmentStatePagerAdapter  {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return RealtimePage.newInstance(position+1);
                case 1:
                    return RealtimePage.newInstance(position + 1);
                case 2:
                    return RealtimePage.newInstance(position + 1);
                default:
                    return RealtimePage.newInstance(position + 1);
            }

        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 1;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }
    }
}


