package com.example.cardwallet

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.window.OnBackInvokedCallback
import android.window.OnBackInvokedDispatcher
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.example.cardwallet.ui.navigation.CardWalletNavGraph
import com.example.cardwallet.ui.theme.CardWalletTheme
import com.example.cardwallet.ui.theme.paletteByKey

class MainActivity : FragmentActivity() {
    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val settings = (application as CardWalletApplication).settingsRepository
        setContent {
            val navController = rememberNavController()
            val themeColor by settings.themeColor.collectAsStateWithLifecycle(initialValue = "purple")
            val dynamicColor by settings.dynamicColor.collectAsStateWithLifecycle(initialValue = false)
            val predictiveBack by settings.predictiveBack.collectAsStateWithLifecycle(initialValue = true)

            DisposableEffect(predictiveBack) {
                var callback: OnBackInvokedCallback? = null
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !predictiveBack) {
                    callback = OnBackInvokedCallback {
                        if (navController.previousBackStackEntry != null) {
                            navController.popBackStack()
                        } else {
                            this@MainActivity.finish()
                        }
                    }
                    onBackInvokedDispatcher.registerOnBackInvokedCallback(
                        OnBackInvokedDispatcher.PRIORITY_DEFAULT,
                        callback
                    )
                }
                onDispose {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        callback?.let { onBackInvokedDispatcher.unregisterOnBackInvokedCallback(it) }
                    }
                }
            }

            CardWalletTheme(
                palette = paletteByKey(themeColor),
                dynamicColor = dynamicColor
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CardWalletNavGraph(navController = navController)
                }
            }
        }
    }
}