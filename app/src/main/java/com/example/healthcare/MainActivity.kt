package com.example.healthcare

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.example.healthcare.fragments.*
import eu.long1.spacetablayout.SpaceTabLayout


class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val fragmentList: ArrayList<Fragment> = ArrayList()
        fragmentList.add(Analysis())
        fragmentList.add(News())
        fragmentList.add(Location())
        fragmentList.add(Profile())

        val tabLayout: SpaceTabLayout = findViewById(R.id.spaceTabLayout)

        val viewPager: ViewPager = findViewById(R.id.viewPager)

        tabLayout.initialize(viewPager, supportFragmentManager, fragmentList, savedInstanceState)
        tabLayout.setTabFourIcon(R.drawable.ic_person_black_24dp)


    }


}
