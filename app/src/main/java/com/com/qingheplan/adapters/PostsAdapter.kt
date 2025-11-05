package com.com.qingheplan.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.com.qingheplan.R
import com.com.qingheplan.models.Post

/**
 * 帖子列表适配器
 */
class PostsAdapter(
    private val posts: List<Post>,
    private val onAction: (Post, String) -> Unit
) : RecyclerView.Adapter<PostsAdapter.PostViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post_card, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(posts[position])
    }

    override fun getItemCount(): Int = posts.size

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val layoutUserInfo: View = itemView.findViewById(R.id.layoutUserInfo)
        private val ivAvatar: ImageView = itemView.findViewById(R.id.ivAvatar)
        private val tvUsername: TextView = itemView.findViewById(R.id.tvUsername)
        private val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        private val btnMore: ImageView = itemView.findViewById(R.id.btnMore)
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val tvContent: TextView = itemView.findViewById(R.id.tvContent)
        private val btnLike: View = itemView.findViewById(R.id.btnLike)
        private val ivLike: ImageView = itemView.findViewById(R.id.ivLike)
        private val tvLikeCount: TextView = itemView.findViewById(R.id.tvLikeCount)
        private val btnComment: View = itemView.findViewById(R.id.btnComment)
        private val tvCommentCount: TextView = itemView.findViewById(R.id.tvCommentCount)
        private val btnBookmark: View = itemView.findViewById(R.id.btnBookmark)
        private val ivBookmark: ImageView = itemView.findViewById(R.id.ivBookmark)
        private val tvBookmarkCount: TextView = itemView.findViewById(R.id.tvBookmarkCount)
        private val tvViewCount: TextView = itemView.findViewById(R.id.tvViewCount)

        fun bind(post: Post) {
            // 设置用户信息
            tvUsername.text = post.username
            tvTime.text = post.time

            // 设置帖子标题和内容
            tvTitle.text = post.title
            tvContent.text = post.content

            // 设置点赞状态
            updateLikeState(post)
            tvLikeCount.text = if (post.likeCount > 0) post.likeCount.toString() else "0"

            // 设置评论数
            tvCommentCount.text = if (post.commentCount > 0) post.commentCount.toString() else "0"

            // 设置收藏状态
            updateBookmarkState(post)
            tvBookmarkCount.text = if (post.bookmarkCount > 0) post.bookmarkCount.toString() else "0"

            // 设置浏览量
            tvViewCount.text = post.viewCount.toString()
            
            // 点击事件
            layoutUserInfo.setOnClickListener {
                onAction(post, "user")
            }
            
            tvContent.setOnClickListener {
                onAction(post, "post")
            }
            
            btnLike.setOnClickListener {
                onAction(post, "like")
            }
            
            btnComment.setOnClickListener {
                onAction(post, "comment")
            }
            
            btnBookmark.setOnClickListener {
                onAction(post, "bookmark")
            }
            
            btnMore.setOnClickListener {
                // TODO: 显示更多选项（举报等）
            }
        }
        
        private fun updateLikeState(post: Post) {
            if (post.isLiked) {
                ivLike.setImageResource(R.drawable.ic_like_filled)
                ivLike.setColorFilter(
                    ContextCompat.getColor(itemView.context, android.R.color.holo_red_light)
                )
                tvLikeCount.setTextColor(
                    ContextCompat.getColor(itemView.context, android.R.color.holo_red_light)
                )
            } else {
                ivLike.setImageResource(R.drawable.ic_like)
                ivLike.setColorFilter(
                    ContextCompat.getColor(itemView.context, R.color.text_secondary)
                )
                tvLikeCount.setTextColor(
                    ContextCompat.getColor(itemView.context, R.color.text_secondary)
                )
            }
        }
        
        private fun updateBookmarkState(post: Post) {
            if (post.isBookmarked) {
                ivBookmark.setImageResource(R.drawable.ic_bookmark_filled)
                ivBookmark.setColorFilter(
                    ContextCompat.getColor(itemView.context, android.R.color.holo_orange_light)
                )
            } else {
                ivBookmark.setImageResource(R.drawable.ic_bookmark)
                ivBookmark.setColorFilter(
                    ContextCompat.getColor(itemView.context, R.color.text_secondary)
                )
            }
        }
    }
}

