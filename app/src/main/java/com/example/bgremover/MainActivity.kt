package com.example.bgremover

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.example.bgremover.di.appModule
import com.example.bgremover.presentation.ui.navigation.Navigation
import com.example.bgremover.presentation.ui.navigation.Screens
import com.example.bgremover.presentation.ui.screens.BiometricPromptManager
import com.example.bgremover.ui.theme.BgRemoverTheme
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MainActivity : AppCompatActivity() {

    private val promptManager by lazy {
        BiometricPromptManager(this)
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        startKoin {
            androidContext(this@MainActivity)
            androidLogger()
            modules(appModule)
        }

        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { true }

        promptManager.showBiometricPrompt(
            title = "Fingerprint Authentication",
            description = "Authenticate to continue"
        )

        setContent {
            val biometricResult by promptManager.promptResult.collectAsState(initial = null)


            val navController = rememberNavController()

            LaunchedEffect(key1 = biometricResult) {
                when (biometricResult) {
                    is BiometricPromptManager.BiometricResult.AuthenticationSuccess -> {
                        splashScreen.setKeepOnScreenCondition { false }
                        navController.navigate(Screens.BgRemover.route) {
                            popUpTo(Screens.BgRemover.route) { inclusive = true }
                        }
                    }
                    is BiometricPromptManager.BiometricResult.AuthenticationError,
                    BiometricPromptManager.BiometricResult.AuthenticationFailed -> {
                        finish()
                    }
                    else -> Unit
                }
            }

            BgRemoverTheme {
                Navigation(navController)
            }
        }
    }
}





fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "Download Channel"
        val descriptionText = "Channel for download notifications"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel("DOWNLOAD_CHANNEL", name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}
