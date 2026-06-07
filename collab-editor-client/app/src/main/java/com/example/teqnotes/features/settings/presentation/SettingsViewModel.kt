package com.example.teqnotes.features.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teqnotes.core.storage.TokenStorage
import com.example.teqnotes.features.auth.domain.usecase.DeleteAccountUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val tokenStorage: TokenStorage,
    private val deleteAccountUseCase: DeleteAccountUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadUserInfo()
    }

    private fun loadUserInfo() {
        viewModelScope.launch {
            tokenStorage.getCurrentUser().collect { userInfo ->
                _uiState.update {
                    it.copy(
                        userName = userInfo?.name ?: "Гость",
                        userEmail = userInfo?.email ?: ""
                    )
                }
            }
        }
    }

    fun deleteAccount(): Result<Unit> {
        viewModelScope.launch {
            deleteAccountUseCase()
                .onSuccess {
                    tokenStorage.clear()
                }
                .onFailure { error ->
                    _uiState.update { it.copy(error = error.message) }
                }
        }
        return Result.success(Unit)
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

data class SettingsUiState(
    val userName: String = "",
    val userEmail: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)