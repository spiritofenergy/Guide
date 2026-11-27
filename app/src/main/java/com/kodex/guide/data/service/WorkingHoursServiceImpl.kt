package com.kodex.guide.data.service

import android.os.Build
import androidx.annotation.RequiresApi
import com.kodex.guide.domain.service.WorkingHoursService
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class WorkingHoursServiceImpl : WorkingHoursService {

    @RequiresApi(Build.VERSION_CODES.O)
    private val timeFormat = DateTimeFormatter.ofPattern("HH:mm")

    @RequiresApi(Build.VERSION_CODES.O)
    override fun isOpenNow(openingHours: String, now: LocalTime): Boolean {
        val source = openingHours.trim()
        if (source.isEmpty()) return false

        val normalized = source.lowercase()
        if (normalized == "24" ||
            normalized == "24/7" ||
            normalized.contains("круглосуточно", ignoreCase = true)
        ) return true

        return try {
            val parts = source.split("-", "–", "—").map { it.trim() }
            if (parts.size != 2) return false

            val open = parseTime(parts[0])
            val close = if (parts[1] == "24:00") LocalTime.MAX else parseTime(parts[1])

            when {
                // Обычный интервал: 09:00 - 20:00
                close.isAfter(open) -> !now.isBefore(open) && !now.isAfter(close)
                // Переход через полночь: 22:00 - 06:00
                else -> !now.isBefore(open) || !now.isAfter(close)
            }
        } catch (e: Exception) {
            false
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun parseTime(value: String): LocalTime = LocalTime.parse(value, timeFormat)
}
