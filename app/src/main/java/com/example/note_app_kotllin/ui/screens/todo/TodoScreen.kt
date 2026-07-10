package com.example.note_app_kotllin.ui.screens.todo

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.note_app_kotllin.R
import com.example.note_app_kotllin.core.constants.IconSizes
import com.example.note_app_kotllin.core.constants.Paddings
import com.example.note_app_kotllin.core.constants.Spaces
import com.example.note_app_kotllin.domain.models.Todo
import com.example.note_app_kotllin.ui.components.EmptyStateBox
import com.example.note_app_kotllin.ui.screens.todo.components.TodoCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoScreen(
    navController: NavHostController,
    parentPadding: PaddingValues = PaddingValues(),
    viewModel: TodoViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val focusManager = LocalFocusManager.current

    val todoList: List<Todo> = if (state.draftTodo != null) {
        listOf(state.draftTodo!!) + state.todos
    } else {
        state.todos
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.handleScreenExit()
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                containerColor = MaterialTheme.colorScheme.primary,
                onClick = { viewModel.createTodo() },
                modifier = Modifier.offset(y = -parentPadding.calculateBottomPadding())
            ) {
                Icon(
                    painterResource(R.drawable.edit),
                    contentDescription = null,
                    modifier = Modifier.size(
                        IconSizes.LargePlus
                    )
                )
            }
        }) { innerScaffoldPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { focusManager.clearFocus() })
                }) {
            if (todoList.isEmpty()) {
                EmptyStateBox(
                    stringResource(R.string.empty_state),
                    painterResource(R.drawable.edit),
                    modifier = Modifier.align(
                        Alignment.Center
                    )
                )
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = parentPadding.calculateTopPadding()),

                    contentPadding = PaddingValues(
                        start = Paddings.Medium,
                        end = Paddings.Medium,
                        top = Paddings.Medium,
                        bottom = Paddings.Medium + parentPadding.calculateBottomPadding() + Spaces.Medium
                    ),
                    verticalArrangement = Arrangement.spacedBy(Spaces.Medium)
                ) {
                    itemsIndexed(
                        items = todoList,
                        key = { _, todo -> todo.id.ifEmpty { "draft" } }) { _, todo ->
                        TodoCard(
                            initialDescription = todo.description,
                            isCompleted = todo.isCompleted,
                            isFocused = state.focusedTodoId == todo.id,
                            onFocusGained = { viewModel.onFocusGained(todo.id, todo.description) },
                            onTextChange = { viewModel.onTextChange(it) },
                            onFocusLost = {
                                viewModel.handleFocusLost(
                                    todo.id, it, todo.isCompleted
                                )
                            },
                            onCheckedChange = {
                                viewModel.updateTodoCompletion(
                                    todo.id, todo.description, it
                                )
                            },
                            onLongClick = { viewModel.deleteTodo(todo.id) })
                    }
                }
            }
        }
    }
}