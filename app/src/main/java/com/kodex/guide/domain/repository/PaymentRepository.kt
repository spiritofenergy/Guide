package com.kodex.guide.domain.repository


import com.kodex.guide.domain.model.UserRole

// Результат оплаты (sealed class для строгой типизации)
sealed interface PaymentResult {
    data class Success(val transactionId: String) : PaymentResult
    data class Error(val message: String, val exception: Throwable? = null) : PaymentResult
    object Cancelled : PaymentResult
}

// Контракт для любого платежного шлюза (Яндекс, Google Play, Stripe и т.д.)
interface PaymentRepository {
    /**
     * Инициирует процесс оплаты.
     * @param tariff Целевой тариф (Бизнес или Премиум)
     * @param amount Сумма к оплате в копейках/центах (зависит от настроек бэкенда)
     * @return PaymentResult
     */
    suspend fun startPayment(tariff: UserRole, amount: Int): PaymentResult
}