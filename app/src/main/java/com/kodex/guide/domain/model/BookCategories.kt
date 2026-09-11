package com.kodex.guide.domain.model

enum class BookCategories(
    val id : Int
) {
    ANIMALS(0),       // "Животные"
    PLANTS(1),        // "Растения"
    WORK(2),          // "Работа"
    SERVICES(3),      // "Услуги"
    REAL_ESTATE(4),   // "Недвижимость"
    AUTO(5),          // "Авто"
    ELECTRONICS(6),   // "Электроника"
    ENTERTAINMENTS(7),// "Развлечения"
    OTHER(8),         // "Сохраненные"
    NEWS(9),           // "Все" (id=0, но в коде стоит внизу)
    ALL(10);
    companion object{
        fun fromId(id: Int): BookCategories{
            return entries.firstOrNull{ entry ->
                entry.id == id
            } ?: ALL
        }
    }
 }