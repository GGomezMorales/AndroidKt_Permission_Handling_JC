package com.example.permissionhandler

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val permissionState = rememberMultiplePermissionsState(
                permissions = listOf(
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.RECORD_AUDIO
                )
            )

            val lifecycleOwner = LocalLifecycleOwner.current
            DisposableEffect(
                key1 = lifecycleOwner,
                effect = {
                    val observer = LifecycleEventObserver { _, event ->
                        if (event == Lifecycle.Event.ON_RESUME) {
                            permissionState.launchMultiplePermissionRequest()
                        }
                    }
                    lifecycleOwner.lifecycle.addObserver(observer)
                    onDispose {
                        lifecycleOwner.lifecycle.removeObserver(observer)
                    }
                }
            )

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                permissionState.permissions.forEach { perm ->
                    when (perm.permission) {
                        android.Manifest.permission.CAMERA -> {
                            when {
                                perm.hasPermission -> {
                                    Text(text = "Camera permission accepted")
                                }

                                perm.shouldShowRationale -> {
                                    Text(text = "Camera permission is needed to access the camera")
                                }

                                !perm.isPermanentlyDenied() -> {
                                    Text(text = "Camera permission was permanently denied. You can enable it in the app setting")
                                }
                            }
                        }

                        android.Manifest.permission.RECORD_AUDIO -> {
                            when {
                                perm.hasPermission -> {
                                    Text(text = "Record audio permission accepted")
                                }

                                perm.shouldShowRationale -> {
                                    Text(text = "Record audio permission is needed to access the camera")
                                }

                                !perm.isPermanentlyDenied() -> {
                                    Text(text = "Record audio permission was permanently denied. You can enable it in the app setting")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
fun PermissionState.isPermanentlyDenied(): Boolean {
    return !shouldShowRationale && !hasPermission
}