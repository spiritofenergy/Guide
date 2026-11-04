package com.kodex.guide.domain.usecase

import com.kodex.guide.domain.model.Book
import com.kodex.guide.domain.repository.MyPostsRepo
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ObserveLatestSampleUseCase @Inject constructor(
    private val repo: MyPostsRepo
) {
    suspend operator fun invoke(uid: String): Book? =
        repo.observeMyPosts(uid).first().maxByOrNull { it.timeStamp }
}