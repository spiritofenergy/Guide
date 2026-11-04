package com.kodex.guide.presentation.settingsScreen.menuItems

import com.kodex.guide.domain.model.UserRole


fun getRoleDisplayName(role: UserRole): String {
    return when (role) {
        UserRole.ANONYMOUS -> "Гостевой"
        UserRole.USER -> "Пользователь"
        UserRole.BUSINESS -> "Бизнес"
        UserRole.PREMIUM -> "Премиум"
        UserRole.ADMIN -> "Администратор"
    }
}