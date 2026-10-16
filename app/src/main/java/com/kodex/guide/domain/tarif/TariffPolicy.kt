package com.kodex.guide.domain.tarif

import com.kodex.guide.domain.model.UserRole
import javax.inject.Inject

interface TariffPolicy {

    fun nextRole(currentRole: UserRole): UserRole?

    fun displayName(role: UserRole): String

    fun requiresPayment(role: UserRole): Boolean
}

class DefaultTariffPolicy @Inject constructor() : TariffPolicy {

    override fun nextRole(currentRole: UserRole): UserRole? {
        return when (currentRole) {
            UserRole.ANONYMOUS -> UserRole.USER
            UserRole.USER -> UserRole.BUSINESS
            UserRole.BUSINESS -> UserRole.PREMIUM
            UserRole.PREMIUM -> null
            UserRole.ADMIN -> null
        }
    }

    override fun displayName(role: UserRole): String {
        return when (role) {
            UserRole.ANONYMOUS -> "Гостевой"
            UserRole.USER -> "Пользователь"
            UserRole.BUSINESS -> "Бизнес"
            UserRole.PREMIUM -> "Премиум"
            UserRole.ADMIN -> "Администратор"
        }
    }

    override fun requiresPayment(role: UserRole): Boolean {
        return role == UserRole.BUSINESS || role == UserRole.PREMIUM
    }
}