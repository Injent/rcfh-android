package ru.rcfh.feature.login.presentation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_EXPANDED_LOWER_BOUND
import org.koin.androidx.compose.koinViewModel
import ru.rcfh.designsystem.component.AppLargeButton
import ru.rcfh.designsystem.component.AppSecureTextField
import ru.rcfh.designsystem.component.AppTextField
import ru.rcfh.designsystem.theme.AppTheme
import ru.rcfh.designsystem.util.rememberImeState
import ru.rcfh.designsystem.util.thenIf
import ru.rcfh.feature.login.R
import ru.rcfh.feature.login.component.TopGoneLayout
import ru.rcfh.navigation.Screen

fun NavGraphBuilder.loginScreen() {
    composable<Screen.Login> { LoginRoute() }
}

@Composable
private fun LoginRoute() {
    val viewModel = koinViewModel<LoginViewModel>()

    LoginScreen(
        onLogin = viewModel::onLogin,
        loginState = viewModel.loginState,
        passwordState = viewModel.passwordState
    )
}

@Composable
private fun LoginScreen(
    onLogin: () -> Unit,
    loginState: TextFieldState,
    passwordState: TextFieldState
) {
    val isImeVisible by rememberImeState()
    val window = currentWindowAdaptiveInfo().windowSizeClass

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = {

        }
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
                    val alpha by animateFloatAsState(
                        targetValue = if (isImeVisible) 0f else 1f
                    )
                    Box(
                        contentAlignment = if (window.isHeightAtLeastBreakpoint(WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND)) {
                            Alignment.Center
                        } else Alignment.CenterStart,
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
                ) {
                    AppTextField(
                        state = loginState,
                        label = stringResource(R.string.label_login),
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    AppSecureTextField(
                        state = passwordState,
                        label = stringResource(R.string.label_password),
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    Spacer(Modifier.height(AppTheme.spacing.m))

                    AppLargeButton(
                        text = stringResource(R.string.button_login),
                        onClick = onLogin,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}