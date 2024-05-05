package com.darkblue.miniproject

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresExtension
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.darkblue.miniproject.ui.theme.MiniProjectTheme
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import okhttp3.MediaType.Companion.toMediaTypeOrNull

class MainActivity : ComponentActivity() {
    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        val retrofit = Retrofit.Builder()
//            .baseUrl("https://wise-pet-moth.ngrok-free.app")
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//
//        val apiService = retrofit.create(ApiService::class.java)
//
//        val call = apiService.uploadImage(1)
//        call.enqueue(object : Callback<Post> {
//            override fun onResponse(call: Call<Post>, response: Response<Post>) {
//                if (response.isSuccessful) {
//                    val post = response.body()
//                    // Handle the response data
//                    post?.let {
//                        // Access post properties like userId, id, title, body
//                    }
//                } else {
//                    // Handle unsuccessful response
//                }
//            }
//
//            override fun onFailure(call: Call<Post>, t: Throwable) {
//                // Handle network errors or request failures
//            }
//        })
//
//        imageBitmap?.let { bitmap ->
//            val description = "Image upload description".toRequestBody() // Or any description
//            val imageBody = bitmap.asRequestBody("image/jpeg".toMediaTypeOrNull())
//            val imagePart = MultipartBody.Part.createFormData("image", "image.jpg", imageBody)
//
//            val call = apiService.uploadImage(imagePart)
//            call.enqueue(object : Callback<ResponseBody> {
//                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
//                    if (response.isSuccessful) {
//                        // Handle successful upload
//                    } else {
//                        // Handle unsuccessful upload
//                    }
//                }
//
//                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//                    // Handle network errors or request failures
//                }
//            })
//        }

//        val fileRepo = FileRepo()
//        val file = File(cacheDir, "image.jpg")
//        file.createNewFile()
//        file.outputStream().use {
//            assets.open("image.jpg").copyTo(it)
//        }
//        fileRepo.uploadImage(file)
//
//

        val retrofit = Retrofit.Builder()
            .baseUrl("https://wise-pet-moth.ngrok-free.app")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        var plantName by mutableStateOf("")
        var certainty by mutableStateOf("")

        setContent {
            MiniProjectTheme {
                val viewModel = viewModel<ViewModel> ()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column (
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ){
//                        Button(onClick = {
//                            image = !image
//                        }) {
//                            Text(text = "DISPLAY")
//                        }
                        if(image || imageBitmap != null) {
                            DisplayCapturedImage(imageBitmap = imageBitmap)
                        }
                        Button(onClick = {
                            dispatchTakePictureIntent()
                        }) {
                            Text(text = "CAPTURE")
                        }
                        Button(onClick = {
                            imageBitmap?.let { bitmap ->
                                progress = true
                                val stream = ByteArrayOutputStream()
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                                val byteArray = stream.toByteArray()

                                val imageBody = byteArray.toRequestBody("image/jpeg".toMediaType())

                                val imagePart = MultipartBody.Part.createFormData("file_obj", "image.jpg", imageBody)

                                val call = apiService.uploadImage(imagePart)
                                call.enqueue(object : Callback<ResponseBody> {
                                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                                        progress = false
                                        if (response.isSuccessful) {
                                            val responseBody = response.body()?.string()
                                            // Process the response body as needed
                                            Log.d("MYTAG", responseBody.toString())
                                            plantName = extractNameFromJson(responseBody!!)
                                            certainty = extractCertaintyFromJson(responseBody)
                                        } else {
                                            // Handle unsuccessful upload
                                            Log.d("MYTAG", "unsuccessful upload")
                                            plantName = "Failed to Upload"
                                        }
                                    }

                                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                        progress = false
                                        // Handle network errors or request failures
                                        Log.d("MYTAG", "unsuccessful upload")
                                        plantName = "Failed to Upload"
                                    }
                                })
                            }


//                            val file = File(cacheDir, "image.jpg")
//                            file.createNewFile()
//                            file.outputStream().use {
//                                assets.open("image.jpg").copyTo(it)
//                            }
//                            val imagePart = MultipartBody.Part.createFormData("file_obj", file.name, file.asRequestBody())
////                            val imageFile = File(cacheDir, "image.jpg") // Assuming you have the image file
////                            val imageBody = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
////                            val imagePart = MultipartBody.Part.createFormData("image", imageFile.name, imageBody)
//
//                            val call = apiService.uploadImage(imagePart)
//                            call.enqueue(object : Callback<ResponseBody> {
//                                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
//                                    if (response.isSuccessful) {
//                                        val responseBody = response.body()?.string()
//                                        // Process the response body as needed
//                                        Log.d("MYTAG", responseBody.toString())
//                                        plantName = extractNameFromJson(responseBody!!)
////                                        responseBody.
//                                    } else {
//                                        // Handle unsuccessful upload
//                                        Log.d("MYTAG", "unsuccessful upload")
//                                    }
//                                }
//
//                                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//                                    // Handle network errors or request failures
//                                    Log.d("MYTAG", "unsuccessful upload")
//                                }
//                            })
                        }) {
                            Text(text = "UPLOAD")
                        }
                        if(progress) CircularProgressBar()
                        else {
                            Text(text = plantName)
                            Text(text = certainty)
                        }
//                        val roundedNumber = String.format("%.2f", number).toDouble()
                    }
                }
            }
        }
    }

    fun extractNameFromJson(jsonString: String): String {
        try {
            val jsonObject = JSONObject(jsonString)
            return jsonObject.getString("name")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "Apple"
    }
    fun extractCertaintyFromJson(jsonString: String): String {
        try {
            val jsonObject = JSONObject(jsonString)
            return jsonObject.getString("certainty")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "-1"
    }

    val REQUEST_IMAGE_CAPTURE = 1
    var image by mutableStateOf(false)
    
    var progress by mutableStateOf(false)

//    private fun dispatchTakePictureIntent() {
//        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        try {
//            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
//        } catch (e: ActivityNotFoundException) {
//            // display error state to the user
//        }
//    }

    private fun dispatchTakePictureIntent() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val chooserIntent = Intent.createChooser(galleryIntent, "Select Image")
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(takePictureIntent))

        try {
            startActivityForResult(chooserIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            // Handle the case where the device doesn't support image capture or gallery selection
        }
    }

    // Inside your activity or fragment
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
//            // Retrieve the captured image bitmap from the intent data
//            val extras = data?.extras
//            imageBitmap = extras?.get("data") as Bitmap
//
//            // Now you can use the imageBitmap for further processing or display
//            imageView.setImageBitmap(imageBitmap) // Assuming imageView is your ImageView
//        }
//    }

//    val imageBitmap: Bitmap? = null


    // Define a global variable to store the captured image
    private var imageBitmap: Bitmap? = null


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            // Check if the result is from camera or gallery
            if (data?.data != null) {
                // Image selected from gallery
                val selectedImageUri = data.data
                // Now you can use the selectedImageUri for further processing or display
                // For example, you can load the selected image into an ImageView
                // imageView.setImageURI(selectedImageUri)

                // If you want to convert the selected image URI to a Bitmap, you can do so
                imageBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImageUri)
            } else {
                // Image captured from camera
                imageBitmap = data?.extras?.get("data") as? Bitmap
            }
            // Update the UI to display the selected/captured image
            image = true
        }
    }
//    @Deprecated("Deprecated in Java")
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
//            imageBitmap = data?.extras?.get("data") as? Bitmap
//            // Now you can use the imageBitmap in your Jetpack Compose UI
//            // For example, you can pass it to a composable function that displays the image
//            image = true
//        }
//    }

    @Composable
    fun DisplayCapturedImage(imageBitmap: Bitmap?) {
        if (imageBitmap != null) {
            Image(
                modifier = Modifier.size(400.dp),
                bitmap = imageBitmap.asImageBitmap(),
                contentDescription = "Captured Image",
            )
        } else {
            Text("No image available")
        }
    }

    @Composable
    fun CircularProgressBar() {
        // Define the animation specs for the progress indicator
        val infiniteTransition = rememberInfiniteTransition()
        val rotation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ), label = ""
        )

        Box(
            modifier = Modifier.size(50.dp), // Adjust the size as needed
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            CircularProgressIndicator(
                progress = 0.5f, // Set the progress value if needed (0.0 to 1.0)
                color = Color.Blue, // Set the color of the progress bar
                strokeWidth = 4.dp, // Set the width of the progress bar
                modifier = Modifier.rotate(rotation) // Apply rotation animation
            )
        }
    }

}
