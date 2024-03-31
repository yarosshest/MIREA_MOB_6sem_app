package com.example.mirea_mob_6sem.find

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mirea_mob_6sem.R
import com.example.mirea_mob_6sem.api.Film
import java.net.URL


class FilmAdapter (private val films: List<Film>) :
    RecyclerView.Adapter<FilmAdapter.MyViewHolder>()
    {
        private var onClickListener: OnClickListener? = null


        class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val photo: ImageView = itemView.findViewById(R.id.imageView)
            val name: TextView = itemView.findViewById(R.id.textName)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val itemView =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_film, parent, false)
            return MyViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val film : Film = films[position]
            holder.itemView.tag = film

            holder.itemView.setOnClickListener {
                if (onClickListener != null) {
                    onClickListener!!.onClick(position, film)
                }
            }

            val currentUrl: String = film.photo
            Glide.with(holder.itemView)
                .load(currentUrl)
                .override(200, 300)
                .into(holder.photo)
            holder.name.text = film.name
        }

        override fun getItemCount() = films.size


        fun setOnClickListener(onClickListener: OnClickListener) {
            this.onClickListener = onClickListener
        }


        // onClickListener Interface
        interface OnClickListener {
            fun onClick(position: Int, model: Film)
        }

    }