package com.kodex.guide.domain.usecase


    import android.util.Patterns
    import com.kodex.guide.domain.model.Book
    import javax.inject.Inject

    data class ValidationResult(
        val isValid: Boolean,
        val errors: List<String> = emptyList()
    )

    class ValidatePostUseCase @Inject constructor() {
        operator fun invoke(book: Book): ValidationResult {
            val errors = mutableListOf<String>()

            if (book.imageUrl.isBlank()) errors.add("Загрузите фотографию")
            if (book.title.isBlank()) errors.add("Введите заголовок объявления")
            if (book.description.length < 20) errors.add("Описание должно быть длиннее 10 символов")
            if (book.price <= 0) errors.add("Укажите корректную цену")
            if (book.village.isBlank()) errors.add("Укажите населенный пункт")

            // Проверка телефона по паттерну Android
            if (!Patterns.PHONE.matcher(book.telephone).matches()) {
                errors.add("Некорректный номер телефона")
            }

            return ValidationResult(errors.isEmpty(), errors)
        }
    }
