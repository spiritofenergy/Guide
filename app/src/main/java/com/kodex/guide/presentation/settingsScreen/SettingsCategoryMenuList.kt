package com.kodex.guide.presentation.settingsScreen

import com.kodex.guide.R

object SettingsCategoryMenuList {
    val menuItemList = listOf(
        MenuItem.CategoryItem("Account Settings"),
        MenuItem.DialogItem("Personal Data",

        ),
        MenuItem.DialogItem("Address",
            dialogType = DialogType.ADDRESS,
            fieldsLabelsArrayId = R.array.address_array
        ),
        MenuItem.DialogItem("Password",
            dialogType = DialogType.PASSWORD,
            fieldsLabelsArrayId = R.array.password_array
        ),MenuItem.DialogItem(
            "Delete account",
            dialogType = DialogType.DELETE_ACCOUNT,
            fieldsLabelsArrayId = R.array.delete_account_array
        ),
        MenuItem.CategoryItem(
            "Image Settings",
        ),
        MenuItem.DropDownItem(
            "Image format",
            menuType = DropDownMenuType.IMAGE_FORMAT,
            arrayId = R.array.image_format_array

        ),
        MenuItem.DropDownItem(
            "Image quality",
            menuType = DropDownMenuType.IMAGE_QUALITY,
            arrayId = R.array.image_quality_array

        ),  MenuItem.DropDownItem(
            "Image size",
            menuType = DropDownMenuType.IMAGE_SIZE,
            arrayId = R.array.image_size_array

        ),
                )
}