package com.example.note_app_kotllin.ui.screens.aichat

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.note_app_kotllin.R
import com.example.note_app_kotllin.core.constants.IconSizes
import com.example.note_app_kotllin.core.constants.Paddings
import com.example.note_app_kotllin.ui.components.EmptyStateBox
import com.example.note_app_kotllin.ui.screens.aichat.componenets.AiChatInputBar
import com.example.note_app_kotllin.ui.screens.aichat.componenets.AiTypingBubble
import com.example.note_app_kotllin.ui.screens.aichat.componenets.MessageBubble

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiChatScreen(
    navController: NavHostController,
    viewModel: AiChatViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) {
            listState.animateScrollToItem(state.messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.ai_chat_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painterResource(R.drawable.lower_than),
                            contentDescription = stringResource(R.string.cd_back),
                            modifier = Modifier.size(IconSizes.LargePlus)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
        ) {
            if (state.messages.isEmpty()) {

                    EmptyStateBox(
                        text = stringResource(R.string.empty_ai_state),
                        icon = painterResource(R.drawable.ai),
                        modifier = Modifier.align(
                            Alignment.Center
                        )
                    )

            }  else LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 72.dp),
                contentPadding = PaddingValues(horizontal = Paddings.Small, vertical = Paddings.ExtraSmall)
            ) {

                items(state.messages, key = { it.id }) { message ->
                    MessageBubble(
                        text = message.message,
                        tasks = message.tasks,
                        createdAt = message.createdAt,
                        type = message.type
                    )
                }

                if (state.isLoading) {
                    item(key = "ai_typing") {
                        AiTypingBubble()
                    }
                }
            }

            AiChatInputBar(
                inputText = state.inputText,
                isLoading = state.isLoading,
                onInputChanged = viewModel::onInputChanged,
                onSendClicked = viewModel::onSendClicked,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = Paddings.Small)
            )
        }
    }
}



