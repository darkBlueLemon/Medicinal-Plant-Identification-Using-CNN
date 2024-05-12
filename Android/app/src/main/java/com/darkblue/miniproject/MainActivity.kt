package com.darkblue.miniproject

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.hardware.Camera
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresExtension
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
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

        setContent {
            MiniProjectTheme {
//                val viewModel = viewModel<ViewModel> ()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if(loadingAnimation) {
                        AnimatedPreloader(modifier = Modifier.size(400.dp), R.raw.leafloading)
                    } else {
                        SelectImage()
                    }
//                    Column (
//                        modifier = Modifier.fillMaxSize(),
//                        horizontalAlignment = Alignment.CenterHorizontally,
//                        verticalArrangement = Arrangement.SpaceAround
//                    ){
//                        if(image || imageBitmap != null) {
//                            DisplayCapturedImage()
//                        } else {
//                            Spacer(modifier = Modifier.size(400.dp))
//                        }
//                        Button(onClick = {
//                            dispatchTakePictureIntent()
//                        }) {
//                            Text(text = "UPLOAD")
//                        }
//                        Column (
////                            modifier = Modifier.fillMaxSize(),
//                            horizontalAlignment = Alignment.CenterHorizontally,
//                            verticalArrangement = Arrangement.Center
//                        ) {
//                            Button(
//                                onClick = {
//                                    imageBitmap?.let { bitmap ->
//                                        progress = true
//                                        val stream = ByteArrayOutputStream()
//                                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
//                                        val byteArray = stream.toByteArray()
//
//                                        val imageBody =
//                                            byteArray.toRequestBody("image/jpeg".toMediaType())
//
//                                        val imagePart = MultipartBody.Part.createFormData(
//                                            "file_obj",
//                                            "image.jpg",
//                                            imageBody
//                                        )
//
//                                        val call = apiService.uploadImage(imagePart)
//                                        call.enqueue(object : Callback<ResponseBody> {
//                                            override fun onResponse(
//                                                call: Call<ResponseBody>,
//                                                response: Response<ResponseBody>
//                                            ) {
//                                                progress = false
//                                                if (response.isSuccessful) {
//                                                    val responseBody = response.body()?.string()
//                                                    // Process the response body as needed
//                                                    Log.d("MYTAG", responseBody.toString())
//                                                    plantName = extractNameFromJson(responseBody!!)
//                                                    certainty =
//                                                        extractCertaintyFromJson(responseBody)
//                                                } else {
//                                                    // Handle unsuccessful upload
//                                                    Log.d("MYTAG", "unsuccessful upload")
//                                                    plantName = "Failed to Upload"
//                                                }
//                                            }
//
//                                            override fun onFailure(
//                                                call: Call<ResponseBody>,
//                                                t: Throwable
//                                            ) {
//                                                progress = false
//                                                // Handle network errors or request failures
//                                                Log.d("MYTAG", "Network Error")
//                                                plantName = "Network Error"
//                                            }
//                                        })
//                                    }
//                                }) {
//                                Text(text = "PREDICT")
//                            }
//                        }
//                        if(progress) CircularProgressBar()
//                        else {
//                            Column (
////                                modifier = Modifier.fillMaxSize(),
//                                horizontalAlignment = Alignment.CenterHorizontally,
//                                verticalArrangement = Arrangement.Center
//                            ) {
//                                Text(text = plantName)
//                                if(certainty.length > 3) Text(text = "Confidence: " + certainty.substring(0,5) + '%')
//                            }
//                        }
//                    }
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

    private var imageBitmap: Bitmap? = null
//    var imageBitmap by mutableStateOf(null)
    var imagePicker by mutableStateOf(false)



    val retrofit = Retrofit.Builder()
        .baseUrl("https://wise-pet-moth.ngrok-free.app")
//            .baseUrl("http://127.0.0.1:4529")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService = retrofit.create(ApiService::class.java)

    var plantName by mutableStateOf("")
    var certainty by mutableStateOf("")
    var loadingAnimation by mutableStateOf(false)



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
    fun DisplayCapturedImage() {
//        if (imageBitmap != null) {

            Image(
                modifier = Modifier
//                    .clip(RoundedCornerShape(200.dp))
                    .padding(10.dp)
                    .size(500.dp)
                    .clickable {
                        image = false
                        imageBitmap = null
                        plantName = ""
                        certainty = ""
                        dispatchTakePictureIntent()
                    }
                ,
                bitmap = imageBitmap!!.asImageBitmap(),
                contentDescription = "Captured Image",
            )
//        } else {
//            Text("No image available")
//        }
    }

    @Composable
    fun AnimatedPreloader(modifier: Modifier = Modifier, drawable: Int, iterations: Int = LottieConstants.IterateForever,) {
        val preloaderLottieComposition by rememberLottieComposition(
            LottieCompositionSpec.RawRes(
                drawable
            )
        )

        val preloaderProgress by animateLottieCompositionAsState(
            preloaderLottieComposition,
            iterations = iterations,
            isPlaying = true,

            )
        LottieAnimation(
            composition = preloaderLottieComposition,
            progress = preloaderProgress,
            modifier = modifier,
        )
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

    @Composable
    fun SelectImage() {
        Box(
            modifier = Modifier
                .fillMaxSize()
//                .background(MaterialTheme.color.)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.size(16.dp))

                Row (
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    val context = LocalContext.current as? ComponentActivity
                    Box(
                        modifier = Modifier
                            .width(95.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null, // Disable the click animation
                            ) {
                                context?.finish()
                            }
                            .height(35.dp)
                    ){
                    }

                    Text(
                        text = "Identify Plant",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Box(
                        modifier = Modifier
                            .widthIn(max = 90.dp, min = 90.dp)
                    )
                    {

                    }

                }
                Divider(
                    color = Color.LightGray,
                    thickness = 2.dp,
                    modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)
                )




                var expanded by remember { mutableStateOf(false) }
                val suggestions = listOf("All", "Photo", "Video", "Audio")
                var selectedText by remember { mutableStateOf("") }

                var textfieldSize by remember { mutableStateOf(Size.Zero) }

                val icon = if (expanded)
                    Icons.Filled.KeyboardArrowUp
                else
                    Icons.Filled.KeyboardArrowDown


                Column(
                    Modifier.padding(20.dp),
//        horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top

                ) {
                    val interactionSource = remember { MutableInteractionSource() }
                    Box(
                        modifier = Modifier
                            .height(if (image || imageBitmap != null) 650.dp else 300.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(MaterialTheme.colorScheme.secondaryContainer)
//                            .background(MyRedSecondaryLight)
                            .fillMaxWidth()
//                            .onGloballyPositioned { coordinates ->
//                                textfieldSize = coordinates.size.toSize()
//                            }
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null
                            ) {
                                Log.d("MYTAG", "clicked")
                                dispatchTakePictureIntent()
//                                data.launch("*/*")
                            },
                        contentAlignment = Alignment.Center,
                    ){
                        Column (
//                modifier =
//                    Modifier.clickable { expanded = !expanded },
                            modifier = Modifier
                            ,
                            verticalArrangement = Arrangement.SpaceBetween,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ){
                            if(image || imageBitmap != null) {
                                Column(
//                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Top
                                ) {

                                }
                                DisplayCapturedImage()
                                Text(
                                    text = plantName ,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 25.sp
                                )
                                if(certainty.length > 3) Text(
                                    text = "Confidence: " + certainty.substring(0,5) + '%',
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 25.sp
                                    )
                            } else {
                                Icon(
                                    modifier = Modifier
                                        .size(60.dp)
                                    ,
                                    imageVector = Icons.Default.AddCircle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                )
                                Text(
                                    text = "Upload Image",
//                                style = MaterialTheme.typography.h5,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 30.sp
                                )
                            }
                        }
                    }

                }






                Spacer(modifier = Modifier
                    .weight(1f)
                    .background(Color.LightGray))
                val buttonColor: Color
                val buttonTextColor: Color
                if(image || imageBitmap != null){
                    buttonColor = MaterialTheme.colorScheme.primary
                    buttonTextColor = Color.White
                }else{
                    buttonColor = Color.LightGray
                    buttonTextColor = Color.White
                }

                val interactionSource = remember { MutableInteractionSource() }
                Button(
                    enabled = image || imageBitmap != null,
                    onClick = {
                        imageBitmap?.let { bitmap ->
                            progress = true
                            val stream = ByteArrayOutputStream()
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                            val byteArray = stream.toByteArray()

                            val imageBody =
                                byteArray.toRequestBody("image/jpeg".toMediaType())

                            val imagePart = MultipartBody.Part.createFormData(
                                "file_obj",
                                "image.jpg",
                                imageBody
                            )
                            val call = apiService.uploadImage(imagePart)
                            call.enqueue(object : Callback<ResponseBody> {
                                override fun onResponse(
                                    call: Call<ResponseBody>,
                                    response: Response<ResponseBody>
                                ) {
                                    loadingAnimation = false
                                    progress = false
                                    if (response.isSuccessful) {
                                        val responseBody = response.body()?.string()
                                        // Process the response body as needed
                                        Log.d("MYTAG", responseBody.toString())
                                        plantName = extractNameFromJson(responseBody!!)
                                        certainty =
                                            extractCertaintyFromJson(responseBody)
                                    } else {
                                        // Handle unsuccessful upload
                                        Log.d("MYTAG", "unsuccessful upload")
                                        plantName = "Failed to Upload"
                                    }
                                }

                                override fun onFailure(
                                    call: Call<ResponseBody>,
                                    t: Throwable
                                ) {
                                    loadingAnimation = false
                                    progress = false
                                    // Handle network errors or request failures
                                    Log.d("MYTAG", "Network Error")
                                    plantName = "Network Error"
                                }
                            })
                        }
                        loadingAnimation = true

                    },
                    modifier = Modifier
                        .padding(bottom = 20.dp, start = 20.dp, end = 20.dp)
                        .fillMaxWidth()
                        .size(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = buttonColor
//                        backgroundColor = buttonColor
                    ),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = "Analyse",
//                        style = MaterialTheme.typography.h5,
                        color = buttonTextColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }

            }

        }
    }
}
