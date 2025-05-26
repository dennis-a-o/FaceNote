package com.example.facenote

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.facenote.core.data.repository.SettingRepository
import com.example.facenote.core.model.ThemeConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
	settingRepository: SettingRepository
): ViewModel() {
	val uiState = settingRepository.getTheme().map {
		MainActivityUiState.Success(it)
	}.stateIn(
		scope = viewModelScope,
		initialValue = MainActivityUiState.Loading,
		started = SharingStarted.WhileSubscribed(5_000)
	)

}

sealed class MainActivityUiState{
	data object  Loading: MainActivityUiState()
	data class  Success(val themeConfig: ThemeConfig): MainActivityUiState()
}