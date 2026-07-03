package com.example.note_app_kotllin.ui.screens.settings

import android.annotation.SuppressLint
import android.widget.Space
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.note_app_kotllin.R
import com.example.note_app_kotllin.core.constants.ApiUrls
import com.example.note_app_kotllin.core.constants.Paddings
import com.example.note_app_kotllin.core.constants.Spaces
import com.example.note_app_kotllin.core.extensions.getCurrentLanguage
import com.example.note_app_kotllin.core.navigation.Register
import com.example.note_app_kotllin.core.navigation.Settings
import com.example.note_app_kotllin.ui.components.LanguageBottomSheet
import com.example.note_app_kotllin.ui.screens.settings.components.SettingTile
import com.example.note_app_kotllin.ui.screens.settings.components.UserImage

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun SettingsScreen(
    navController: NavHostController = rememberNavController(),
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.settings_title)) }, navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.cd_back)
                    )
                }
            })
        }) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .fillMaxSize()
        ) {
            Row(modifier = Modifier.padding(vertical = Paddings.Medium) ){
                Box(
                    modifier = Modifier.padding(horizontal = Paddings.Medium)
                        .size(70.dp)
                        .clip(CircleShape)
                        .border(width = 2.dp, color = MaterialTheme.colorScheme.primary, shape = CircleShape)
                ) {
                    UserImage(ApiUrls.USER_MOCK_IMAGE)
                }
                Column {
                    Text(
                        state.userName,

                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(Modifier.height(Spaces.Mini))
                    Text(
                        state.userEmail, style = MaterialTheme.typography.bodySmall

                    )
                }
            }
            SettingTile(
                title = stringResource(R.string.settings_language),
                onClick = { viewModel.openLanguageSheet() },
                trailingContent = {
                    Text(
                        text = context.getCurrentLanguage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                })

            SettingTile(
                title = stringResource(R.string.settings_dark_mode), trailingContent = {
                    Switch(
                        checked = state.isDarkMode,
                        onCheckedChange = { viewModel.changeThemeMode() })
                })
            SettingTile(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                onClick = {
                    viewModel.logout()
                    navController.navigate(Register) {
                        popUpTo(0) {
                            inclusive = true
                        }
                    }
                },
                trailingContent = {
                    Text(
                        text = stringResource(R.string.settings_logout),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.width(Spaces.Medium))
                    Icon(
                        Icons.AutoMirrored.Outlined.ExitToApp,
                        contentDescription = stringResource(R.string.settings_logout),
                    )
                })
            if (state.isLanguageSheetOpen) {
                LanguageBottomSheet(
                    onDismissRequest = { viewModel.closeLanguageSheet() })
            }
        }
    }
}