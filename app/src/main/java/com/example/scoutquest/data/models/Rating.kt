package com.example.scoutquest.data.models

data class Rating(
    val totalVotes: Int = 0,
    val totalStars: Int = 0,
    val comments: Map<String, Comment> = mapOf()
) {
    val averageRating: Float
        get() = if (totalVotes > 0) totalStars.toFloat() / totalVotes else 0f

    fun addComment(userId: String, comment: Comment): Rating {
        return if (!comments.containsKey(userId)) {
            this.copy(comments = comments + (userId to comment))
        } else {
            this
        }
    }

    fun removeComment(userId: String): Rating {
        return this.copy(comments = comments - userId)
    }

    fun updateComment(userId: String, newComment: String): Rating {
        return if (comments.containsKey(userId)) {
            val updatedComment = comments[userId]?.copy(text = newComment) ?: return this
            this.copy(comments = comments + (userId to updatedComment))
        } else {
            this
        }
    }
}
