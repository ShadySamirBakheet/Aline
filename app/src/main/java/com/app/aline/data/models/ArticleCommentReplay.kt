package com.app.aline.data.models

import java.util.*

data class ArticleCommentReplay(
    val id: String? = null,
    var commentId: String? = null,
    var comment: String? = null,
    var userID: String? = null,
    var userName: String? = null,
    var userImage: String? = null,
    var date: Date = Date(),
)