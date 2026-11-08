package com.example.cardwallet

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cardwallet.ui.navigation.CardWalletNavGraph
import com.example.cardwallet.ui.theme.CardWalletTheme
import com.example.cardwallet.ui.theme.paletteByKey

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val settings = (application as CardWalletApplication).settingsRepository
        setContent {
            val themeColor by settings.themeColor.collectAsStateWithLifecycle(initialValue = "purple")
            val dynamicColor by settings.dynamicColor.collectAsStateWithLifecycle(initialValue = false)
            CardWalletTheme(
                palette = paletteByKey(themeColor),
                dynamicColor = dynamicColor
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CardWalletNavGraph()
                }
            }
        }
    }
}