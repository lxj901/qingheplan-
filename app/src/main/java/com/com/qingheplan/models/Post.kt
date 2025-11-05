package com.com.qingheplan.models

/**
 * 帖子数据模型
 */
data class Post(
    val id: String,
    val userId: String,
    val username: String,
    val avatar: String,
    val title: String,
    val content: String,
    var likeCount: Int,
    val commentCount: Int,
    val bookmarkCount: Int,
    val viewCount: Int,
    val time: String,
    var isLiked: Boolean = false,
    var isBookmarked: Boolean = false
)

