package com.kodex.guide.di


import com.kodex.guide.data.repository.YandexPaymentRepositoryImpl
import com.kodex.guide.domain.repository.PaymentRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PaymentModule {
    @Binds
    @Singleton
    abstract fun bindPaymentRepository(
        yandexPaymentRepositoryImpl: YandexPaymentRepositoryImpl
    ): PaymentRepository
}