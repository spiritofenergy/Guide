package com.kodex.guide.presentation.payment

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.kodex.guide.domain.model.UserRole
import com.kodex.guide.presentation.home.HomeViewModel

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun ShowTariffDialog(
    viewModel: HomeViewModel,
    userRole: UserRole
) {
    val showAuthDialog by viewModel.showAuthDialog
    val isLogoutDialog by viewModel.isLogoutDialog
    if (showAuthDialog) {
        val isAnonymous = userRole == UserRole.ANONYMOUS

        // Определяем, какие кнопки видны
        // ✅ СЕРАЯ КНОПКА (Пользователь) — ТОЛЬКО ДЛЯ ГОСТЕЙ
        val showUserBtn = isAnonymous

        // Зелёная кнопка (Бизнес) — для гостей и обычных пользователей
        val showBusinessBtn = isAnonymous || userRole == UserRole.USER

        // Фиолетовая кнопка (Премиум) — для гостей, пользователей и бизнеса
        val showPremiumBtn = isAnonymous || userRole == UserRole.USER || userRole == UserRole.BUSINESS

        // Для анонима первый тариф — Пользователь (бесплатно), для остальных — следующий по цепочке
        val nextRole = if (isAnonymous) UserRole.USER else viewModel.getNextRole(userRole)

        TariffDialog(
            currentRoleName = viewModel.getRoleDisplayName(userRole),
            nextRoleName = nextRole?.let { viewModel.getRoleDisplayName(it) },
            premiumRoleName = viewModel.getRoleDisplayName(UserRole.PREMIUM),

            // Для анонима Пользователь бесплатный, для других — проверяем по тарифу
            nextRequiresPayment = if (isAnonymous) false else nextRole?.let {
                viewModel.requiresPayment(it)
            } ?: false,

            // ✅ ПЕРЕДАЕМ ФЛАГИ ПОКАЗА КНОПОК
            showNextButton = showUserBtn,
            showBusinessButton = showBusinessBtn,
            showPremiumButton = showPremiumBtn,
            showGuestButton = !isAnonymous && viewModel.isAuthorized.value,

            onDismiss = { viewModel.dismissAuthDialog() },

            // ==========================================
            // ЛОГИКА КЛИКОВ
            // ==========================================

            onNextTariffClick = {
                viewModel.dismissAuthDialog()
                if (isAnonymous) {
                    viewModel.desiredRole.value = UserRole.USER
                    viewModel.navigateToAuth() // Уводим на регистрацию
                } else {
                    viewModel.requestUpgrade(userRole) // Логика оплаты/повышения
                }
            },

            onBusinessClick = {
                viewModel.dismissAuthDialog()
                viewModel.desiredRole.value = UserRole.BUSINESS
                if (isAnonymous) {
                    viewModel.navigateToAuth() // Сначала регистрация
                } else {
                    viewModel.requestUpgrade(userRole) // Оплата
                }
            },

            onPremiumClick = {
                viewModel.dismissAuthDialog()
                viewModel.desiredRole.value = UserRole.PREMIUM
                if (isAnonymous) {
                    viewModel.navigateToAuth() // Сначала регистрация
                } else {
                    viewModel.requestPremiumUpgrade() // Оплата Премиума
                }
            },

            onLogoutClick = {
                viewModel.dismissAuthDialog()
                viewModel.logout()
            }
        )
    }
}