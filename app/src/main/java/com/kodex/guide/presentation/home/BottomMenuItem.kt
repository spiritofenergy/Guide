package com.kodex.guide.presentation.home

import com.kodex.guide.R


sealed class BottomMenuItem(
    val route: String,
    val titleId: Int,
    val iconId: Int
) {
    object Home: BottomMenuItem(
        route = "home",
        titleId = R.string.home,
        iconId = R.drawable.ic_home
    )
    object Saved: BottomMenuItem(
        route = "saved",
        titleId = R.string.saved,
        iconId = R.drawable.bookmark
    )
    object Settings: BottomMenuItem(
        route = "setting",
        titleId = R.string.settings,
        iconId = R.drawable.ic_settings
    )
}