package com.example.facenote.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.facenote.core.data.repository.SettingRepository
import com.example.facenote.core.model.ThemeConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
	private val settingRepository: SettingRepository
): ViewModel() {
	private val _themeState = MutableStateFlow(ThemeConfig.FOLLOW_SYSTEM)

	val themeState = _themeState.asStateFlow()

	init {
		getTheme()
	}

	fun setTheme(theme: ThemeConfig){
		viewModelScope.launch {
			settingRepository.setTheme(theme)
			_themeState.value = theme
		}
	}

	private fun getTheme(){
		viewModelScope.launch {
			val theme = settingRepository.getTheme().first()
			_themeState.value = theme
		}
	}
}