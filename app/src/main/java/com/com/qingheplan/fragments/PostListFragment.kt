package com.com.qingheplan.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.com.qingheplan.R
import com.com.qingheplan.adapters.PostsAdapter
import com.com.qingheplan.models.Post
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 帖子列表 Fragment - 用于 ViewPager2 的每个标签页
 * 对应 SwiftUI 中的 PostListView
 */
class PostListFragment : Fragment() {

    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var rvPosts: RecyclerView
    private lateinit var layoutLoading: View
    private lateinit var layoutEmpty: View
    
    private lateinit var postsAdapter: PostsAdapter
    private val posts = mutableListOf<Post>()
    
    private var tabType: String = "recommended"
    private var isLoading = false
    private var hasMore = true
    private var currentPage = 1
    
    private val scope = CoroutineScope(Dispatchers.Main + Job())

    companion object {
        private const val ARG_TAB_TYPE = "tab_type"

        fun newInstance(tabType: String): PostListFragment {
            val fragment = PostListFragment()
            val args = Bundle()
            args.putString(ARG_TAB_TYPE, tabType)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            tabType = it.getString(ARG_TAB_TYPE, "recommended")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_post_list, container, false)
        initViews(view)
        setupRecyclerView()
        setupSwipeRefresh()
        loadInitialData()
        return view
    }

    private fun initViews(view: View) {
        swipeRefresh = view.findViewById(R.id.swipeRefresh)
        rvPosts = view.findViewById(R.id.rvPosts)
        layoutLoading = view.findViewById(R.id.layoutLoading)
        layoutEmpty = view.findViewById(R.id.layoutEmpty)
    }

    private fun setupRecyclerView() {
        postsAdapter = PostsAdapter(posts) { post, action ->
            handlePostAction(post, action)
        }
        
        rvPosts.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = postsAdapter
            
            // 添加滚动监听，实现无限加载
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val totalItemCount = layoutManager.itemCount
                    val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                    
                    // 当滚动到倒数第3个item时加载更多
                    if (!isLoading && hasMore && lastVisibleItem >= totalItemCount - 3) {
                        loadMoreData()
                    }
                }
            })
        }
    }

    private fun setupSwipeRefresh() {
        swipeRefresh.setColorSchemeResources(R.color.green_primary)
        swipeRefresh.setOnRefreshListener {
            refreshData()
        }
    }

    private fun loadInitialData() {
        showLoading()
        scope.launch {
            delay(1000) // 模拟网络请求
            val mockPosts = generateMockPosts(20)
            withContext(Dispatchers.Main) {
                posts.clear()
                posts.addAll(mockPosts)
                postsAdapter.notifyDataSetChanged()
                hideLoading()
                updateEmptyState()
            }
        }
    }

    private fun refreshData() {
        currentPage = 1
        hasMore = true
        scope.launch {
            delay(800) // 模拟网络请求
            val mockPosts = generateMockPosts(20)
            withContext(Dispatchers.Main) {
                posts.clear()
                posts.addAll(mockPosts)
                postsAdapter.notifyDataSetChanged()
                swipeRefresh.isRefreshing = false
                updateEmptyState()
            }
        }
    }

    private fun loadMoreData() {
        if (isLoading || !hasMore) return
        
        isLoading = true
        currentPage++
        
        scope.launch {
            delay(800) // 模拟网络请求
            val mockPosts = generateMockPosts(20)
            withContext(Dispatchers.Main) {
                val startPosition = posts.size
                posts.addAll(mockPosts)
                postsAdapter.notifyItemRangeInserted(startPosition, mockPosts.size)
                isLoading = false
                
                // 模拟：加载3页后没有更多数据
                if (currentPage >= 3) {
                    hasMore = false
                }
            }
        }
    }

    private fun handlePostAction(post: Post, action: String) {
        when (action) {
            "like" -> {
                post.isLiked = !post.isLiked
                post.likeCount += if (post.isLiked) 1 else -1
                postsAdapter.notifyItemChanged(posts.indexOf(post))
            }
            "bookmark" -> {
                post.isBookmarked = !post.isBookmarked
                postsAdapter.notifyItemChanged(posts.indexOf(post))
            }
            "comment" -> {
                // TODO: 打开评论页面
            }
            "share" -> {
                // TODO: 分享功能
            }
            "user" -> {
                // TODO: 打开用户主页
            }
            "post" -> {
                // TODO: 打开帖子详情
            }
        }
    }

    private fun showLoading() {
        layoutLoading.visibility = View.VISIBLE
        rvPosts.visibility = View.GONE
        layoutEmpty.visibility = View.GONE
    }

    private fun hideLoading() {
        layoutLoading.visibility = View.GONE
        rvPosts.visibility = View.VISIBLE
    }

    private fun updateEmptyState() {
        if (posts.isEmpty()) {
            layoutEmpty.visibility = View.VISIBLE
            rvPosts.visibility = View.GONE
        } else {
            layoutEmpty.visibility = View.GONE
            rvPosts.visibility = View.VISIBLE
        }
    }

    private fun generateMockPosts(count: Int): List<Post> {
        val mockPosts = mutableListOf<Post>()
        val titles = listOf(
            "😊 一起走在自律的路上｜不完美也没关系",
            "🌱 每天进步一点点，成为更好的自己",
            "📚 读书笔记分享｜最近在读的好书",
            "🏃 坚持运动第100天｜记录我的改变",
            "🎨 生活需要仪式感｜分享我的日常",
            "💪 自律给我自由｜我的成长故事",
            "🌟 今天也要元气满满哦",
            "📝 学习打卡｜持续精进中"
        )

        val contents = listOf(
            "自律这件事，从来不是要赢过别人，\n而是和一群有方向的人，一起走在变好的路上。",
            "有时候我们也会偷懒、也会失去动力，\n但正因为大家都在努力克服相似的难关，\n这份「共同成长」的感觉，才格外珍贵。",
            "哪怕只是小小的一步，也值得被看见。\n因为每一次坚持，都是在告诉未来的自己：\n我还在路上，而且我并不孤单。",
            "🌿 让我们在这里互相打气、分享能量，\n做彼此的同行者，而...",
            "今天天气真好，适合出去走走。\n分享一下最近的学习心得。",
            "青禾计划真是一个很棒的平台！\n大家有什么好的建议吗？",
            "记录一下今天的美好时光。\n感谢大家的支持和鼓励！",
            "一起加油，共同进步！\n每天都要开心哦~"
        )

        for (i in 1..count) {
            mockPosts.add(
                Post(
                    id = "${System.currentTimeMillis()}_$i",
                    userId = "user_$i",
                    username = if (i % 5 == 0) "青禾计划" else "用户${(1..100).random()}",
                    avatar = "",
                    title = titles.random(),
                    content = contents.random(),
                    likeCount = (0..999).random(),
                    commentCount = (0..99).random(),
                    bookmarkCount = (0..99).random(),
                    viewCount = (10..9999).random(),
                    time = "2025-10-12 12:23",
                    isLiked = false,
                    isBookmarked = false
                )
            )
        }
        return mockPosts
    }

    override fun onDestroyView() {
        super.onDestroyView()
        scope.coroutineContext[Job]?.cancel()
    }
}

