package com.kodex.guide.presentation.home

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kodex.guide.domain.model.UserRole

/**
 * Вспомогательный composable для отображения TariffDialog.
 * Инкапсулирует логику определения кнопок — используется в HomeScreen и SettingsScreen.
 */
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

        // Если диалог открыт — показываем его
        if (showAuthDialog) {
            val isAnonymous = userRole == UserRole.ANONYMOUS

            // Определяем, какие кнопки видны
            val showUserBtn = isAnonymous || userRole == UserRole.USER
            val showBusinessBtn = isAnonymous || userRole == UserRole.BUSINESS
            val showPremiumBtn = isAnonymous || userRole == UserRole.BUSINESS

            // Для анонима первый тариф — Пользователь (бесплатно), для остальных — следующий по цепочке
            val nextRole = if (isAnonymous) UserRole.USER else viewModel.getNextRole(userRole)

            TariffDialog(
                currentRoleName = viewModel.getRoleDisplayName(userRole),
                nextRoleName = nextRole?.let { viewModel.getRoleDisplayName(it) },
                premiumRoleName = viewModel.getRoleDisplayName(UserRole.PREMIUM),

                // Для анонима Пользователь бесплатный, для других — проверяем по тарифу
                nextRequiresPayment = if (isAnonymous) false else nextRole?.let {
                    viewModel.requiresPayment(
                        it
                    )
                } ?: false,

                // ✅ ПЕРЕДАЕМ ФЛАГИ ПОКАЗА КНОПОК
                showNextButton = showUserBtn,
                showBusinessButton = showBusinessBtn,
                showPremiumButton = showPremiumBtn,
                showGuestButton = !isAnonymous && viewModel.isAuthorized.value,

                onDismiss = { viewModel.dismissAuthDialog() },

                // ==========================================
                // ЛОГИКА КЛИКОВ (ВОЗВРАЩЕНО)
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
}

    /*if (showAuthDialog) {
        val nextRole = viewModel.getNextRole(userRole)
        val nextRequiresPayment = nextRole?.let { viewModel.requiresPayment(it) } ?: false

        TariffDialog(
            currentRoleName = viewModel.getRoleDisplayName(userRole),
            nextRoleName = nextRole?.let { viewModel.getRoleDisplayName(it) },
            premiumRoleName = viewModel.getRoleDisplayName(UserRole.PREMIUM),
            nextRequiresPayment = nextRequiresPayment,
            showNextButton = nextRole != null && !nextRequiresPayment,
            showBusinessButton = nextRole == UserRole.BUSINESS && nextRequiresPayment,
            showPremiumButton = nextRole == UserRole.PREMIUM && nextRequiresPayment,
            showGuestButton = userRole == UserRole.USER ||
                    userRole == UserRole.BUSINESS ||
                    userRole == UserRole.PREMIUM ||
                    userRole == UserRole.ADMIN,
            onDismiss = { viewModel.dismissAuthDialog() },
            onNextTariffClick = {
                viewModel.dismissAuthDialog()
                viewModel.requestUpgrade(userRole)
            },
            onBusinessClick = {
                viewModel.dismissAuthDialog()
                viewModel.requestUpgrade(userRole)
            },
            onPremiumClick = {
                viewModel.dismissAuthDialog()
                viewModel.requestPremiumUpgrade()
            },
            onLogoutClick = {
                viewModel.dismissAuthDialog()
                viewModel.logout()
            }
        )
    }*/

