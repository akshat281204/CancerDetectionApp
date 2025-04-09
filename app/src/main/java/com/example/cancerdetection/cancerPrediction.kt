package com.example.cancerdetection

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.nio.ByteBuffer
import java.nio.ByteOrder
import org.tensorflow.lite.Interpreter

class cancerPrediction : AppCompatActivity() {
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_IMAGE_PICK = 2
    private val CAMERA_PERMISSION_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_lung_cancer_prediction)

        // Capture Image Button
        findViewById<Button>(R.id.btnCapture).setOnClickListener {
            captureImage()
        }

        // Pick Image Button
        findViewById<Button>(R.id.btnGallery).setOnClickListener {
            pickImageFromGallery()
        }
    }
    private fun captureImage() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
        } else {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    val bitmap = data.extras?.get("data") as? Bitmap
                    if (bitmap != null) {
                        classifyImage(bitmap)
                    } else {
                        Toast.makeText(this, "Could not capture image", Toast.LENGTH_SHORT).show()
                    }
                }
                REQUEST_IMAGE_PICK -> {
                    val uri = data.data
                    if (uri != null) {
                        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                        classifyImage(bitmap)
                    } else {
                        Toast.makeText(this, "Could not load image from gallery", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun preprocessImage(bitmap: Bitmap): ByteBuffer {
        val inputSize = 224
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, true)
        val byteBuffer = ByteBuffer.allocateDirect(4 * inputSize * inputSize * 3)
        byteBuffer.order(ByteOrder.nativeOrder())

        for (y in 0 until inputSize) {
            for (x in 0 until inputSize) {
                val pixel = resizedBitmap.getPixel(x, y)
                byteBuffer.putFloat((pixel shr 16 and 0xFF) / 255.0f) // Red
                byteBuffer.putFloat((pixel shr 8 and 0xFF) / 255.0f)  // Green
                byteBuffer.putFloat((pixel and 0xFF) / 255.0f)        // Blue
            }
        }
        return byteBuffer
    }

    private fun classifyImage(bitmap: Bitmap) {
        val model = loadModel()
        val inputBuffer = preprocessImage(bitmap)
        val outputBuffer = Array(1) { FloatArray(4) } // Assuming 4 classes (Benign, Malignant, Normal, Rejection)

        // Run inference
        model.run(inputBuffer, outputBuffer)

        // Get the predicted class
        val classIndex = outputBuffer[0].indices.maxByOrNull { outputBuffer[0][it] } ?: -1
        val classLabels = arrayOf("Benign", "Malignant", "Normal", "Rejection")
        val explanations = arrayOf(
            "Benign : This indicates non-cancerous cells. Consider consulting a healthcare provider.",
            "Malignant: Cancerous cells detected. Immediate medical attention is recommended.",
            "Normal: No signs of cancer detected. Keep maintaining a healthy lifestyle!",
            "Rejection : The image does not seem to be related to lung cancer. Please provide a valid medical image."
        )
        val result = if (classIndex >= 0) classLabels[classIndex] else "Unknown"
        val explanation = if (classIndex >= 0) explanations[classIndex] else "Unable to determine the result. Please try again."

        // Update the TextView
        val resultTextView = findViewById<TextView>(R.id.textView)
        resultTextView.text = "Prediction: $result\n\n$explanation"

        // Close the model after usage
        model.close()
    }

    private fun loadModel(): Interpreter {
        val modelFile = assets.open("lung_cancer_model_with_rejection.tflite").use { it.readBytes() }
        val modelBuffer = ByteBuffer.allocateDirect(modelFile.size).apply {
            put(modelFile)
            rewind()
        }
        return Interpreter(modelBuffer)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                captureImage() // Retry
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}