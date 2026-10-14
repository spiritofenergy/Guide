package com.kodex.guide

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.kodex.guide.presentation.navigation.NavigationNavGraph
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

  //  private val destroyScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val viewModel: MainActivityViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            NavigationNavGraph(
                navController = navController,
                onExitApp = { finish() }
            )

        }
    }

    override fun onStop() {
        super.onStop()
        lifecycleScope.launch {
            viewModel.updateLastVisit()
        }
    }
}

