package com.example.facenote.feature.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.facenote.core.model.ThemeConfig
import com.example.facenote.core.ui.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
	onNavigateBack: () -> Unit,
	viewModel: SettingsViewModel = hiltViewModel()
) {

	val theme by viewModel.themeState.collectAsState()

	Scaffold(
		topBar = {
			TopAppBar(
				title = { Text(stringResource(R.string.settings)) },
				navigationIcon = {
					IconButton(onClick = onNavigateBack) {
						Icon(
							painter = painterResource(R.drawable.ic_arrow_back),
							contentDescription = null
						)
					}
				}
			)
		}
	) { paddingValues ->
		Box(
			modifier = Modifier
				.padding(paddingValues)
				.fillMaxSize()
		){
			Column{
				Text(
					text = stringResource(R.string.theme),
					modifier = Modifier.padding(horizontal = 16.dp),
					style = MaterialTheme.typography.titleMedium
				)
				ThemeConfig.entries.forEach {
					Row (
						modifier = Modifier
							.fillMaxWidth()
							.clickable {
								viewModel.setTheme(it)
							}
							.padding(16.dp),
						verticalAlignment = Alignment.CenterVertically
					){
						RadioButton(
							selected = theme == it,
							onClick = { viewModel.setTheme(it) },
							modifier = Modifier.size(20.dp)
						)
						Spacer(Modifier.width(16.dp))
						Text(
							text = when(it){
								ThemeConfig.LIGHT -> "Light"
								ThemeConfig.DARK -> "Dark"
								else -> "System default"
							},
							style = MaterialTheme.typography.bodyMedium
						)
					}
				}
			}
		}
	}
}