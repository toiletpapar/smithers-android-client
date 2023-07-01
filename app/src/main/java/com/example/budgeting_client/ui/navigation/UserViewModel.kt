package com.example.budgeting_client.ui.navigation

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.budgeting_client.SmithersApplication
import com.example.budgeting_client.models.AuthUser
import com.example.budgeting_client.models.User
import com.example.budgeting_client.models.AuthUserErrors
import com.example.budgeting_client.repositories.UserRepository
import com.example.budgeting_client.utils.AppErrors
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

// initial state
data class UserUiState (
    val user: User? = null,
    val initialLoadComplete: Boolean = false,
    val errors: AppErrors<AuthUserErrors>? = null
)

// reduce
class UserViewModel constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    var uiState by mutableStateOf(UserUiState())
        private set

    private var fetchUserInfoJob: Job? = null
    fun initMyUserInfo() {
        fetchUserInfoJob?.cancel()
        fetchUserInfoJob = viewModelScope.launch {
            try {
                val response = userRepository.getMyUserInfo()

                uiState = if (response.isSuccessful) {
                    uiState.copy(
                        user = response.value,
                        errors = null,
                        initialLoadComplete = true
                    )
                } else {
                    uiState.copy(
                        initialLoadComplete = true,
                    )
                }
            } catch (e: Exception) {
                Log.e("BUDGETING_ERROR", e.message ?: "Unable to get own user info initially.")
                Log.e("BUDGETING_ERROR", e.stackTraceToString())

                uiState = uiState.copy(
                    initialLoadComplete = true,
                )
            }
        }
    }

    private var fetchJob: Job? = null
    fun login(authUser: AuthUser) {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            try {
                val response = userRepository.login(authUser)

                uiState = if (response.isSuccessful) {
                    uiState.copy(
                        user = response.value,
                        errors = null
                    )
                } else {
                    uiState.copy(
                        errors = response.errors,
                    )
                }
            } catch (e: Exception) {
                Log.e("BUDGETING_ERROR", e.message ?: "Unable to login.")
                Log.e("BUDGETING_ERROR", e.stackTraceToString())

                uiState = uiState.copy(
                    errors = AppErrors(listOf(AuthUserErrors.UNKNOWN_ERROR)),
                )
            }
        }
    }

    private var deleteJob: Job? = null
    fun logout() {
        deleteJob?.cancel()
        deleteJob = viewModelScope.launch {
            try {
                val response = userRepository.logout()

                uiState = if (response.isSuccessful) {
                    uiState.copy(
                        user = null,
                        errors = null
                    )
                } else {
                    uiState.copy(
                        errors = AppErrors(listOf(AuthUserErrors.UNKNOWN_ERROR)),
                    )
                }
            } catch (e: Exception) {
                Log.e("BUDGETING_ERROR", e.message ?: "Unable to login.")
                Log.e("BUDGETING_ERROR", e.stackTraceToString())

                uiState = uiState.copy(
                    errors = AppErrors(listOf(AuthUserErrors.UNKNOWN_ERROR)),
                )
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                // Get the Application object from extras
                val application = checkNotNull(extras[APPLICATION_KEY])

                return (application as SmithersApplication).userRepository?.let {
                    UserViewModel(
                        it
                    )
                } as T
            }
        }
    }
}
