# Testing Notes

Reference notes for the tests in this project. The **auth** feature is fully tested and meant to be
copied from. The **todo** feature has no tests on purpose. It's your practice area (see
[Practice: the todo feature](#practice-the-todo-feature)).

---

## 1. The four kinds of tests here

| Kind | What it checks | Folder | Runs on | Speed |
|---|---|---|---|---|
| **Logic** | Pure rules: validation, mappers, error mapping, use cases | `src/test` | Your computer (JVM) | ms |
| **ViewModel** | State changes after user actions and backend answers | `src/test` | Your computer (JVM) | ms |
| **Compose UI** | What a screen shows for a given state, and that taps call the right callback | `src/androidTest` | Emulator / device | seconds |
| **Navigation** | Real screens + real ViewModels + real `AppNavDisplay`, with fake repositories | `src/androidTest` | Emulator / device | seconds |

Write lots of the fast ones and a few of the slow ones. Most bugs can be caught by a Logic or ViewModel test.

### Reference files

```
app/src/test/java/com/androidapp/todolistapplication/
├── testutil/MainDispatcherRule.kt                   ← coroutine setup for ViewModel tests
├── core/network/NetworkErrorMapperTest.kt           ← Logic
├── feature/auth/
│   ├── fake/FakeAuthRepository.kt                   ← the fake every auth test uses
│   ├── data/remote/mapper/AuthMapperTest.kt         ← Logic (simplest file, start here)
│   ├── domain/usecase/ConfirmPasswordResetUseCaseTest.kt  ← Logic (use case)
│   └── presentation/
│       ├── forgotpassword/ForgotPasswordUiStateTest.kt    ← Logic
│       ├── forgotpassword/ForgotPasswordViewModelTest.kt  ← ViewModel
│       ├── otpverify/OtpVerifyUiStateTest.kt              ← Logic
│       ├── otpverify/OtpVerifyViewModelTest.kt            ← ViewModel (assisted injection)
│       ├── resetpassword/ResetPasswordUiStateTest.kt      ← Logic
│       ├── resetpassword/ResetPasswordViewModelTest.kt    ← ViewModel (assisted injection)
│       └── login/LoginViewModelTest.kt                    ← ViewModel

app/src/androidTest/java/com/androidapp/todolistapplication/
├── HiltTestRunner.kt                                ← makes Hilt tests possible
├── di/TestRepositoryModule.kt                       ← swaps real repositories for fakes
├── fake/FakeAuthRepository.kt, FakeTodoRepository.kt
├── feature/auth/presentation/
│   ├── forgotpassword/ForgotPasswordContentTest.kt  ← Compose UI
│   ├── otpverify/OtpVerifyContentTest.kt            ← Compose UI
│   └── resetpassword/ResetPasswordContentTest.kt    ← Compose UI
└── navigation/
    ├── PasswordResetNavigationTest.kt               ← Navigation
    └── SessionStartNavigationTest.kt                ← Navigation (start screen from saved session)

app/src/debug/.../HiltTestActivity.kt                ← empty activity used by Navigation tests
```

---

## 2. How to run them

Use the **pro** flavor. It's the one wired to your local backend, and tests are set up for it.

```bash
# Logic + ViewModel tests (no emulator needed)
./gradlew :app:testProDebugUnitTest

# Just one class
./gradlew :app:testProDebugUnitTest --tests "*OtpVerifyViewModelTest"

# Compose UI + Navigation tests (start the emulator first)
./gradlew :app:connectedProDebugAndroidTest

# Just one instrumented class
./gradlew :app:connectedProDebugAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.class=com.androidapp.todolistapplication.navigation.PasswordResetNavigationTest
```

Or in Android Studio: click the green ▶ next to any test class or function. Make sure the
**Build Variant** panel says `proDebug`.

HTML reports (open in a browser):
- `app/build/reports/tests/testProDebugUnitTest/index.html`
- `app/build/reports/androidTests/connected/debug/flavors/pro/index.html`

> ⚠️ `connectedProDebugAndroidTest` **uninstalls the app** from the emulator when it finishes.
> Run `./gradlew :app:installProDebug` afterwards to get it back.

---

## 3. Every test has the same shape: Arrange → Act → Assert

```kotlin
@Test
fun `failure shows the backend message and does not navigate`() = runTest {
    // Arrange: set up the world
    repository.requestPasswordResetResult = AppResult.Error(AppError.TooManyRequests("Slow down"))
    viewModel.onEmailChange("me@example.com")

    // Act: do ONE thing the user would do
    viewModel.onSendCodeClick()
    advanceUntilIdle()

    // Assert: check the result
    val state = viewModel.uiState.value
    assertEquals("Slow down", state.errorMessage)
    assertFalse(state.isRequestSuccess)
}
```

Rules of thumb:
- **One behavior per test.** If the name needs "and", think about splitting it.
- **The name is a sentence** describing the behavior, not the method: `resend works before any code is typed`
  beats `testOnResendClick`.
- **Names with spaces in backticks only work in `src/test`.** Android can't run method names with spaces on
  API < 30 (our minSdk is 29), so `src/androidTest` uses `underscores_like_this`.

---

## 4. Logic tests

The easiest kind: build an object, check a value. No coroutines, no Android.

```kotlin
class ResetPasswordUiStateTest {
    // A state that passes every rule; each test breaks exactly ONE thing.
    private val valid = ResetPasswordUiState(email = "me@example.com", otp = "123456",
        newPassword = "Abcdefg1", confirmPassword = "Abcdefg1")

    @Test
    fun `password without a digit is not strong`() {
        assertFalse(valid.copy(newPassword = "Abcdefgh", confirmPassword = "Abcdefgh").isFormValid)
    }
}
```

The "valid baseline + `copy()` one change" trick keeps each test focused. See it in `ResetPasswordUiStateTest`.

For lists of bad inputs, loop and **put the input in the failure message**, so you know which one broke:

```kotlin
listOf("not-an-email", "me@", "@example.com").forEach { email ->
    assertFalse("expected '$email' to be invalid", ForgotPasswordUiState(email = email).isFormValid)
}
```

`NetworkErrorMapperTest` shows how to build a real Retrofit `HttpException` without a server:

```kotlin
HttpException(Response.error<Any>(400, """{"message":"Invalid or expired OTP"}""".toResponseBody("application/json".toMediaType())))
```

---

## 5. ViewModel tests

### 5.1 Fakes, not mocks

A **fake** is a small, real implementation of an interface that you control. `FakeAuthRepository`
follows a simple pattern:

```kotlin
class FakeAuthRepository : AuthRepository {
    var requestPasswordResetResult: AppResult<Unit> = AppResult.Success(Unit)   // ← what the "backend" answers
    val requestPasswordResetCalls = mutableListOf<String>()                     // ← what your code sent
    var delayMillis: Long = 0                                                   // ← keep calls "in flight"

    override suspend fun requestPasswordReset(email: String): AppResult<Unit> {
        requestPasswordResetCalls += email
        return respond(requestPasswordResetResult)
    }
}
```

- **Results in**: set a `*Result` to simulate success, a 400, no internet…
- **Calls out**: assert on the `*Calls` lists to prove what was (or wasn't) sent.

Fakes need no library and read like normal Kotlin. Mocking libraries (MockK, Mockito) are worth learning later.

We fake the **repository** (the interface) and use the **real use case** on top of it, so each
ViewModel test also covers the use case wiring for free.

### 5.2 Coroutines: the part that trips everyone up

ViewModels launch coroutines in `viewModelScope`, which runs on `Dispatchers.Main`. There is no Main
thread in a JVM test, so we swap it:

```kotlin
@get:Rule
val mainDispatcherRule = MainDispatcherRule()   // sets Dispatchers.Main = StandardTestDispatcher
```

Our use cases also switch to `Dispatchers.IO` (in `BaseUseCase`). If that happened in a test, the work
would run on a **real background thread** the test can't see. `advanceUntilIdle()` would return too early,
and tests would pass or fail at random. So use cases take the dispatcher as a constructor argument:

```kotlin
// Production: Hilt passes Dispatchers.IO via @IoDispatcher (see di/DispatcherModule.kt)
class RequestPasswordResetUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    @IoDispatcher ioDispatcher: CoroutineDispatcher
) : BaseUseCase<...>(ioDispatcher)

// Test: pass the test dispatcher, so everything shares ONE virtual clock
RequestPasswordResetUseCase(repository, mainDispatcherRule.testDispatcher)
```

This is called **dependency injection of dispatchers**, and it's the standard Android advice.

### 5.3 `runTest`, `runCurrent`, `advanceUntilIdle`

With `StandardTestDispatcher`, launched coroutines **don't run until you tell them to**:

| Call | Meaning |
|---|---|
| `runTest { }` | Wrap every test that touches coroutines. Uses virtual time: `delay(1_000)` takes 0 real ms. |
| `runCurrent()` | Run everything that's ready **now**, but don't skip ahead in time. |
| `advanceUntilIdle()` | Run everything, skipping all delays, until nothing is left. |

That lets you catch the **middle** of an operation, e.g. the loading state:

```kotlin
repository.delayMillis = 1_000          // fake backend takes 1 "second"
viewModel.onSendCodeClick()
runCurrent()                            // coroutine started, now waiting inside the delay
assertTrue(viewModel.uiState.value.isLoading)

advanceUntilIdle()                      // skip the delay, finish
assertFalse(viewModel.uiState.value.isLoading)
```

Same trick for **double-tap** tests: tap, `runCurrent()`, tap again, then check only one call was sent.

### 5.4 ViewModels with assisted injection

`OtpVerifyViewModel` and `ResetPasswordViewModel` get `email`/`otp` through `@Assisted`. In a test
that's just a normal constructor argument, so no Hilt is needed:

```kotlin
viewModel = OtpVerifyViewModel(
    requestPasswordResetUseCase = RequestPasswordResetUseCase(repository, mainDispatcherRule.testDispatcher),
    email = "me@example.com"
)
```

---

## 6. Compose UI tests

### 6.1 Test the `*Content`, not the `*Screen`

Every auth screen is split in two:

- `ForgotPasswordScreen`: gets the ViewModel, collects state, runs `LaunchedEffect`s. **Hard to test.**
- `ForgotPasswordScreenContent`: takes a `UiState` + callbacks, just draws. **Easy to test.**

So UI tests render the Content with a hand-made state:

```kotlin
@get:Rule
val composeRule = createComposeRule()

@Test
fun invalidEmail_showsErrorAndDisablesButton() {
    composeRule.setContent {
        ForgotPasswordScreenContent(
            uiState = ForgotPasswordUiState(email = "not-an-email"),
            onEmailChange = {}, onSendCodeClick = {}, onBackToLoginClick = {}
        )
    }

    composeRule.onNodeWithText("Enter a valid email address").assertIsDisplayed()
    composeRule.onNodeWithText("Send code").assertIsNotEnabled()
}
```

To check a callback fired, count it in a variable (`sendClicks++`) and assert on it afterwards.

### 6.2 Finders, actions, assertions cheat sheet

| Finders | Actions | Assertions |
|---|---|---|
| `onNodeWithText("Continue")` | `performClick()` | `assertIsDisplayed()` |
| `onNodeWithContentDescription("Back")` | `performTextInput("me")` | `assertExists()` / `assertDoesNotExist()` |
| `onNode(hasSetTextAction() and hasText("Email"))` | `performTextClearance()` | `assertIsEnabled()` / `assertIsNotEnabled()` |
| `onAllNodesWithText("x")[0]` | `performScrollTo()` | `assertTextEquals("…")` |

Things that surprised us:
- **`onNodeWithText("Send code").assertIsNotEnabled()` works** even though the text is *inside* the
  button. Buttons merge their children into one node, so the finder returns the button itself.
- **Finding a text field:** its label is also text, so `onNodeWithText("Email")` can be ambiguous.
  `hasSetTextAction()` means "an editable field", which makes it precise.
- **Icons need a `contentDescription`** to be findable. Decorative icons with `null` can't be clicked
  in tests, and screen readers can't see them either.
- When `AuthPrimaryButton` is loading, its label is replaced by a spinner, so
  `onNodeWithText("Send code").assertDoesNotExist()` is how we check loading.

---

## 7. Navigation tests (Hilt)

These run the **real app wiring** with only the repositories faked. Setup (already done):

1. **`HiltTestRunner`**: runs tests in `HiltTestApplication` (set as `testInstrumentationRunner` in
   `app/build.gradle.kts`).
2. **`TestRepositoryModule`** with `@TestInstallIn(replaces = [RepositoryModule::class])`: every
   `@HiltAndroidTest` gets fakes instead of real repositories. It also provides `FakeAuthRepository` by
   its own type, so a test can `@Inject` it and set results.
3. **`HiltTestActivity`** in `src/debug`: an empty `@AndroidEntryPoint` activity. We don't use
   `MainActivity` because it pops a permission dialog on Android 17.

A test looks like:

```kotlin
@HiltAndroidTest
class PasswordResetNavigationTest {
    @get:Rule(order = 0) val hiltRule = HiltAndroidRule(this)                       // Hilt FIRST
    @get:Rule(order = 1) val composeRule = createAndroidComposeRule<HiltTestActivity>()

    @Inject lateinit var authRepository: FakeAuthRepository

    @Before fun setUp() {
        hiltRule.inject()
        composeRule.setContent { AppNavDisplay(startRoute = LoginRoute, isLoggedIn = false) }
    }
}
```

**Wait for screens, don't assume.** Here use cases really do run on `Dispatchers.IO`, and Compose
doesn't know to wait for that. So after an action that calls the "backend", wait for the next screen:

```kotlin
composeRule.onNodeWithText("Send code").performClick()
composeRule.waitUntilAtLeastOneExists(hasText("We sent a 6-digit code to me@example.com"), timeoutMillis = 5_000)
```

---

## 8. Make sure your test can fail

A test that passes no matter what is worse than no test. After writing one, **break the code on
purpose** and check the test goes red. Then undo the break.

We did this for the reference tests:
- Put back the old `if (!isOtpValid) return` guard in `OtpVerifyViewModel.onResendClick` → 4 tests fail.
- Removed `viewModel.consumeRequestSuccess()` from `ForgotPasswordScreen` →
  `backFromOtp_returnsToForgotPassword_withoutBouncingForward` fails.

---

## 9. Common errors

| Error | Cause | Fix |
|---|---|---|
| `Module with the Main dispatcher had failed to initialize` | ViewModel test without the rule | Add `@get:Rule val mainDispatcherRule = MainDispatcherRule()` |
| Test passes/fails randomly; state is still loading | Use case running on real `Dispatchers.IO` | Pass `mainDispatcherRule.testDispatcher` into the use case |
| `Method d in android.util.Log not mocked` | JVM tests have no real Android classes | Remove the `Log` call, or add `testOptions { unitTests.isReturnDefaultValues = true }` |
| `Expected exactly '1' node but found '2'` | Finder matches more than one node (e.g. label + field) | Narrow it: `hasSetTextAction() and hasText(...)`, or `onAllNodes…[0]` |
| `ComposeTimeoutException … still not satisfied after 5000 ms` | Screen never appeared: navigation didn't happen, or the text is different | Read the text in the message exactly; check the fake's result |
| Hilt `IllegalStateException` mentioning `HiltAndroidRule`, or an `@Inject lateinit` field not initialized | Missing `HiltAndroidRule`, wrong rule order, or forgot `hiltRule.inject()` | See section 7; the Hilt rule must be `order = 0` |
| App vanished from emulator | `connected…AndroidTest` uninstalls it | `./gradlew :app:installProDebug` |

---

## Practice: the todo feature

Nothing in `feature/todo` has tests. Work through the levels in order. Each one builds on the
last, and each has a matching reference file in auth to copy from.

The todo code has **real bugs** that these tests will uncover. Try writing the test from how the code
*should* behave before opening the hints.

### Level 1: Logic: `TodoMapperTest` and `TodoPageTest`

📄 Copy from: `AuthMapperTest`, `ResetPasswordUiStateTest`
📁 Create: `src/test/.../feature/todo/data/remote/mapper/TodoMapperTest.kt`, `src/test/.../feature/todo/domain/model/TodoPageTest.kt`

Test ideas:
- A full `TodoResponseDto` maps every field correctly (use different values for `createdAt` and `updatedAt`!)
- Unknown priority `"URGENT"` falls back to `MEDIUM`
- Bad or null `dueDate` becomes `null`; bad `createdAt` becomes `Instant.EPOCH`
- `TodoPage.hasMore` is true on page 0 of 3, false on the last page, false when `totalPages = 0`

<details>
<summary>💡 Hint: a bug you should find</summary>

Look closely at the line that sets `updatedAt` in `TodoMapper.kt`. Which field does it parse?
</details>

### Level 2: Make the todo use cases testable, then test them

📄 Copy from: the auth use cases + `ConfirmPasswordResetUseCaseTest`

1. Add `@IoDispatcher ioDispatcher: CoroutineDispatcher` to `GetTodosUseCase` and `CreateTodoUseCase`
   and pass it to `BaseUseCase(ioDispatcher)`, exactly like `RequestPasswordResetUseCase`.
   Build the app to check Hilt is still happy.
2. Write `src/test/.../feature/todo/fake/FakeTodoRepository.kt` in the same "results in, calls out" style as `FakeAuthRepository`.
3. Write `GetTodosUseCaseTest` and `CreateTodoUseCaseTest`: params pass through, errors come back
   unchanged, an exception becomes `AppError.Unknown`.

### Level 3: ViewModel: `TodoListViewModelTest`

📄 Copy from: `ForgotPasswordViewModelTest`, `OtpVerifyViewModelTest`

`TodoListViewModel` needs **three** use cases. Two are auth ones, so you can reuse `FakeAuthRepository`
for those and your new `FakeTodoRepository` for the third.

Test ideas:
- On creation it loads page 0 and shows the todos
- On creation it loads the current user's email
- A failed load shows the error message and stops loading
- `loadTodo(resetPage = false)` **appends** the next page instead of replacing
- `onLogoutClick` success sets `isLoggedOut`; failure shows the error
- Double-tapping logout only logs out once

<details>
<summary>💡 Hint: two things will block you at first</summary>

1. **`init` runs in the constructor.** Configure your fakes *before* you create the ViewModel. So
   create it inside each test (or a helper function), not in `@Before`.
2. **`Log.d` in `loadCurrentUser`** crashes JVM tests. See section 9 for two fixes. Which one is the better idea, and why?
</details>

<details>
<summary>💡 Hint: bugs your tests may expose</summary>

- Does `TodoRepositoryImpl.getTodos(page)` actually *use* `page`? Check `TodoApiService.getTodos()` too.
- What stops `loadTodo(resetPage = false)` from loading past the last page, or twice at once?
  (Look for a check on `hasMore` or `isLoading`.)
</details>

### Level 4: Compose UI: `TodoItem` and the list screen

📄 Copy from: `ForgotPasswordContentTest`, `OtpVerifyContentTest`
📁 Create in: `src/androidTest/.../feature/todo/presentation/list/`

The split is already done for you here: `TodoListScreen` holds the ViewModel, `TodoListContent`
is stateless, and the pieces live in `component/` (`TodoCard`, `TodoListStates`, `TodoListToolbar`).

1. **Start with `TodoCard`**: it takes a `Todo` and an optional `onClick`. Check it shows the title,
   the description only when present, the priority word (`High`/`Medium`/`Low`), "Today"/"Tomorrow"
   for due dates, "Overdue · …" in the error colour for a past date, and that a completed todo shows
   the "Completed" icon. Tapping it should call `onClick`.
2. **Then `TodoListContent`**, driving it with hand-made `TodoListUiState` values: skeletons while
   `isLoading` with no todos, "Nothing to do yet" when empty, a different empty message per filter,
   "Retry" on error (and that tapping it calls back), the footer spinner when `isLoadingMore`, and
   that tapping the "Active" chip calls `onFilterChange(TodoFilter.ACTIVE)`.
3. **Bonus**: `DueDateFormatter` is pure Kotlin and takes a `Clock`, so you can unit test it in
   `src/test` with a fixed clock — no emulator. Try `Clock.fixed(...)` and check every branch.

### Level 5: Navigation: login → todos → logout

📄 Copy from: `PasswordResetNavigationTest`

1. Grow `src/androidTest/.../fake/FakeTodoRepository.kt` so a test can set the todos it returns.
2. Write `LoginNavigationTest`: type email + password → Login → wait for "My todos" → see a todo title
   → open "Account" → Log out → confirm → back on "Welcome Back".
   (Heads-up: once the confirm dialog opens there are **two** "Log out" nodes on screen.)

### Bonus: `RegisterViewModelTest` + `RegisterUiStateTest`

📄 Copy from: `LoginViewModelTest`

<details>
<summary>💡 Hint: a bug you should find</summary>

`RegisterUiState` has a `passwordsMismatch` property. Does `isFormValid` use it? What happens if you
register with two different passwords?
</details>

---

### Checklist for every test you write

- [ ] Name describes a behavior a user or teammate would care about
- [ ] Arrange / Act / Assert are easy to spot
- [ ] Tests one thing
- [ ] Doesn't depend on other tests running first
- [ ] **You watched it fail** at least once (section 8)
