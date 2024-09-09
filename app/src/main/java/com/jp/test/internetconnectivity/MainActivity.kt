package com.jp.test.internetconnectivity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import library.internet.connection.internetchecklibrary.helper.NetworkMonitor
import com.jp.test.internetconnectivity.ui.theme.InternetConnectivityTheme

class MainActivity : ComponentActivity() {

    private lateinit var networkMonitor: NetworkMonitor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        networkMonitor = NetworkMonitor(this)
        setContent {
            InternetConnectivityTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        modifier = Modifier.padding(innerPadding), networkMonitor
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        networkMonitor.registerCallback()
    }

    override fun onPause() {
        super.onPause()
        networkMonitor.unregisterCallback()
    }
}

@Composable
fun Greeting(modifier: Modifier = Modifier, networkMonitor: NetworkMonitor) {

    val isNetworkConnected = networkMonitor.isConnected.collectAsStateWithLifecycle()


    Text(
        text = if (isNetworkConnected.value) "Connected" else "Not Connected",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    /*InternetConnectivityTheme {
        Greeting("Android", networkMonitor)
    }*/
}