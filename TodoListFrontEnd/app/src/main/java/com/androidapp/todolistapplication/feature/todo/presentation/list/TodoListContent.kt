package com.androidapp.todolistapplication.feature.todo.presentation.list

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.androidapp.todolistapplication.core.designsysytem.AppSpacing
import com.androidapp.todolistapplication.core.designsysytem.TodoListApplicationTheme
import com.androidapp.todolistapplication.feature.todo.domain.model.SortDirection
import com.androidapp.todolistapplication.feature.todo.domain.model.Todo
import com.androidapp.todolistapplication.feature.todo.domain.model.TodoFilter
import com.androidapp.todolistapplication.feature.todo.domain.model.TodoSortBy
import com.androidapp.todolistapplication.feature.todo.presentation.list.component.AccountAvatar
import com.androidapp.todolistapplication.feature.todo.presentation.list.component.SwipeToDeleteBox
import com.androidapp.todolistapplication.feature.todo.presentation.list.component.TodoCard
import com.androidapp.todolistapplication.feature.todo.presentation.list.component.TodoListEmptyState
import com.androidapp.todolistapplication.feature.todo.presentation.list.component.TodoListErrorState
import com.androidapp.todolistapplication.feature.todo.presentation.list.component.TodoListLoadingState
import com.androidapp.todolistapplication.feature.todo.presentation.list.component.TodoListToolbar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListContent(
    uiState: TodoListUiState,
    onRetry: () -> Unit,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    onFilterChange: (TodoFilter) -> Unit,
    onSortChange: (TodoSortBy, SortDirection) -> Unit,
    onAccountClick: () -> Unit,
    onAddTodo: () -> Unit = {},
    onTodoClick: (Todo) -> Unit = {},
    onToggleComplete: (String) -> Unit = {},
    onDeleteTodo: (Todo) -> Unit = {},
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val listState = rememberLazyListState()

    val shouldLoadMore by remember(uiState.hasMore) {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: return@derivedStateOf false
            uiState.hasMore && lastVisible >= listState.layoutInfo.totalItemsCount - 3
        }
    }

    val firstTodoId = uiState.todos.firstOrNull()?.id
    LaunchedEffect(firstTodoId) {
        if (firstTodoId != null && listState.firstVisibleItemIndex <= 2) {
            listState.animateScrollToItem(0)
        }
    }

    LaunchedEffect(listState, uiState.hasMore) {
        snapshotFlow { shouldLoadMore }
            .collect { if (it) onLoadMore() }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            LargeTopAppBar(
                title = {
                    Column {
                        Text("My todos")
                        summaryText(uiState.todos)?.let { summary ->
                            Text(
                                text = summary,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = onAccountClick,
                        modifier = Modifier.semantics {
                            contentDescription = "Account"
                        }
                    ) {
                        AccountAvatar(email = uiState.userEmail)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                ),
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddTodo
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add todo"
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TodoListToolbar(
                filter = uiState.filter,
                sortBy = uiState.sortBy,
                direction = uiState.direction,
                onFilterChange = onFilterChange,
                onSortChange = onSortChange
            )

            PullToRefreshBox(
                isRefreshing = uiState.isLoading && uiState.todos.isNotEmpty(),
                onRefresh = onRefresh,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                when {
                    uiState.isLoading && uiState.todos.isEmpty() -> TodoListLoadingState()

                    uiState.errorMessage != null && uiState.todos.isEmpty() ->
                        TodoListErrorState(
                            message = uiState.errorMessage,
                            onRetry = onRetry
                        )

                    uiState.todos.isEmpty() -> TodoListEmptyState(filter = uiState.filter)

                    else -> LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = AppSpacing.screenHorizontal,
                            end = AppSpacing.screenHorizontal,
                            top = AppSpacing.small,
                            bottom = 88.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            uiState.todos,
                            key = { it.id }
                        ) { todo ->
                            SwipeToDeleteBox(
                                onDelete = { onDeleteTodo(todo) },
                                modifier = Modifier.animateItem()
                            ) {
                                TodoCard(
                                    todo = todo,
                                    onClick = { onTodoClick(todo) },
                                    onToggleComplete = { onToggleComplete(todo.id) }
                                )
                            }
                        }

                        if (uiState.isLoadingMore) {
                            item("loading-more") {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(AppSpacing.medium),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(28.dp),
                                        strokeWidth = 3.dp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun summaryText(todos: List<Todo>): String? {
    if (todos.isEmpty()) return null
    val done = todos.count { it.completed }
    val open = todos.size - done
    return when {
        done == 0 -> "$open open"
        open == 0 -> "$done done"
        else -> "$open open · $done done"
    }
}

@Composable
private fun previewContent(
    uiState: TodoListUiState
) {
    TodoListApplicationTheme {
        TodoListContent(
            uiState = uiState,
            onRetry = {},
            onRefresh = {},
            onLoadMore = {},
            onFilterChange = {},
            onSortChange = { _, _ -> },
            onAccountClick = {}
        )
    }
}

@Preview(name = "List", showBackground = true)
@Composable
private fun TodoListContentPreview() {
    previewContent(
        TodoListUiState(
            todos = SampleTodos.list,
            userEmail = "chhay@example.com"
        )
    )
}

@Preview(name = "List - dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TodoListContentDarkPreview() {
    previewContent(
        TodoListUiState(
            todos = SampleTodos.list,
            userEmail = "chhay@example.com")
    )
}

@Preview(name = "List - loading more", showBackground = true)
@Composable
private fun TodoListLoadingMorePreview() {
    previewContent(
        TodoListUiState(
            todos = SampleTodos.list,
            isLoadingMore = true,
            hasMore = true
        )
    )
}

@Preview(name = "Empty", showBackground = true)
@Composable
private fun TodoListEmptyPreview() {
    previewContent(TodoListUiState(userEmail = "chhay@example.com"))
}

@Preview(name = "Empty - completed filter", showBackground = true)
@Composable
private fun TodoListEmptyFilteredPreview() {
    previewContent(TodoListUiState(filter = TodoFilter.COMPLETED))
}

@Preview(name = "Error", showBackground = true)
@Composable
private fun TodoListErrorPreview() {
    previewContent(TodoListUiState(errorMessage = "No internet connection. Please check your network"))
}

@Preview(name = "Loading", showBackground = true)
@Composable
private fun TodoListLoadingPreview() {
    previewContent(TodoListUiState(isLoading = true))
}
