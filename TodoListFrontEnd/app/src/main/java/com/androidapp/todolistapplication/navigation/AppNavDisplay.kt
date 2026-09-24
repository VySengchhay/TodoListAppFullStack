package com.androidapp.todolistapplication.navigation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.androidapp.todolistapplication.feature.auth.presentation.forgotpassword.ForgotPasswordScreen
import com.androidapp.todolistapplication.feature.auth.presentation.login.LoginScreen
import com.androidapp.todolistapplication.feature.auth.presentation.otpverify.OtpVerifyScreen
import com.androidapp.todolistapplication.feature.auth.presentation.register.RegisterScreen
import com.androidapp.todolistapplication.feature.auth.presentation.resetpassword.ResetPasswordScreen
import com.androidapp.todolistapplication.feature.todo.presentation.add.AddTodoScreen
import com.androidapp.todolistapplication.feature.todo.presentation.edit.EditTodoScreen
import com.androidapp.todolistapplication.feature.todo.presentation.list.TodoListScreen

@Composable
fun AppNavDisplay(
    startRoute: NavKey,
    isLoggedIn: Boolean,
    modifier: Modifier = Modifier
) {
    val backStack = rememberNavBackStack(startRoute)

    fun resetTo(route: NavKey) {
        backStack.clear()
        backStack.add(route)
    }

    LaunchedEffect(isLoggedIn) {
        if (!isLoggedIn && backStack.lastOrNull() == TodoListRoute) {
            resetTo(LoginRoute)
        }
    }

    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        onBack = { backStack.removeLastOrNull() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<RegisterRoute> {
                RegisterScreen(
                    onRegisterSuccess = { resetTo(LoginRoute) },
                    onNavigateToLogin = { resetTo(LoginRoute) }
                )
            }

            entry<LoginRoute> {
                LoginScreen(
                    onLoginSuccess = { resetTo(TodoListRoute) },
                    onNavigateToRegister = { resetTo(RegisterRoute) },
                    onNavigateToForgotPassword = { backStack.add(ForgotPasswordRoute) }
                )
            }

            entry<ForgotPasswordRoute> {
                ForgotPasswordScreen(
                    onCodeSent = { email -> backStack.add(OtpVerifyRoute(email)) },
                    onNavigateBack = { backStack.removeLastOrNull() }
                )
            }

            entry<OtpVerifyRoute> { key ->
                OtpVerifyScreen(
                    email = key.email,
                    onOtpEntered = { otp -> backStack.add(ResetPasswordRoute(key.email, otp)) },
                    onNavigateBack = { backStack.removeLastOrNull() }
                )
            }

            entry<ResetPasswordRoute> { key ->
                ResetPasswordScreen(
                    email = key.email,
                    otp = key.otp,
                    onResetSuccess = { resetTo(LoginRoute) },
                    onNavigateBack = { backStack.removeLastOrNull() }
                )
            }

            entry<TodoListRoute> {
                TodoListScreen(
                    onAddTodo = { backStack.add(AddTodoRoute) },
                    onEditTodo = { todo -> backStack.add(EditTodoRoute(todo.id)) },
                    onLogoutComplete = { resetTo(LoginRoute) }
                )
            }

            entry<EditTodoRoute> { key ->
                EditTodoScreen(
                    todoId = key.todoId,
                    // The list reloads when it returns to the foreground, so both
                    // an edit and a delete are reflected on the way back.
                    onTodoSaved = { backStack.removeLastOrNull() },
                    onTodoDeleted = { backStack.removeLastOrNull() },
                    onNavigateBack = { backStack.removeLastOrNull() }
                )
            }

            entry<AddTodoRoute> {
                AddTodoScreen(
                    // The list reloads when it comes back to the foreground,
                    // so the new todo is there on return.
                    onTodoCreated = { backStack.removeLastOrNull() },
                    onNavigateBack = { backStack.removeLastOrNull() }
                )
            }
        }
    )
}
