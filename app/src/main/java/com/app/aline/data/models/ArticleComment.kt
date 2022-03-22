package com.app.aline.data.models

import java.util.*

data class ArticleComment(
    val id: String? = null,
    var articleId: String? = null,
    var comment: String? = null,
    var userID: String? = null,
    var userName: String? = null,
    var userImage: String? = null,
    var date: Date = Date(),
)