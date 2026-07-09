package com.example.note_app_kotllin.ui.screens.note_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.note_app_kotllin.R
import com.example.note_app_kotllin.core.constants.Paddings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(
    id: String,
    title: String,
    subtitle: String,
    isSynced: Boolean,
    navController: NavHostController,
    viewModel: NoteDetailViewModel = hiltViewModel()
) {
    var titleTextInput by remember { mutableStateOf(title) }
    var subtitleTextInput by remember { mutableStateOf(subtitle) }
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    val textFieldColors = TextFieldDefaults.colors(
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent
    )

    DisposableEffect(Unit) {
        onDispose {
            viewModel.handleSaveOrDelete(
                id = id,
                initialTitle = title,
                initialContent = subtitle,
                currentTitle = titleTextInput,
                currentContent = subtitleTextInput
            )
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(title = { Text(" ") }, navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(id = R.string.cd_back)
                    )
                }
            }, actions = {
                Icon(
                    imageVector = if (isSynced) Icons.Filled.Check else Icons.Filled.Close,
                    contentDescription = null,
                    tint = if (state.isSynced) MaterialTheme.colorScheme.primary else Color.Gray,
                    modifier = Modifier.padding(horizontal = Paddings.ExtraSmall)
                )
                IconButton(
                    onClick = {
                        viewModel.deleteNote(id)
                        navController.popBackStack()
                    }) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = stringResource(id = R.string.cd_delete)
                    )
                }

            })
        }) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(Paddings.Small)
                .verticalScroll(scrollState)
        ) {
            Text(subtitleTextInput.length.toString(), modifier = Modifier.align(Alignment.End))
            TextField(
                value = titleTextInput,
                onValueChange = { titleTextInput = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.Transparent),
                textStyle = MaterialTheme.typography.titleLarge,
                maxLines = 3,
                placeholder = { Text(stringResource(id = R.string.hint_title)) },
                colors = textFieldColors
            )
            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = Paddings.Small)
            )
            TextField(
                value = subtitleTextInput,
                onValueChange = { subtitleTextInput = it },
                modifier = Modifier.fillMaxSize(),
                textStyle = MaterialTheme.typography.bodyLarge,
                placeholder = { Text(stringResource(id = R.string.hint_subtitle)) },
                colors = textFieldColors
            )
        }
    }
}