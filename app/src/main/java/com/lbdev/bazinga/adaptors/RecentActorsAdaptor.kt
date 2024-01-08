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
import com.lbdev.bazinga.db.RecentBazingas

class RecentActorsAdaptor(private var context: Context, recentBazingas: List<RecentBazingas>) :
    RecyclerView.Adapter<RecentActorsAdaptor.ViewHolder>() {

    private var onClickListener: OnClickListener? = null
    private val listOfActors =
        recentBazingas.sortedByDescending { it.dateAdded }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var rctImg = v.findViewById<ImageView>(R.id.rctImg)
        var rctName = v.findViewById<TextView>(R.id.rctName)
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    // onClickListener Interface
    interface OnClickListener {
        fun onClick(position: Int,photoUrl: String, id: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.recent_actors_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listOfActors.count()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context).load(listOfActors[position].photoUrl).into(holder.rctImg)
        holder.rctName.text = listOfActors[position].name

        holder.itemView.setOnClickListener {
            if (onClickListener != null) {
                onClickListener!!.onClick( position, listOfActors[position].photoUrl, listOfActors[position].id )
            }
        }
    }
}