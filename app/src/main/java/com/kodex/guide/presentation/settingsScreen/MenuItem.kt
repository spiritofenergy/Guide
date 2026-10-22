package com.kodex.guide.presentation.settingsScreen

import com.kodex.guide.R


sealed class MenuItem{
    data class CategoryItem(val title: String): MenuItem()
    data class DialogItem(
        val title: String,
        val dialogType: DialogType = DialogType.PERSONAL_DATA,
        val fieldsLabelsArrayId: Int = R.array.personal_data_array
    ): MenuItem()
    data class DropDownItem(
        val title: String,
        val menuType: DropDownMenuType = DropDownMenuType.IMAGE_FORMAT,
        val arrayId: Int = R.array.image_format_array
    ): MenuItem()

}
enum class DropDownMenuType{
    IMAGE_FORMAT,
    IMAGE_QUALITY,
    IMAGE_SIZE

}

