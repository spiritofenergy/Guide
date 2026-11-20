package com.kodex.guide.presentation.myPosts


import com.kodex.guide.domain.model.PostEditorState

/**
 * События, которые ViewModel отправляет на экран (UI)
 */
sealed interface MyPostsEvent {

    data class Toast(val message: String) : MyPostsEvent
    data class RestoreSample(val state: PostEditorState) : MyPostsEvent
    data class DescriptionGenerated(val text: String) : MyPostsEvent

    object NavigateBack : MyPostsEvent
}