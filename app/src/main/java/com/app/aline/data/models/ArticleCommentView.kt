package com.app.aline.data.models

data class ArticleCommentView(
    var comment: ArticleComment,
    var replays :List<ArticleCommentReplay>?=null,
)
