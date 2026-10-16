package com.kodex.guide.domain.usecase

import android.content.Context
import android.location.Geocoder
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject

data class PostCoordinates(
    val latitude: String,
    val longitude: String
)

class GeocodePostAddressUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend operator fun invoke(village: String, street: String, house: String): PostCoordinates? =
        withContext(Dispatchers.IO) {
            check(Geocoder.isPresent()) { "Служба определения координат недоступна" }

            val address = "$house, $street, $village, Россия"
            @Suppress("DEPRECATION")
            Geocoder(context, Locale("ru", "RU"))
                .getFromLocationName(address, 1)
                ?.firstOrNull()
                ?.let { PostCoordinates(it.latitude.toString(), it.longitude.toString()) }
        }
}
