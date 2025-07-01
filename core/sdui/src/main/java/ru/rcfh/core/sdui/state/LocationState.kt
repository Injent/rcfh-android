package ru.rcfh.core.sdui.state

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.buildJsonObject
import ru.rcfh.core.sdui.common.DetectedError
import java.util.Locale

@Stable
class LocationState(
    override val id: String,
    val lat: TextState,
    val lon: TextState,
    documentState: DocumentState,
) : FieldState(documentState) {
    var isReceiving by mutableStateOf(false)
        private set

    fun request(activity: ComponentActivity) {
        when {
            activity.hasAccessFineLocationPermission() -> activity.receiveLocation()
            else -> activity.requestLocationPermission()
        }
    }

    override fun isValid() = lat.isValid() && lon.isValid()

    private fun ComponentActivity.requestLocationPermission() {
        var launcher: ActivityResultLauncher<String>? = null
        launcher = activityResultRegistry.register(
            "location_permission_$id",
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
                        lat.value = location?.latitude?.round(6)?.toString() ?: ""
                        lon.value = location?.longitude?.round(6)?.toString() ?: ""
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

    override fun detectErrors(): List<DetectedError> = emptyList()

    override fun save(): JsonElement {
        return buildJsonObject {
            put(lat.id, lat.save())
            put(lon.id, lon.save())
        }
    }

    private fun Activity.hasAccessFineLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
}

private fun Double.round(places: Int): Float {
    return String.format(Locale.US, "%.${places}f", this).toFloat()
}