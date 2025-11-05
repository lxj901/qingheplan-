package com.com.qingheplan

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.com.qingheplan.fragments.*
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * 主页面 - 包含底部导航栏
 */
class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigation: BottomNavigationView

    // 5个 Fragment 实例
    private val homeFragment = HomeFragment()
    private val recordFragment = RecordFragment()
    private val askFragment = AskFragment()
    private val libraryFragment = LibraryFragment()
    private val profileFragment = ProfileFragment()

    // 当前显示的 Fragment
    private var currentFragment: Fragment = homeFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 隐藏 ActionBar
        supportActionBar?.hide()

        setContentView(R.layout.activity_main)

        // 初始化底部导航栏
        bottomNavigation = findViewById(R.id.bottomNavigation)

        // 默认显示首页
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainer, homeFragment)
                .commit()
        }

        // 设置导航栏点击监听
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    switchFragment(homeFragment)
                    true
                }
                R.id.navigation_record -> {
                    switchFragment(recordFragment)
                    true
                }
                R.id.navigation_ask -> {
                    switchFragment(askFragment)
                    true
                }
                R.id.navigation_library -> {
                    switchFragment(libraryFragment)
                    true
                }
                R.id.navigation_profile -> {
                    switchFragment(profileFragment)
                    true
                }
                else -> false
            }
        }
    }

    /**
     * 切换 Fragment
     */
    private fun switchFragment(fragment: Fragment) {
        if (fragment != currentFragment) {
            val transaction = supportFragmentManager.beginTransaction()

            // 隐藏当前 Fragment
            transaction.hide(currentFragment)

            // 如果新 Fragment 还没有添加，则添加；否则显示
            if (!fragment.isAdded) {
                transaction.add(R.id.fragmentContainer, fragment)
            } else {
                transaction.show(fragment)
            }

            transaction.commit()
            currentFragment = fragment
        }
    }
}
