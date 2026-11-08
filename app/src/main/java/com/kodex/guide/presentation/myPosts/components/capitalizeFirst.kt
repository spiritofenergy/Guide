package com.kodex.guide.presentation.myPosts.components


// Функция для преобразования строки в верхний регистр
fun String.capitalizeFirst(): String {
    return if (isEmpty()) this else this[0].uppercase() + this.substring(1)
}
