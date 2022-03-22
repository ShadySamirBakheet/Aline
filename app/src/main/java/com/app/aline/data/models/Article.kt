package com.app.aline.data.models

import kotlin.collections.Map

data class Article(
    val id: String? = null,
    var articleTitle: String? = null,
    var article: String? = null,
    var articleImage: String? = null,
    var userId: String? = null,
    var deptId: String? = null,
){
    fun toMap(): Map<String, String?> {
        val map: Map<String, String?> = mapOf(
            "id" to id,
            "articleTitle" to articleTitle,
            "article" to article,
            "articleImage" to articleImage,
            "userId" to userId,
            "deptId" to deptId,
        )


        return map
    }

    private fun mapOf(pair: () -> Pair<String, String?>): Map<String, Any> {
        TODO("Not yet implemented")
    }
}