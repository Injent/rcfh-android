package ru.rcfh.feature.forms.state

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import kotlinx.coroutines.suspendCancellableCoroutine
import ru.rcfh.designsystem.util.unwrap
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

data class Location(val latitude: Double, val longitude: Double)

class LocationReceiver(
    private val context: Context,
    private val onRequestPermission: (String) -> Unit
) {
    private val settingsClient by lazy { LocationServices.getSettingsClient(context) }
    private val fusedLocationClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    suspend fun receiveLocation(): Location? {
        if (
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            onRequestPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            return null
        }

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            5000
        ).build()
        val locationSettingsRequest = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .build()

        return try {
            suspendCancellableCoroutine { continuation ->
                settingsClient.checkLocationSettings(locationSettingsRequest)
                    .addOnSuccessListener {
                        fusedLocationClient.getCurrentLocation(
                            Priority.PRIORITY_HIGH_ACCURACY,
                            null
                        )
                            .addOnSuccessListener { locationResult ->
                                if (locationResult != null) {
                                    continuation.resume(
                                        Location(locationResult.latitude, locationResult.longitude)
                                    )
                                } else {
                                    continuation.resume(null)
                                }
                            }
                            .addOnFailureListener { exception ->
                                continuation.resumeWithException(exception)
                            }
                    }
                    .addOnFailureListener { exception ->
                        if (exception is ResolvableApiException) {
                            requestEnableLocation()
                            continuation.resume(null)
                        } else {
                            continuation.resumeWithException(exception)
                        }
                    }
            }
        } catch (_: Exception) {
            null
        }
    }

    private fun requestEnableLocation() {
        val locationReq = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            5000
        ).build()
        val locationSettingsReq = LocationSettingsRequest.Builder()
            .addLocationRequest(locationReq)
            .build()
        settingsClient.checkLocationSettings(locationSettingsReq)
            .addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    try {
                        exception.startResolutionForResult(context.unwrap(), 0)
                    } catch (_: IntentSender.SendIntentException) {
                        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        context.startActivity(intent)
                    }
                } else {
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    context.startActivity(intent)
                }
            }
    }
}

@Composable
fun rememberLocationReceiver(): LocationReceiver {
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            if (!
                ActivityCompat.shouldShowRequestPermissionRationale(
                    context.unwrap(), Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                val intent = Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", context.packageName, null)
                )
                context.startActivity(intent)
            }
        }
    }

    return remember {
        LocationReceiver(
            context = context,
            onRequestPermission = { permissionLauncher.launch(it) },
        )
    }
}