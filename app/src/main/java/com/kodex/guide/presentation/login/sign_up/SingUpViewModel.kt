package com.kodex.guide.presentation.login.sign_up

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kodex.guide.data.model.PersonalDataDTO
import com.kodex.guide.data.source.local.PreferenceDataSource
import com.kodex.guide.data.source.remote.FirebaseUserSettingsDataSource
import com.kodex.guide.domain.model.User
import com.kodex.guide.domain.repository.AuthRepo
import com.kodex.guide.presentation.navigation.NavRoutes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import javax.inject.Inject
import kotlinx.coroutines.launch


@HiltViewModel
class SingUpViewModel @Inject constructor(
    private val authRepo : AuthRepo,
    private val firebaseUserSettingsDataSource: FirebaseUserSettingsDataSource,
    private val preferenceDataSource: PreferenceDataSource
) : ViewModel() {
    val currentUser = mutableStateOf<User?>(null)

    val errorState = mutableStateOf("")
    val isLoading = mutableStateOf(false)
    val successState = mutableStateOf(false)
    val emailState = mutableStateOf("")
    val passwordState = mutableStateOf("")
    val nameState = mutableStateOf("")
    val phoneState = mutableStateOf("")

    init {
        preferenceDataSource.clearSavedPassword()
    }

    fun signUp(
        onSignUpSuccess: (NavRoutes.HomeDataObject) -> Unit
    ) = viewModelScope.launch {
        if (isLoading.value) return@launch

        isLoading.value = true
        errorState.value = ""
        try {
            val email = emailState.value.trim()
            val name = nameState.value.trim()
            val existingUser = authRepo.getCurrentUser()
                ?.takeIf { it.email.equals(email, ignoreCase = true) }
            val user = existingUser ?: authRepo.signUp(email, passwordState.value).getOrThrow()
            val userWithName = user.copy(userName = name)

            authRepo.createUserProfile(userWithName)
            firebaseUserSettingsDataSource.insertPersonalData(
                personalData = PersonalDataDTO(name, phoneState.value.trim())
            ).getOrThrow()

            onSignUpSuccess(NavRoutes.HomeDataObject(user.uid, user.email))
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            errorState.value = exception.message ?: "Не удалось создать аккаунт"
            Log.e("SignUp", "Не удалось завершить регистрацию", exception)
        } finally {
            isLoading.value = false
        }
    }
    fun saveLastName(){
        preferenceDataSource.saveName(PreferenceDataSource.NAME_KEY, nameState.value)
    }
    fun saveLastPhone(){
        preferenceDataSource.savePhone(PreferenceDataSource.PHONE_KEY, phoneState.value)
    }
    fun saveLastEmail(){
        preferenceDataSource.saveEmail(PreferenceDataSource.EMAIL_KEY, emailState.value)
    }

    fun getName() {
        nameState.value = preferenceDataSource.getName(PreferenceDataSource.NAME_KEY, "")
    }
    fun getPhone() {
        phoneState.value = preferenceDataSource.getPhone(PreferenceDataSource.PHONE_KEY, "")
    }
    fun getEmail() {
        emailState.value = preferenceDataSource.getEmail(PreferenceDataSource.EMAIL_KEY, "")
    }
        fun getAccountState(){
        currentUser.value = authRepo.getCurrentUser()
    }
}