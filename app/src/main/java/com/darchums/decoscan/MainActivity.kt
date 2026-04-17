package com.darchums.decoscan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.darchums.decoscan.ui.navigation.Screen
import com.darchums.decoscan.ui.screens.*
import com.darchums.decoscan.ui.theme.DecoScanTheme
import com.darchums.decoscan.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DecoScanTheme {
                val navController = rememberNavController()
                val mainViewModel: MainViewModel = viewModel()
                val isOnboardingCompleted = mainViewModel.isOnboardingCompleted.collectAsState()
                val loggedInUser = mainViewModel.loggedInUser.collectAsState()

                NavHost(
                    navController = navController,
                    startDestination = Screen.Splash.route
                ) {
                    composable(Screen.Splash.route) {
                        SplashScreen(
                            onAnimationComplete = {
                                val nextRoute = if (!isOnboardingCompleted.value) {
                                    Screen.Onboarding.route
                                } else if (loggedInUser.value == null) {
                                    Screen.Login.route
                                } else {
                                    Screen.Home.route
                                }
                                navController.navigate(nextRoute) {
                                    popUpTo(Screen.Splash.route) { inclusive = true }
                                }
                            }
                        )
                    }

                    composable(Screen.Onboarding.route) {
                        OnboardingScreen(
                            onFinished = {
                                mainViewModel.completeOnboarding()
                                navController.navigate(Screen.Register.route) {
                                    popUpTo(Screen.Onboarding.route) { inclusive = true }
                                }
                            }
                        )
                    }

                    composable(Screen.Login.route) {
                        LoginScreen(
                            onLoginSuccess = {
                                navController.navigate(Screen.Home.route) {
                                    popUpTo(Screen.Login.route) { inclusive = true }
                                }
                            },
                            onNavigateToRegister = {
                                navController.navigate(Screen.Register.route)
                            }
                        )
                    }

                    composable(Screen.Register.route) {
                        RegisterScreen(
                            onRegisterSuccess = {
                                navController.navigate(Screen.Home.route) {
                                    popUpTo(Screen.Register.route) { inclusive = true }
                                }
                            },
                            onNavigateToLogin = {
                                navController.navigate(Screen.Login.route)
                            }
                        )
                    }

                    composable(Screen.Home.route) {
                        HomeScreen(
                            navController = navController,
                            onLogout = {
                                mainViewModel.logout()
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(Screen.Home.route) { inclusive = true }
                                }
                            }
                        )
                    }

                    composable(Screen.Camera.route) {
                        CameraScreen(
                            onImageCaptured = { material, confidence ->
                                navController.navigate(Screen.Result.createRoute(material, confidence)) {
                                    popUpTo(Screen.Camera.route) { inclusive = true }
                                }
                            },
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.Result.route) { backStackEntry ->
                        val material = backStackEntry.arguments?.getString("material") ?: "Unknown"
                        val confidence = backStackEntry.arguments?.getString("confidence")?.toFloatOrNull() ?: 0f
                        ResultScreen(
                            material = material,
                            confidence = confidence,
                            onClose = { navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Home.route) { inclusive = true }
                            }}
                        )
                    }

                    composable(Screen.Profile.route) {
                        ProfileScreen(onBack = { navController.popBackStack() })
                    }

                    composable(Screen.EcoTips.route) {
                        EcoTipsScreen(onBack = { navController.popBackStack() })
                    }
                }
            }
        }
    }
}
