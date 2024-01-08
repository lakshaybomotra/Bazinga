package com.lbdev.bazinga

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.google.android.material.color.MaterialColors
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import com.lbdev.bazinga.api.ApiService
import com.lbdev.bazinga.databinding.ActivityMovieBinding
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MovieActivity : AppCompatActivity() {

    private var duration = 800L
    lateinit var viewBinding: ActivityMovieBinding

    private val authorizationHeader =
        com.lbdev.bazinga.BuildConfig.movieApi

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.themoviedb.org/3/")
        .client(OkHttpClient())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(ApiService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        viewBinding = ActivityMovieBinding.inflate(layoutInflater)
        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        findViewById<View>(android.R.id.content).transitionName = "EXTRA_VIEW"
        setEnterSharedElementCallback(MaterialContainerTransformSharedElementCallback())
        window.sharedElementEnterTransition = buildContainerTransform(true)
        window.sharedElementReturnTransition = buildContainerTransform(false)

        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)

        val id = intent.getIntExtra("EXTRA_ID",0)
        val posterPath = intent.getStringExtra("EXTRA_POSTER")
        Glide.with(this).load("https://image.tmdb.org/t/p/w500$posterPath").into(viewBinding.movieImage)

        getMovieDetails(id)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            super.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun buildContainerTransform(entering: Boolean): MaterialContainerTransform? {
        val transform = MaterialContainerTransform()
        transform.transitionDirection =
            if (entering) MaterialContainerTransform.TRANSITION_DIRECTION_ENTER else MaterialContainerTransform.TRANSITION_DIRECTION_RETURN
        transform.setAllContainerColors(
            MaterialColors.getColor(
                findViewById(android.R.id.content),
                com.google.android.material.R.attr.colorPrimary
            )
        )
        transform.addTarget(android.R.id.content)
        transform.duration = duration
        return transform
    }

    private fun getMovieDetails(id: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val movieResponse = apiService.getMovieDetails(
                    id = id,
                    authorizationHeader = authorizationHeader
                )

                withContext(Dispatchers.Main) {
                    if (movieResponse.isSuccessful) {
                        val movieResponseBody = movieResponse.body()
                        if (movieResponseBody != null) {
                            val movieName = movieResponseBody.title + " (" + movieResponseBody.release_date.substring(0,4) + ")"
                            viewBinding.movieName.text = movieName
                            if (movieResponseBody.genres.isNotEmpty()) {
                                var genre1: String? = null
                                var genre2: String? = null
                                var genre3: String? = null
                                for (i in 0 until movieResponseBody.genres.size) {
                                    if (i == 0) {
                                        genre1 = movieResponseBody.genres[i].name
                                    }
                                    else if (i == 1) {
                                        genre2 = movieResponseBody.genres[i].name
                                    }
                                    else if (i == 2) {
                                        genre3 = movieResponseBody.genres[i].name
                                    }
                                }

                                if (genre1 != null) {
                                    viewBinding.genre1.text = genre1
                                    viewBinding.genre1.visibility = View.VISIBLE
                                }
                                if (genre2 != null) {
                                    viewBinding.genre2.text = genre2
                                    viewBinding.genre2.visibility = View.VISIBLE
                                }
                                if (genre3 != null) {
                                    viewBinding.genre3.text = genre3
                                    viewBinding.genre3.visibility = View.VISIBLE
                                }
                            }
                            val rating = "%.1f".format(movieResponseBody.vote_average)
                            viewBinding.movieRating.text = rating

                            val timing = (movieResponseBody.runtime / 60).toString() + "h"
                            viewBinding.movieTiming.text = timing

                            val cast = movieResponseBody.credits.cast.filter { it.profile_path != null }.sortedByDescending { it.popularity }

                            for (actors in cast)
                            {
                                val imageView = CircleImageView(this@MovieActivity)

                                imageView.layoutParams = LinearLayout.LayoutParams(
                                    300,
                                    300
                                )
                                imageView.setPadding(0, 0, 16, 0)
                                imageView.borderWidth = 2
                                imageView.borderColor = Color.WHITE
                                imageView.scaleType = ImageView.ScaleType.CENTER_CROP

                                Glide.with(this@MovieActivity)
                                    .load("https://image.tmdb.org/t/p/w500${actors.profile_path}")
                                    .into(imageView)

                                imageView.setOnClickListener {
                                    Toast.makeText(this@MovieActivity, actors.name, Toast.LENGTH_SHORT).show()
                                }
                                viewBinding.castImgLL.addView(imageView)
                            }

                            viewBinding.movieOverview.text = movieResponseBody.overview
                            viewBinding.movieShimmer.stopShimmer()
                            viewBinding.movieShimmer.visibility = View.GONE
                            viewBinding.movieDetails.visibility = View.VISIBLE
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("RETROFIT_ERROR exception", e.message ?: "Unknown error")
            }
        }
    }
}