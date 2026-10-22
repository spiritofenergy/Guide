package com.kodex.guide.presentation.login

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kodex.guide.data.repository.FirebaseAuthRepoImpl
import com.kodex.guide.domain.model.User
import com.kodex.guide.presentation.navigation.NavRoutes
import com.kodex.guide.data.source.local.PreferenceDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

@HiltViewModel
class SignInViewModel@Inject constructor(
    private val authRepo: FirebaseAuthRepoImpl,
    private val preferenceDataSource: PreferenceDataSource
): ViewModel() {
    val currentUser = mutableStateOf<User?>(null)
    val errorState = mutableStateOf("")
    val isLoading = mutableStateOf(false)
    val successState = mutableStateOf(false)
    val emailState = mutableStateOf("")
    val nameState = mutableStateOf("")
    val passwordState = mutableStateOf("")
    val resetPasswordState = mutableStateOf(false)
    val showResetPasswordDialog = mutableStateOf(false)


    fun signIn(
        onSignInSuccess: (NavRoutes.HomeDataObject)-> Unit,
    ) = viewModelScope.launch(Dispatchers.IO){
        if (isLoading.value) return@launch
        isLoading.value = true
        errorState.value = ""
        try {
            val result = authRepo.signIn(emailState.value.trim(), passwordState.value)
            result.fold(
                onSuccess = { user ->
                    user.email?.let { email ->
                        preferenceDataSource.saveEmail(PreferenceDataSource.EMAIL_KEY, email)
                    }
                    withContext(Dispatchers.Main) {
                        onSignInSuccess(NavRoutes.HomeDataObject(user.uid, user.email))
                    }
                },
                onFailure = { exception ->
                    errorState.value = exception.message ?: "Unknown error"
                }
            )
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            errorState.value = exception.message ?: "Не удалось выполнить вход"
            Log.e("SignIn", "Не удалось выполнить вход", exception)
        } finally {
            isLoading.value = false
        }
    }
    fun getEmail(){
        emailState.value = preferenceDataSource.getEmail(PreferenceDataSource.EMAIL_KEY, "")
    }

    fun saveLastEmail(){
        preferenceDataSource.saveEmail(PreferenceDataSource.EMAIL_KEY, emailState.value)
    }


     fun resetPassword() = viewModelScope.launch(Dispatchers.IO) {
        if (isLoading.value) return@launch
        isLoading.value = true
        errorState.value = ""
        try {
            val result = authRepo.resetPassword(emailState.value.trim())
            result.fold(
                onSuccess = {
                    resetPasswordState.value = false
                    showResetPasswordDialog.value = true
                },
                onFailure = { exception ->
                    errorState.value = exception.message ?: "Unknown error"
                }
            )
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            errorState.value = exception.message ?: "Не удалось отправить письмо для сброса пароля"
            Log.e("SignIn", "Не удалось сбросить пароль", exception)
        } finally {
            isLoading.value = false
        }
    }
  fun getAccountState(){
         currentUser.value = authRepo.getCurrentUser()
    }
        fun signOut(){
            authRepo.signOut()
            currentUser.value = null
        }
}