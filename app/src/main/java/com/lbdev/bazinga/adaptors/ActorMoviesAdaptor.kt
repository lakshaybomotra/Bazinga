package com.lbdev.bazinga.adaptors

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lbdev.bazinga.R
import com.lbdev.bazinga.api.Credits
import com.lbdev.bazinga.api.PersonMoviesResponse

class ActorMoviesAdaptor(private var context: Context, list: Credits) :
    RecyclerView.Adapter<ActorMoviesAdaptor.ViewHolder>() {

    private var onClickListener: OnClickListener? = null
    private val listOfMovies =
        list.cast.filter { it.poster_path != null }.sortedByDescending { it.release_date }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var movieImg = v.findViewById<ImageView>(R.id.movieImg)
        var movieName = v.findViewById<TextView>(R.id.movieName)
        var movieDesc = v.findViewById<TextView>(R.id.movieDesc)
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    // onClickListener Interface
    interface OnClickListener {
        fun onClick(view: View, position: Int,posterPath: String, id: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.movies_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listOfMovies.count()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val baseImg = "https://image.tmdb.org/t/p/original"
        Glide.with(context).load(baseImg + listOfMovies[position].poster_path).into(holder.movieImg)
        holder.movieName.text = listOfMovies[position].title
        holder.movieDesc.text = listOfMovies[position].overview

        holder.itemView.setOnClickListener {
            if (onClickListener != null) {
                onClickListener!!.onClick(holder.itemView , position, listOfMovies[position].poster_path, listOfMovies[position].id )
            }
        }
    }
}