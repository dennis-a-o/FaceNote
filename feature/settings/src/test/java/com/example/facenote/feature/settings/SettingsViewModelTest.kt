package com.example.facenote.feature.settings

import com.example.facenote.core.data.repository.SettingRepository
import com.example.facenote.core.model.ThemeConfig
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test


class SettingsViewModelTest {
	@OptIn(ExperimentalCoroutinesApi::class)
	private val testDispatcher = StandardTestDispatcher()
	private val testScope = TestScope(testDispatcher)

	private lateinit var viewModel: SettingsViewModel
	private lateinit var settingRepository: SettingRepository

	@OptIn(ExperimentalCoroutinesApi::class)
	@Before
	fun setUp() {
		Dispatchers.setMain(testDispatcher)

		settingRepository = mockk()
		//take care of getTheme that called in init block of viewmodel
		every {
			settingRepository.getTheme()
		} returns flow { emit(ThemeConfig.FOLLOW_SYSTEM) }

		viewModel = SettingsViewModel(settingRepository)
	}

	@OptIn(ExperimentalCoroutinesApi::class)
	@After
	fun tearDown() {
		Dispatchers.resetMain()
	}

	@OptIn(ExperimentalCoroutinesApi::class)
	@Test
	fun settingStateSetOnInitialLoad() = testScope.runTest {
		every {
			settingRepository.getTheme()
		} returns flow { emit(ThemeConfig.LIGHT) }

		viewModel.getTheme()

		advanceUntilIdle()

		assertTrue(viewModel.themeState.value == ThemeConfig.LIGHT)
	}

	@OptIn(ExperimentalCoroutinesApi::class)
	@Test
	fun settingStateUpdateWhenThemeIsUpdated() = testScope.runTest {
		viewModel.setTheme(ThemeConfig.DARK)

		coEvery {
			settingRepository.setTheme(ThemeConfig.DARK)
		} returns Unit

		advanceUntilIdle()

		assertTrue(viewModel.themeState.value == ThemeConfig.DARK)
	}

}