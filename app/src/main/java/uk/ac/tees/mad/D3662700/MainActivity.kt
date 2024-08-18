package uk.ac.tees.mad.D3662700

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import uk.ac.tees.mad.D3662700.screens.IntroScreen
import uk.ac.tees.mad.D3662700.screens.LoginScreen
import uk.ac.tees.mad.D3662700.screens.RegisterScreen
import uk.ac.tees.mad.D3662700.ui.theme.BookMyShowTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BookMyShowTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "intro") {
                        composable("intro") {
                            IntroScreen(onGetStartedClick = {
                                navController.navigate("login")
                            })
                        }
                        composable("login") {
                            LoginScreen(
                                onLoginClick = {
                                    // Handle login
                                },
                                onRegisterClick = {
                                    navController.navigate("register")
                                }
                            )
                        }
                        composable("register") {
                            RegisterScreen(
                                onRegisterClick = {
                                    // Handle registration
                                },
                                onLoginClick = {
                                    navController.navigate("login")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}