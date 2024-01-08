package com.lbdev.bazinga

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.lbdev.bazinga.adaptors.ActorMoviesAdaptor
import com.lbdev.bazinga.api.ApiService
import com.lbdev.bazinga.databinding.ActivityActorBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Locale

class ActorActivity : AppCompatActivity() {
    lateinit var viewBinding: ActivityActorBinding
    lateinit var actorMoviesAdaptor: ActorMoviesAdaptor
    lateinit var rvMain: RecyclerView
    lateinit var photoUrl: String
    val context: Context = this
    private val authorizationHeader =
        com.lbdev.bazinga.BuildConfig.movieApi

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.themoviedb.org/3/")
        .client(OkHttpClient())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(ApiService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityActorBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        viewBinding.miShimmerFrameLayout.startShimmer()
        val id = intent.getIntExtra("id", 0)
        photoUrl = intent.getStringExtra("photoUrl").toString()
        rvMain = viewBinding.recyclerView
        val params = rvMain.layoutParams
        params.apply {
            width = context.resources.displayMetrics.widthPixels
            height = context.resources.displayMetrics.heightPixels
        }
        rvMain.layoutParams = params
        rvMain.layoutManager = GridLayoutManager(this, 2)
        rvMain.addItemDecoration(SpacingItemDecoration(2, dpToPx(this, 8), true))
        rvMain.setHasFixedSize(true)
        getPersonMovies(id)
    }

    private fun getPersonMovies(id: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val pdResponse = apiService.searchPersonWithId(
                    id = id,
                    authorizationHeader = authorizationHeader
                )

                withContext(Dispatchers.Main) {
                    if (pdResponse.isSuccessful) {
                        val personResponseBody = pdResponse.body()
                        if (personResponseBody != null) {
                            viewBinding.actorName.text = personResponseBody.name
                            if (personResponseBody.birthday == null || personResponseBody.place_of_birth == null)
                            {
                                viewBinding.actorBirth.visibility = View.GONE
                            } else {
                                val birth = formatBirthday(
                                    personResponseBody.birthday,
                                    personResponseBody.place_of_birth
                                )
                                viewBinding.actorBirth.text = birth
                            }
                            viewBinding.actorImage.load( "https://image.tmdb.org/t/p/w500"+photoUrl)

                            if (personResponseBody.biography == "") {
                                viewBinding.aboutText.visibility = View.GONE
                                viewBinding.actorBio.visibility = View.GONE
                            } else {
                                viewBinding.actorBio.text = personResponseBody.biography
                            }

                            actorMoviesAdaptor =
                                ActorMoviesAdaptor(baseContext, personResponseBody.credits)
                            rvMain.adapter = actorMoviesAdaptor
                            actorMoviesAdaptor.setOnClickListener(
                                object : ActorMoviesAdaptor.OnClickListener {
                                    override fun onClick(
                                        view: View,
                                        position: Int,
                                        posterPath: String,
                                        id: Int
                                    ) {
                                        onItemGridClicked(view, posterPath, id)
                                    }
                                }
                            )
                            viewBinding.miShimmerFrameLayout.visibility = View.GONE
                            viewBinding.miShimmerFrameLayout.stopShimmer()
                            viewBinding.actorLayout.visibility = View.VISIBLE
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("RETROFIT_ERROR exception", e.message ?: "Unknown error")
            }
        }
    }

    private fun onItemGridClicked(sharedElement: View, posterPath: String, id: Int) {
        val intent = Intent(this, MovieActivity::class.java)
        intent.putExtra("EXTRA_ID", id)
        intent.putExtra("EXTRA_POSTER", posterPath)
        val options =
            ActivityOptions.makeSceneTransitionAnimation(this, sharedElement, "EXTRA_VIEW")
        startActivity(intent, options.toBundle())
    }

    private fun formatBirthday(birthday: String, place: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val outputFormat = SimpleDateFormat("MMMM d, yyyy", Locale.US)
        val date = inputFormat.parse(birthday)
        val formattedBirthday = outputFormat.format(date)
        return "Born $formattedBirthday in $place"
    }

    companion object {
        fun dpToPx(c: Context, dp: Int): Int {
            val r = c.resources
            return Math.round(
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    dp.toFloat(),
                    r.displayMetrics
                )
            )
        }
    }

}