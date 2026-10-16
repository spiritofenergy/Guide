package com.kodex.guide.presentation.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kodex.guide.domain.model.UserRole

/**
 * Вспомогательный composable для отображения TariffDialog.
 * Инкапсулирует логику определения кнопок — используется в HomeScreen и SettingsScreen.
 */
@Composable
fun ShowTariffDialog(
    viewModel: HomeViewModel,
    userRole: UserRole
) {
    val showAuthDialog by viewModel.showAuthDialog
    val isLogoutDialog by viewModel.isLogoutDialog

    if (showAuthDialog) {
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
    }
}
