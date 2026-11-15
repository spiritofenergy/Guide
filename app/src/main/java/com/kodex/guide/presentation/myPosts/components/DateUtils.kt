package com.kodex.guide.presentation.myPosts.components

// Вспомогательная функция для расчета даты удаления
fun calculateDeleteDate(publishTimestamp: Long, periodInDays: Long): Long {
    // Переводим дни в миллисекунды: (дни * 24 часа * 60 минут * 60 секунд * 1000 мс)
    val periodInMillis = periodInDays * 24L * 60L * 60L * 1000L
    return publishTimestamp + periodInMillis
}