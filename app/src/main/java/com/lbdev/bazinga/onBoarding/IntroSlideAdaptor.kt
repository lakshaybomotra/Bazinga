package com.lbdev.bazinga.onBoarding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lbdev.bazinga.R

class IntroSlideAdaptor(private val introSlides: List<IntroSlide>)
    : RecyclerView.Adapter<IntroSlideAdaptor.IntroSlideViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IntroSlideViewHolder {
        return IntroSlideViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.slider_layout,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: IntroSlideViewHolder, position: Int) {
        holder.bind(introSlides[position])
    }

    override fun getItemCount(): Int {
        return introSlides.size
    }

    inner class IntroSlideViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val textTitle = view.findViewById<TextView>(R.id.sliderTitle)
        private val textDesc = view.findViewById<TextView>(R.id.sliderDesc)
        private val image = view.findViewById<ImageView>(R.id.sliderImg)

        fun bind(introSlide: IntroSlide) {
            textTitle.text = introSlide.title
            textDesc.text = introSlide.description
            image.setImageResource(introSlide.image)
        }
    }
}