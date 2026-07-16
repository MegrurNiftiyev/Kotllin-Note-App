package com.example.note_app_kotllin.ui.screens.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.note_app_kotllin.R
import com.example.note_app_kotllin.core.constants.AppDurations
import com.example.note_app_kotllin.core.constants.BorderRadiuses
import com.example.note_app_kotllin.core.constants.IconSizes
import com.example.note_app_kotllin.core.constants.Paddings
import com.example.note_app_kotllin.core.navigation.AiChat
import com.example.note_app_kotllin.core.navigation.Home
import com.example.note_app_kotllin.core.navigation.Notes
import com.example.note_app_kotllin.core.navigation.Settings
import com.example.note_app_kotllin.core.navigation.Todos
import com.example.note_app_kotllin.ui.screens.notes.NotesScreen
import com.example.note_app_kotllin.ui.screens.todo.TodoScreen

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun HomeScreen(
    navController: NavHostController = rememberNavController()
) {
    var selectedTab by remember { mutableStateOf<Any>(Notes) }
    val navIconColor = NavigationBarItemDefaults.colors(
        selectedIconColor = MaterialTheme.colorScheme.onPrimary,
        selectedTextColor = MaterialTheme.colorScheme.primary,
        indicatorColor = MaterialTheme.colorScheme.primary,
        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
    )
    Scaffold(containerColor = Color.Transparent, topBar = {
        Column {
            TopAppBar(title = { Text(stringResource(R.string.app_name)) }, actions = {
                IconButton(onClick = {
                    navController.navigate(AiChat) {
                        popUpTo(Home) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }

                }) {
                    Icon(
                        painter = painterResource(R.drawable.ai),
                        contentDescription = stringResource(id = R.string.settings_title),
                        modifier = Modifier.size(IconSizes.LargePlus)
                    )
                }
                IconButton(onClick = { navController.navigate(Settings) }) {
                    Icon(
                        painter = painterResource(R.drawable.settings),
                        contentDescription = stringResource(id = R.string.settings_title),
                        modifier = Modifier.size(IconSizes.LargePlus)
                    )
                }
            })
            HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.primary)
        }
    }, bottomBar = {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    bottom = Paddings.Medium,
                    start = Paddings.LargeMinus * 3,
                    end = Paddings.LargeMinus * 3
                )
                .height(80.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = RoundedCornerShape(BorderRadiuses.Huge),
            tonalElevation = 8.dp
        ) {
            androidx.compose.foundation.layout.Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavigationBarItem(
                    selected = selectedTab == Notes,
                    onClick = { selectedTab = Notes },
                    icon = {
                        Icon(
                            painterResource(R.drawable.home),
                            contentDescription = "Home",
                            modifier = Modifier.size(IconSizes.LargePlus)
                        )
                    },
                    colors = navIconColor
                )

                NavigationBarItem(
                    selected = selectedTab == Todos,
                    onClick = { selectedTab = Todos },
                    icon = {
                        Icon(
                            painterResource(R.drawable.to_do_list),
                            contentDescription = "Todos",
                            modifier = Modifier.size(IconSizes.LargePlus)
                        )
                    },
                    colors = navIconColor
                )
            }
        }
    }) { innerPadding ->

        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            AnimatedContent(
                targetState = selectedTab, label = "TabTransition", transitionSpec = {

                    if (initialState == Notes && targetState == Todos) {
                        (slideInHorizontally(
                            animationSpec = tween(
                                AppDurations.ExtraShort.toInt()
                            )
                        ) { width -> width } + fadeIn(
                            animationSpec = tween(
                                AppDurations.ExtraShort.toInt()

                            )
                        )) togetherWith (slideOutHorizontally(
                            animationSpec = tween(
                                AppDurations.ExtraShort.toInt()

                            )
                        ) { width -> -width } + fadeOut(
                            animationSpec = tween(
                                AppDurations.ExtraShort.toInt()

                            )
                        ))
                    } else {
                        (slideInHorizontally(
                            animationSpec = tween(
                                AppDurations.ExtraShort.toInt()

                            )
                        ) { width -> -width } + fadeIn(
                            animationSpec = tween(
                                AppDurations.ExtraShort.toInt()

                            )
                        )) togetherWith (slideOutHorizontally(
                            animationSpec = tween(
                                AppDurations.ExtraShort.toInt()

                            )
                        ) { width -> width } + fadeOut(
                            animationSpec = tween(
                                AppDurations.ExtraShort.toInt()

                            )
                        ))
                    }.using(SizeTransform(clip = false))
                }) { targetTab ->
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    when (targetTab) {
                        Notes -> NotesScreen(
                            navController = navController, parentPadding = innerPadding
                        )

                        Todos -> TodoScreen(
                            navController = navController, parentPadding = innerPadding
                        )
                    }
                }
            }
        }
    }
}