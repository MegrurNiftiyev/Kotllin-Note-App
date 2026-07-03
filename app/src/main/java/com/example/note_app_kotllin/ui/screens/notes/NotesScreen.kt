package com.example.note_app_kotllin.ui.screens.notes

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.note_app_kotllin.R
import com.example.note_app_kotllin.core.constants.Paddings
import com.example.note_app_kotllin.core.constants.Spaces
import com.example.note_app_kotllin.core.navigation.NoteDetail
import com.example.note_app_kotllin.ui.components.EmptyStateBox
import com.example.note_app_kotllin.ui.screens.notes.components.NoteCard

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    navController: NavHostController,
    parentPadding: PaddingValues = PaddingValues(),
    viewModel: NotesViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),

        floatingActionButton = {
            FloatingActionButton(
                containerColor = MaterialTheme.colorScheme.primary,
                onClick = { navController.navigate(NoteDetail("", "", "", true)) },
                modifier = Modifier.offset(y = -parentPadding.calculateBottomPadding())
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        }) {
        Box {
            if (state.isLoading && state.notes.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                PullToRefreshBox(
                    isRefreshing = state.isRefreshing,
                    onRefresh = { viewModel.refreshNotes() },
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (state.notes.isEmpty()) {
                        EmptyStateBox(stringResource(R.string.empty_state))
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = parentPadding.calculateTopPadding()),
                            contentPadding = PaddingValues(Paddings.Medium),
                            verticalArrangement = Arrangement.spacedBy(Spaces.Medium)
                        ) {
                            items(state.notes) { pair ->
                                NoteCard(
                                    title = pair.title, subtitle = pair.content, onClick = {
                                        navController.navigate(
                                            NoteDetail(
                                                pair.id,
                                                pair.title,
                                                pair.content,
                                                pair.isSynced
                                            )
                                        )
                                    })
                            }
                        }
                    }
                }
            }
        }
    }
}
