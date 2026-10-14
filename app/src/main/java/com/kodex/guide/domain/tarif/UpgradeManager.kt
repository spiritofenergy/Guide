package com.kodex.guide.domain.tarif

import com.kodex.guide.domain.model.UserRole
import com.kodex.guide.domain.repository.UserRoleRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class UpgradeManager @Inject constructor(
    private val authStateProvider: AuthStateProvider,
    private val tariffPolicy: TariffPolicy,
    private val userRoleRepo: UserRoleRepo
) {
    private val _desiredRole = MutableStateFlow<UserRole?>(null)

    // ✅ Повышение по цепочке: ANONYMOUS → USER → BUSINESS → PREMIUM
    fun decideUpgrade(currentRole: UserRole): UpgradeDecision {
        val nextRole = tariffPolicy.nextRole(currentRole)
            ?: return UpgradeDecision.MaxRole

        return decideForTarget(nextRole)
    }

    // ✅ Прямой переход на PREMIUM, минуя BUSINESS
    fun decidePremium(): UpgradeDecision {
        return decideForTarget(UserRole.PREMIUM)
    }

    fun clearDesiredRole() {
        _desiredRole.value = null
    }

    // ✅ Применить желаемый тариф после успешной регистрации
    suspend fun applyDesiredRole(): ApplyRoleResult {
        val role = _desiredRole.value ?: return ApplyRoleResult.NoDesiredRole

        val currentUser = authStateProvider.currentUser()
            ?: return ApplyRoleResult.Failed("Пользователь не авторизован")

        // 💳 Платный тариф: после регистрации показываем оплату
        if (tariffPolicy.requiresPayment(role)) {
            return ApplyRoleResult.PaymentRequired(role)
        }

        // Бесплатный тариф — применяем сразу
        return userRoleRepo.updateUserRole(currentUser.uid, role).fold(
            onSuccess = {
                _desiredRole.value = null
                ApplyRoleResult.Updated(role)
            },
            onFailure = { error ->
                ApplyRoleResult.Failed(error.message ?: "Не удалось обновить тариф")
            }
        )
    }

    // ✅ Бесплатное повышение на следующий тариф
    suspend fun applyNextFreePlan(
        uid: String,
        currentRole: UserRole
    ): Result<UserRole> {
        val nextRole = tariffPolicy.nextRole(currentRole)
            ?: return Result.failure(IllegalStateException("У вас уже максимальный тариф"))

        if (tariffPolicy.requiresPayment(nextRole)) {
            return Result.failure(IllegalStateException("Этот тариф требует оплаты"))
        }

        return userRoleRepo.updateUserRole(uid, nextRole).map { nextRole }
    }

    private fun decideForTarget(target: UserRole): UpgradeDecision {
        val currentUser = authStateProvider.currentUser()

        _desiredRole.value = target

        return if (tariffPolicy.requiresPayment(target)) {
            if (currentUser == null || currentUser.isAnonymous) {
                // Аноним: сначала регистрация, оплата после неё
                UpgradeDecision.AuthRequired(target)
            } else {
                // Уже зарегистрирован — сразу оплата
                UpgradeDecision.PaymentRequired(target)
            }
        } else {
            // Бесплатное повышение — нужна регистрация/вход
            UpgradeDecision.AuthRequired(target)
        }
    }
}