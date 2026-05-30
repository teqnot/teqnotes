package com.example.teqnotes.features.auth.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teqnotes.features.auth.domain.usecase.LoginUseCase
import com.example.teqnotes.features.auth.domain.usecase.LogoutUseCase
import com.example.teqnotes.features.auth.domain.usecase.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun onEvent(event: AuthUiEvent) {
        when (event.type) {
            AuthUiEvent.Type.LOGIN -> login(event.email, event.password)
            AuthUiEvent.Type.REGISTER -> register(event.email, event.password, event.name)
            AuthUiEvent.Type.LOGOUT -> logout()
        }
    }

    private fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            loginUseCase(email, password)
                .onSuccess { user -> _uiState.value = AuthUiState.Success(user) }
                .onFailure { error -> _uiState.value = AuthUiState.Error(error.message ?: "Login failed") }
        }
    }

    private fun register(email: String, password: String, name: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            registerUseCase(email, password, name)
                .onSuccess { user -> _uiState.value = AuthUiState.Success(user) }
                .onFailure {
                    error -> _uiState.value = AuthUiState.Error(error.message ?: "Registration failed")
                    Log.e("MY_DEBUG", "❌ Ошибка регистрации: ${error.message}", error)
                }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            logoutUseCase()
            _uiState.value = AuthUiState.Idle
        }
    }

    fun clearError() {
        if (_uiState.value is AuthUiState.Error) {
            _uiState.value = AuthUiState.Idle
        }
    }
}