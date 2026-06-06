package com.mtali.flashy2.core.torch

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * [TorchController] backed by [CameraManager]. Targets API 24+, so it only needs
 * [CameraManager.setTorchMode] and, on API 33+, [CameraManager.turnOnTorchWithStrengthLevel].
 */
@Singleton
class TorchControllerImpl
@Inject
constructor(
  @param:ApplicationContext private val context: Context,
) : TorchController {
  private val cameraManager: CameraManager? =
    context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager

  /** The id of the first camera that has a flash unit, if any. */
  private val flashCameraId: String? by lazy {
    val manager = cameraManager ?: return@lazy null
    runCatching {
      manager.cameraIdList.firstOrNull { id ->
        manager.getCameraCharacteristics(id)
          .get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
      }
    }.getOrNull()
  }

  override val hasFlashUnit: Boolean by lazy {
    context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH) &&
      flashCameraId != null
  }

  override val maxStrength: Int by lazy {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
      return@lazy 1
    }
    val manager = cameraManager ?: return@lazy 1
    val id = flashCameraId ?: return@lazy 1
    runCatching {
      manager.getCameraCharacteristics(id)
        .get(CameraCharacteristics.FLASH_INFO_STRENGTH_MAXIMUM_LEVEL) ?: 1
    }.getOrDefault(1)
  }

  override fun turnOn(strength: Int?) {
    val manager = cameraManager ?: return
    val id = flashCameraId ?: return
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && supportsStrength && strength != null) {
      manager.turnOnTorchWithStrengthLevel(id, strength.coerceIn(1, maxStrength))
    } else {
      manager.setTorchMode(id, true)
    }
  }

  override fun turnOff() {
    val manager = cameraManager ?: return
    val id = flashCameraId ?: return
    manager.setTorchMode(id, false)
  }
}
