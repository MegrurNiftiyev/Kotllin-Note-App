package com.example.note_app_kotllin.ui.screens.aichat

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.note_app_kotllin.R
import com.example.note_app_kotllin.core.constants.IconSizes
import com.example.note_app_kotllin.core.constants.Paddings
import com.example.note_app_kotllin.core.navigation.AiChat
import com.example.note_app_kotllin.core.navigation.Home
import com.example.note_app_kotllin.ui.components.EmptyStateBox
import com.example.note_app_kotllin.ui.screens.aichat.componenets.AiChatInputBar
import com.example.note_app_kotllin.ui.screens.aichat.componenets.AiTypingBubble
import com.example.note_app_kotllin.ui.screens.aichat.componenets.MessageBubble

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiChatScreen(
    navController: NavHostController, viewModel: AiChatViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    val onBackNavigate = {
        navController.navigate(Home) {
            popUpTo(AiChat) {
                saveState = true
                inclusive = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    BackHandler {
        onBackNavigate()
    }

    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) {
            listState.animateScrollToItem(state.messages.size - 1)
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets.ime,
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.ai_chat_title)) }, navigationIcon = {
                IconButton(onClick = {
                    onBackNavigate()
                }) {
                    Icon(
                        painterResource(R.drawable.lower_than),
                        contentDescription = stringResource(R.string.cd_back),
                        modifier = Modifier.size(IconSizes.LargePlus)
                    )
                }
            })
        }) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    bottom = innerPadding.calculateBottomPadding()
                )
        ) {
            if (state.messages.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyStateBox(
                        text = stringResource(R.string.empty_ai_state),
                        icon = painterResource(R.drawable.ai)
                    )
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(
                        horizontal = Paddings.Small, vertical = Paddings.ExtraSmall
                    )
                ) {
                    items(state.messages, key = { it.id }) { message ->
                        MessageBubble(
                            id = message.id,
                            text = message.message,
                            tasks = message.tasks,
                            createdAt = message.createdAt,
                            type = message.type,
                            isAnimationCompleted = message.id in state.messageAnimationCompletedIds,
                            onAnimationCompleted = { viewModel.onAnimationCompleted(message.id) })
                    }

                    if (state.isLoading) {
                        item(key = "ai_typing") {
                            AiTypingBubble()
                        }
                    }
                }
            }

            AiChatInputBar(
                inputText = state.inputText,
                isLoading = state.isLoading,
                onInputChanged = viewModel::onInputChanged,
                onSendClicked = viewModel::onSendClicked,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Paddings.Small, vertical = Paddings.Small)

            )
        }
    }
}