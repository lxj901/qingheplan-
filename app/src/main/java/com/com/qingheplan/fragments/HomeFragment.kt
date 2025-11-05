package com.com.qingheplan.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.com.qingheplan.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * 首页 Fragment - 对应 SwiftUI 的 MainCommunityView
 * 包含三个标签：关注、推荐、同城
 */
class HomeFragment : Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabFollowing: View
    private lateinit var tabRecommended: View
    private lateinit var tabNearby: View
    private lateinit var tvFollowing: TextView
    private lateinit var tvRecommended: TextView
    private lateinit var tvNearby: TextView
    private lateinit var indicatorFollowing: View
    private lateinit var indicatorRecommended: View
    private lateinit var indicatorNearby: View
    private lateinit var btnMessages: View
    private lateinit var btnSearch: ImageView
    private lateinit var fabPublish: FloatingActionButton
    private lateinit var unreadDot: View

    private var currentTab = 1 // 默认选中推荐（索引1）

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        initViews(view)
        setupViewPager()
        setupTabs()
        setupButtons()
        return view
    }

    private fun initViews(view: View) {
        viewPager = view.findViewById(R.id.viewPager)
        tabFollowing = view.findViewById(R.id.tabFollowing)
        tabRecommended = view.findViewById(R.id.tabRecommended)
        tabNearby = view.findViewById(R.id.tabNearby)
        tvFollowing = view.findViewById(R.id.tvFollowing)
        tvRecommended = view.findViewById(R.id.tvRecommended)
        tvNearby = view.findViewById(R.id.tvNearby)
        indicatorFollowing = view.findViewById(R.id.indicatorFollowing)
        indicatorRecommended = view.findViewById(R.id.indicatorRecommended)
        indicatorNearby = view.findViewById(R.id.indicatorNearby)
        btnMessages = view.findViewById(R.id.btnMessages)
        btnSearch = view.findViewById(R.id.btnSearch)
        fabPublish = view.findViewById(R.id.fabPublish)
        unreadDot = view.findViewById(R.id.unreadDot)
    }

    private fun setupViewPager() {
        // 创建 ViewPager2 适配器
        viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = 3

            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> PostListFragment.newInstance("following")
                    1 -> PostListFragment.newInstance("recommended")
                    2 -> PostListFragment.newInstance("nearby")
                    else -> PostListFragment.newInstance("recommended")
                }
            }
        }

        // 监听页面切换
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateTabSelection(position)
            }
        })

        // 设置默认页面为推荐
        viewPager.setCurrentItem(1, false)
    }

    private fun setupTabs() {
        // 关注标签点击
        tabFollowing.setOnClickListener {
            viewPager.currentItem = 0
        }

        // 推荐标签点击
        tabRecommended.setOnClickListener {
            viewPager.currentItem = 1
        }

        // 同城标签点击
        tabNearby.setOnClickListener {
            viewPager.currentItem = 2
        }
    }

    private fun setupButtons() {
        // 消息按钮
        btnMessages.setOnClickListener {
            // TODO: 打开消息页面
        }

        // 搜索按钮
        btnSearch.setOnClickListener {
            // TODO: 打开搜索页面
        }

        // 发布按钮
        fabPublish.setOnClickListener {
            // TODO: 打开发布页面
        }
    }

    private fun updateTabSelection(position: Int) {
        currentTab = position

        val greenColor = ContextCompat.getColor(requireContext(), R.color.green_primary)
        val textPrimary = ContextCompat.getColor(requireContext(), R.color.text_primary)
        val textSecondary = ContextCompat.getColor(requireContext(), R.color.text_secondary)
        val transparent = Color.TRANSPARENT

        when (position) {
            0 -> { // 关注
                tvFollowing.setTextColor(textPrimary)
                tvFollowing.paint.isFakeBoldText = true
                indicatorFollowing.setBackgroundColor(greenColor)

                tvRecommended.setTextColor(textSecondary)
                tvRecommended.paint.isFakeBoldText = false
                indicatorRecommended.setBackgroundColor(transparent)

                tvNearby.setTextColor(textSecondary)
                tvNearby.paint.isFakeBoldText = false
                indicatorNearby.setBackgroundColor(transparent)
            }
            1 -> { // 推荐
                tvFollowing.setTextColor(textSecondary)
                tvFollowing.paint.isFakeBoldText = false
                indicatorFollowing.setBackgroundColor(transparent)

                tvRecommended.setTextColor(textPrimary)
                tvRecommended.paint.isFakeBoldText = true
                indicatorRecommended.setBackgroundColor(greenColor)

                tvNearby.setTextColor(textSecondary)
                tvNearby.paint.isFakeBoldText = false
                indicatorNearby.setBackgroundColor(transparent)
            }
            2 -> { // 同城
                tvFollowing.setTextColor(textSecondary)
                tvFollowing.paint.isFakeBoldText = false
                indicatorFollowing.setBackgroundColor(transparent)

                tvRecommended.setTextColor(textSecondary)
                tvRecommended.paint.isFakeBoldText = false
                indicatorRecommended.setBackgroundColor(transparent)

                tvNearby.setTextColor(textPrimary)
                tvNearby.paint.isFakeBoldText = true
                indicatorNearby.setBackgroundColor(greenColor)
            }
        }

        // 强制重绘
        tvFollowing.invalidate()
        tvRecommended.invalidate()
        tvNearby.invalidate()
    }
}

