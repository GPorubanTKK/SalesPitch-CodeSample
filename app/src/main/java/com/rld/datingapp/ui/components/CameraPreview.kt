package com.rld.datingapp.ui.components

import android.content.Context
import android.util.Log
import android.view.Surface
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.rld.datingapp.LOGGERTAG
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Composable fun CameraPreviewScreen(
    modifier: Modifier = Modifier,
    lensFacing: Int = CameraSelector.LENS_FACING_FRONT,
    onImageCaptured: (ImageProxy) -> Unit = {},
    onImageDetectFaces: (MutableList<Face>) -> Unit = {}
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val preview = Preview.Builder().build()
    val previewView = remember { PreviewView(context) }
    val cameraXSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
    val imageCapture = remember { ImageCapture.Builder().setTargetRotation(Surface.ROTATION_0).build() }
    LaunchedEffect(lensFacing) {
        val cameraProvider = context.getCameraProvider()
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(lifecycleOwner, cameraXSelector, preview)
        cameraProvider.bindToLifecycle(lifecycleOwner, cameraXSelector, imageCapture)
        preview.surfaceProvider = previewView.surfaceProvider
    }
    Box(contentAlignment = Alignment.BottomCenter, modifier = modifier) {
        AndroidView(factory = { previewView }, modifier = modifier)
        Row {
            IconButton(Icons.Default.AccountCircle) {
                imageCapture.takePicture(
                    ContextCompat.getMainExecutor(context),
                    object : ImageCapture.OnImageCapturedCallback() {
                        override fun onCaptureSuccess(image: ImageProxy) {
                            super.onCaptureSuccess(image)
                            onImageCaptured(image)
                            val detectorOptions = FaceDetectorOptions.Builder()
                                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                                .build()
                            val inputImage = InputImage.fromBitmap(image.toBitmap(), image.imageInfo.rotationDegrees)
                            val detector = FaceDetection.getClient(detectorOptions)
                            detector.process(inputImage)
                                .addOnSuccessListener { onImageDetectFaces(it) }
                                .addOnFailureListener { Log.e(LOGGERTAG, "MLKit error", it) }
                        }
                        override fun onError(exception: ImageCaptureException) {
                            super.onError(exception)
                            Log.e(LOGGERTAG, "Error: $exception")
                        }
                    }
                )
            }
        }
    }
}

private suspend fun Context.getCameraProvider(): ProcessCameraProvider = suspendCoroutine { continuation ->
    ProcessCameraProvider.getInstance(this).also {
        it.addListener({
            continuation.resume(it.get())
        }, ContextCompat.getMainExecutor(this))
    }
}