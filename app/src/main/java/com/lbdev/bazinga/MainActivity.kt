package com.lbdev.bazinga

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.lbdev.bazinga.onBoarding.IntroSlide
import com.lbdev.bazinga.onBoarding.IntroSlideAdaptor

class MainActivity : AppCompatActivity() {
    private lateinit var analytics: FirebaseAnalytics

    private val introSliderAdaptor = IntroSlideAdaptor(
        listOf(
            IntroSlide(
                "Face Recognition",
                "Find any actor behind the scene",
                R.drawable.kk,
            ),
            IntroSlide(
                "Discover",
                "Actors, characters and movies around you",
                R.drawable.srk,
            ),
            IntroSlide(
                "Listen",
                "Official movie playlists with spotify",
                R.drawable.ww1
            )
        )
    )
    lateinit var pageIndicator: LinearLayout
    lateinit var progress: CircularProgressIndicator
    lateinit var skipBtn: TextView
    lateinit var nextBtn: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        analytics = Firebase.analytics
//        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContentView(R.layout.activity_main)

        androidx.preference.PreferenceManager.getDefaultSharedPreferences(this).apply {
            val onBoardingStatus = getBoolean("onBoardingStatus", true)
            if (!onBoardingStatus) {
                startActivity(Intent(this@MainActivity, MainScreen::class.java))
                finish()
            }
        }

        pageIndicator = findViewById(R.id.pageIndicator)
        progress = findViewById(R.id.onBdgProgress)
        skipBtn = findViewById(R.id.skipBtn)
        nextBtn = findViewById(R.id.onBdgBtn)
        val introSliderViewPager = findViewById<ViewPager2>(R.id.onBdgViewPager)
        introSliderViewPager.adapter = introSliderAdaptor
        setupIndicators()
        setCurrentIndicator(0)
        introSliderViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
            }
        })

        skipBtn.setOnClickListener {
            androidx.preference.PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("onBoardingStatus", false).apply()
            startActivity(Intent(this, MainScreen::class.java))
            finish()
        }

        nextBtn.setOnClickListener {
            if (introSliderViewPager.currentItem + 1 < introSliderAdaptor.itemCount) {
                introSliderViewPager.currentItem += 1
            } else {
                androidx.preference.PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("onBoardingStatus", false).apply()
                startActivity(Intent(this, MainScreen::class.java))
                finish()
            }
        }
    }

    private fun setupIndicators() {
        val indicators = arrayOfNulls<ImageView>(introSliderAdaptor.itemCount)
        val layoutParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(25, 120)
        layoutParams.setMargins(24, 0, 12, 60)
        for (i in indicators.indices)
        {
            indicators[i] = ImageView(applicationContext)
            indicators[i].apply {
                this?.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_inactive
                    )
                )
                this?.layoutParams = layoutParams
            }
            pageIndicator.addView(indicators[i])
        }
    }

    private fun setCurrentIndicator(index: Int) {
        when (index) {
            0 -> progress.progress = 33
            1 -> progress.progress = 66
            2 -> progress.progress = 100
            else -> { // Note the block
                print("x is neither 1 nor 2")
            }
        }
        val childCount = pageIndicator.childCount
        for (i in 0 until childCount) {
            val imageView = pageIndicator.getChildAt(i) as ImageView
            if (i == index) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_active
                    )
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_inactive
                    )
                )
            }
        }
    }
}