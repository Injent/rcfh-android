package ru.rcfh.blank.ui.style

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Build.VERSION.SDK_INT
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.core.location.LocationCompat
import androidx.core.location.altitude.AltitudeConverterCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import ru.rcfh.common.AppDispatchers

class LocationState(
    private val coroutineScope: CoroutineScope,
    private val dispatchers: AppDispatchers,
) {
    var latitude by mutableStateOf<Double?>(null)
    var longitude by mutableStateOf<Double?>(null)
    var msl by mutableStateOf<Int?>(null)
    var isReceiving by mutableStateOf(false)
        private set

    fun request(activity: ComponentActivity) {
        when {
            activity.hasAccessFineLocationPermission() -> activity.receiveLocation()
            else -> activity.requestLocationPermission()
        }
    }

    private fun ComponentActivity.requestLocationPermission() {
        var launcher: ActivityResultLauncher<String>? = null
        launcher = activityResultRegistry.register(
            "location_permission",
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            launcher?.unregister()
            if (isGranted) {
                receiveLocation()
            }
        }
        launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    @SuppressLint("MissingPermission")
    private fun Activity.receiveLocation() {
        if (isReceiving) return
        if (!hasAccessFineLocationPermission()) return
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val settingsClient = LocationServices.getSettingsClient(this)

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            5000
        ).build()
        val locationSettingsRequest = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .build()

        isReceiving = true
        settingsClient.checkLocationSettings(locationSettingsRequest)
            .addOnSuccessListener {
                fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    null
                )
                    .addOnSuccessListener { location ->
                        isReceiving = false
                        latitude = location?.latitude
                        longitude = location?.longitude

                        coroutineScope.launch(dispatchers.ioDispatcher) {
                            try {
                                AltitudeConverterCompat.addMslAltitudeToLocation(this@receiveLocation, location)
                                msl = if (SDK_INT >= 34) {
                                    location.mslAltitudeMeters
                                } else {
                                    location.extras?.getDouble(LocationCompat.EXTRA_MSL_ALTITUDE) ?: return@launch
                                }.toInt()
                            } catch (_: Exception) {
                            }
                        }
                    }
                    .addOnFailureListener {
                        isReceiving = false
                    }
            }
            .addOnFailureListener { e ->
                isReceiving = false
                if (e is ResolvableApiException) {
                    requestEnableLocation()
                }
            }
    }

    private fun Activity.requestEnableLocation() {
        val settingsClient = LocationServices.getSettingsClient(this)
        val locationReq = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            5000
        ).build()
        val locationSettingsReq = LocationSettingsRequest.Builder()
            .addLocationRequest(locationReq)
            .build()

        settingsClient.checkLocationSettings(locationSettingsReq)
            .addOnSuccessListener {
                isReceiving = false
                receiveLocation()
            }
            .addOnFailureListener { exception ->
                isReceiving = false
                if (exception is ResolvableApiException) {
                    try {
                        exception.startResolutionForResult(this, 0)
                    } catch (_: IntentSender.SendIntentException) {
                        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        this.startActivity(intent)
                    }
                } else {
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    this.startActivity(intent)
                }
            }
    }

    private fun Activity.hasAccessFineLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
}

@Composable
fun rememberLocationState(): LocationState {
    val coroutineScope = rememberCoroutineScope()
    val dispatchers = koinInject<AppDispatchers>()
    return remember {
        LocationState(
            coroutineScope = coroutineScope,
            dispatchers = dispatchers
        )
    }
}