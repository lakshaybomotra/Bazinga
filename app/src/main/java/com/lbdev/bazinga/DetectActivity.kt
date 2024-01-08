package com.lbdev.bazinga

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Images.Media
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import coil.load
import com.lbdev.bazinga.databinding.ActivityDetectBinding
import com.lbdev.bazinga.api.ApiRequestBody
import com.lbdev.bazinga.api.ApiService
import com.lbdev.bazinga.api.PersonResponse
import com.lbdev.bazinga.api.RetrofitInstance
import com.lbdev.bazinga.db.RecentBazingas
import com.lbdev.bazinga.db.RecentBazingasDatabase
import de.hdodenhof.circleimageview.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream
import kotlin.properties.Delegates

class DetectActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityDetectBinding
    lateinit var name: String
    lateinit var photoName: String
    var id: Int? = null
    lateinit var photoUrl: String
    private lateinit var myDB: RecentBazingasDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityDetectBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        myDB = RecentBazingasDatabase.getDatabase(this)
//        viewBinding.shimmerFrameLayout.startShimmer()
//        val bitmap = intent.getParcelableExtra<Bitmap>("bitmap")
        photoName = intent.getStringExtra("name").toString()
        loadPhotosFromInternalStorage(photoName)
//        val photoUri = intent.getStringExtra("photoUrl").toString()
//        var photoBitmap: Bitmap? = null

//        detectApiCall(bitmap!!, this)
//        if (bitmap != null) {
//            Toast.makeText(this, "not null", Toast.LENGTH_SHORT).show()
//            detectApiCall(bitmap, this)
//        } else {
//            Toast.makeText(this, "null", Toast.LENGTH_SHORT).show()
//        }

        viewBinding.actorBtn.setOnClickListener {
            val intent = Intent(this, ActorActivity::class.java)
            intent.putExtra("id", id)
            intent.putExtra("photoUrl", photoUrl)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        cacheDir.deleteRecursively()
        super.onDestroy()
    }

    private fun searchPerson(query: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .client(OkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)


        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.searchPerson(
                    query = query,
                    authorizationHeader = com.lbdev.bazinga.BuildConfig.movieApi
                )
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null) {
                            val firstPerson = responseBody.getFirstPerson()
                            id = firstPerson!!.id
                            photoUrl =
                                "https://image.tmdb.org/t/p/original" + firstPerson.profilePath
                            viewBinding.testImg.load(photoUrl) {
                                crossfade(true)
                            }.apply {
                                saveDetectedActor(id!!, photoUrl, firstPerson.name)
                                viewBinding.nameText.text = firstPerson.name
                                viewBinding.shimmerFrameLayout.stopShimmer()
                                viewBinding.shimmerFrameLayout.visibility = android.view.View.GONE
                                viewBinding.detectMainLayout.visibility = android.view.View.VISIBLE
                            }
                        } else {
                            Log.e("RETROFIT_ERROR first else", "Response body is null")
                        }
                    } else {
                        Log.e("RETROFIT_ERROR second else", response.message())
                    }
                }
            } catch (e: Exception) {
                Log.e("RETROFIT_ERROR exception", e.message ?: "Unknown error")
            }
        }
    }

    private fun saveDetectedActor(id: Int, photoUrl: String, name: String) {
        val recentBazingas = RecentBazingas(
            name = name,
            photoUrl = photoUrl,
            dateAdded = System.currentTimeMillis(),
            id = id
        )

        GlobalScope.launch(Dispatchers.IO) {
            myDB.recentDao().upsertRecentBazingas(recentBazingas)
            val appWidgetManager = AppWidgetManager.getInstance(this@DetectActivity)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(
                ComponentName(
                    this@DetectActivity,
                    RecentBazingaWidget::class.java
                )
            )

            for (appWidgetId in appWidgetIds) {
                updateRecentWidget(this@DetectActivity, appWidgetManager, appWidgetId)
            }
        }
    }

    private fun detectApiCall(validInputImage: Bitmap, context: Context) {
        val base64Api = bitmapToBase64(validInputImage)
        val apiKey = com.lbdev.bazinga.BuildConfig.opencvApiKey
        val requestBody = ApiRequestBody(
            com.lbdev.bazinga.BuildConfig.opencvCollectionId,
            listOf(base64Api),
            10,
            0.7,
            "FAST"
        )
        RetrofitInstance.api.postData(apiKey, requestBody).enqueue(object :
            Callback<List<PersonResponse>> {
            override fun onResponse(
                call: Call<List<PersonResponse>>,
                response: Response<List<PersonResponse>>
            ) {
                if (response.isSuccessful) {
                    val personList = response.body()
                    if (!personList.isNullOrEmpty()) {
                        personList.forEach { person ->
                            name = person.name
                            searchPerson(person.name)
                        }
                    } else {
                        Toast.makeText(context, "No Match Found", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } else {
                    Toast.makeText(context, "No Face Detected!!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                deletePhotoFromInternalStorage("$photoName.png")
            }

            override fun onFailure(call: Call<List<PersonResponse>>, t: Throwable) {
                // Handle the failure
                viewBinding.shimmerFrameLayout.stopShimmer()
                deletePhotoFromInternalStorage("$photoName.png")
                finish()
                Toast.makeText(context, "Try Again", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun deletePhotoFromInternalStorage(filename: String): Boolean {
        return try {
            deleteFile(filename)
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun loadPhotosFromInternalStorage(photoName: String) {
        val files = filesDir.listFiles()
        val myfile = files?.filter { it.canRead() && it.isFile && it.name.contains(photoName) }
        val bytes = myfile!![0].readBytes()
        val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        detectApiCall(bmp, this)
    }

    companion object {
        private fun bitmapToBase64(bitmap: Bitmap): String {
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream)
            val imageBytes = byteArrayOutputStream.toByteArray()
            return Base64.encodeToString(imageBytes, Base64.DEFAULT)
        }
    }


}