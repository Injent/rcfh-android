package ru.rcfh.feature.login.presentation.signin

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_EXPANDED_LOWER_BOUND
import org.koin.androidx.compose.koinViewModel
import ru.rcfh.designsystem.component.AppLargeButton
import ru.rcfh.designsystem.component.AppSecureTextField
import ru.rcfh.designsystem.component.AppTextButton
import ru.rcfh.designsystem.component.AppTextField
import ru.rcfh.designsystem.icon.AppIcons
import ru.rcfh.designsystem.icon.Email
import ru.rcfh.designsystem.icon.Password
import ru.rcfh.designsystem.theme.AppTheme
import ru.rcfh.designsystem.util.rememberImeState
import ru.rcfh.designsystem.util.thenIf
import ru.rcfh.feature.login.R
import ru.rcfh.feature.login.component.TopGoneLayout
import ru.rcfh.navigation.Screen

fun NavGraphBuilder.signInScreen() {
    composable<Screen.SignIn>(
        enterTransition = {
            if (initialState.destination.route == Screen.Loading::class.qualifiedName) {
                fadeIn(animationSpec = tween(2000))
            } else null
        },
        exitTransition = {
            if (targetState.destination.route == Screen.SignUp::class.qualifiedName) {
                slideOutHorizontally(targetOffsetX = { -it })
            } else {
                null
            }
        }
    ) { SignInRoute() }
}

@Composable
private fun SignInRoute() {
    val viewModel = koinViewModel<SignInViewModel>()

    SignInScreen(
        onLogin = viewModel::onLogin,
        loginState = viewModel.loginState,
        passwordState = viewModel.passwordState
    )
}

@Composable
private fun SignInScreen(
    onLogin: () -> Unit,
    loginState: TextFieldState,
    passwordState: TextFieldState
) {
    val isImeVisible by rememberImeState()
    val window = currentWindowAdaptiveInfo().windowSizeClass

    Scaffold(
        contentWindowInsets = WindowInsets(0),
    ) { innerPadding ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .then(
                    with(Modifier) {
                        when {
                            window.isWidthAtLeastBreakpoint(WIDTH_DP_EXPANDED_LOWER_BOUND) -> {
                                systemBarsPadding().padding(vertical = AppTheme.spacing.l)
                            }

                            else -> background(AppTheme.colorScheme.background1).systemBarsPadding()
                        }
                    }
                )
        ) {
            TopGoneLayout(
                top = {
                    val showLogo = window.isHeightAtLeastBreakpoint(WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND)
                    val alpha by animateFloatAsState(
                        targetValue = if (isImeVisible) 0f else 1f
                    )
                    if (showLogo) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Image(
                                painter = painterResource(R.drawable.app_logo),
                                contentDescription = null,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .widthIn(max = 200.dp)
                                    .aspectRatio(1f)
                                    .alpha(alpha)
                            )
                        }
                    }
                },
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .thenIf(window.isWidthAtLeastBreakpoint(WIDTH_DP_EXPANDED_LOWER_BOUND)) {
                        background(
                            color = AppTheme.colorScheme.background1,
                            shape = AppTheme.shapes.large
                        )
                    }
                    .padding(
                        horizontal = AppTheme.spacing.xxxl,
                        vertical = AppTheme.spacing.xxl
                    )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.m),
                    modifier = Modifier
                        .focusGroup()
                ) {
                    AppTextField(
                        state = loginState,
                        label = stringResource(R.string.label_login),
                        onCard = true,
                        keyboardOptions = KeyboardOptions(
                            autoCorrectEnabled = false,
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        leadingIcon = {
                            Icon(
                                imageVector = AppIcons.Email,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(20.dp)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    AppSecureTextField(
                        state = passwordState,
                        label = stringResource(R.string.label_password),
                        onCard = true,
                        keyboardOptions = KeyboardOptions(
                            autoCorrectEnabled = false,
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Go
                        ),
                        onKeyboardAction = {
                            onLogin()
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = AppIcons.Password,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(20.dp)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        AppTextButton(
                            text = stringResource(R.string.button_noAccount),
                            onClick = {}
                        )
                    }

                    AppLargeButton(
                        text = stringResource(R.string.button_signIn),
                        onClick = onLogin,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}