/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.controller.tutorial;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import de.sudoq.R;

public class TutorialActivity extends AppCompatActivity
{
	//private DrawerLayout mDrawerLayout;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		//this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tutorial2);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		
		final ActionBar ab = getSupportActionBar();
		ab.setHomeAsUpIndicator(R.drawable.launcher);
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setDisplayShowTitleEnabled(true);
		
		//-----------------------------
		//fix for snackbar bug but cant get it casted...
        /*CoordinatorLayout cl = (CoordinatorLayout) findViewById(R.id.main_content);
        CoordinatorLayout.LayoutParams clp = (CoordinatorLayout.LayoutParams) cl.getLayoutParams();
        clp.setBehavior(new AppBarLayoutBehavior());*/
		
		//(cl.getLayoutParams()).setBehavior(new AppBarLayoutBehavior());
		//((CoordinatorLayout.LayoutParams) findViewById(R.id.main_content).getLayoutParams()).setBehavior(new AppBarLayoutBehavior());
		
		CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_content);
		coordinatorLayout.setOnTouchListener(
				new View.OnTouchListener()
				{
					@Override
					public boolean onTouch(View v, MotionEvent event)
					{
						return true;
					}
				}
		);
		
		//-----------------------------
		//toolbar

        /*mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }*/
		
		ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
		if(viewPager != null)
		{
			setupViewPager(viewPager);
		}

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
		
		TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
		tabLayout.setupWithViewPager(viewPager);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		return false;
	}
	
	private void setupViewPager(ViewPager viewPager)
	{
		Adapter adapter = new Adapter(getSupportFragmentManager());
		adapter.addFragment(new FragmentSudoku(), getString(R.string.sf_tutorial_sudoku_title));
		adapter.addFragment(new FragmentAssistances(), getString(R.string.sf_tutorial_assistances_title));
		adapter.addFragment(new FragmentActionTree(), getString(R.string.sf_tutorial_action_title));
		viewPager.setAdapter(adapter);
	}

    /*private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
    }*/
	
	static class Adapter extends FragmentPagerAdapter
	{
		private final List<Fragment> mFragments = new ArrayList<>();
		private final List<String> mFragmentTitles = new ArrayList<>();
		
		public Adapter(FragmentManager fm)
		{
			super(fm);
		}
		
		public void addFragment(Fragment fragment, String title)
		{
			mFragments.add(fragment);
			mFragmentTitles.add(title);
		}
		
		@NonNull
		@Override
		public Fragment getItem(int position)
		{
			return mFragments.get(position);
		}
		
		@Override
		public int getCount()
		{
			return mFragments.size();
		}
		
		@Override
		public CharSequence getPageTitle(int position)
		{
			return mFragmentTitles.get(position);
		}
	}
}
