package library.internet.connection.internetchecklibrary.helper

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NetworkMonitor(context: Context) {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val _isConnected = MutableStateFlow(value = false)
    val isConnected = _isConnected.asStateFlow()

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            CoroutineScope(Dispatchers.Main).launch {
                println("onAvailable() ==>> Internet is connected")
                _isConnected.emit(true)
            }
        }

        override fun onLost(network: Network) {
            CoroutineScope(Dispatchers.Main).launch {
                println("onLost() ==>> Internet is not connected")
                _isConnected.emit(false)
            }
        }

        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            // Check if the network has Internet capability and is validated
            val hasInternet = connectivityManager.getNetworkCapabilities(network)?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) ?: false
            val isValidated = connectivityManager.getNetworkCapabilities(network)?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) ?: false



            // Check if the network is either Wi-Fi, Cellular, or Ethernet
            val isWifi = connectivityManager.getNetworkCapabilities(network)?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ?: false
            val isCellular = connectivityManager.getNetworkCapabilities(network)?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ?: false
            val isEthernet = connectivityManager.getNetworkCapabilities(network)?.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ?: false


            CoroutineScope(Dispatchers.Main).launch {
                println("onCapabilitiesChanged() ==>> ${hasInternet && isValidated && (isWifi || isCellular || isEthernet)}")
                _isConnected.emit(hasInternet && isValidated && (isWifi || isCellular || isEthernet))
            }
        }
    }

    fun registerCallback() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(networkCallback)
        } else {
            val request = NetworkRequest.Builder().apply {
                addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
            }.build()
            connectivityManager.registerNetworkCallback(request, networkCallback)
        }
    }

    fun unregisterCallback() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
}