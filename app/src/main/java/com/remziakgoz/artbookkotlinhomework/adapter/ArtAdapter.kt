package com.remziakgoz.artbookkotlinhomework.adapter

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.remziakgoz.artbookkotlinhomework.R
import com.remziakgoz.artbookkotlinhomework.databinding.RecyclerRowBinding
import com.remziakgoz.artbookkotlinhomework.model.Art

class ArtAdapter(val artList: List<Art>, val navController: NavController) : RecyclerView.Adapter<ArtAdapter.ArtHolder>() {

    class ArtHolder(val recyclerRowBinding: RecyclerRowBinding) : RecyclerView.ViewHolder(recyclerRowBinding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtHolder {
        val recyclerRowBinding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ArtHolder(recyclerRowBinding)
    }

    override fun getItemCount(): Int {
        return artList.size
    }

    override fun onBindViewHolder(holder: ArtHolder, position: Int) {
        val art = artList[position]
        holder.recyclerRowBinding.recyclerRowTextView.text = artList.get(position).artName
        val byteArray = art.imageUri
        byteArray?.let {
            val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
            holder.recyclerRowBinding.recyclerRowImageView.setImageBitmap(bitmap)
        }
        holder.itemView.setOnClickListener {
            val bundle = Bundle().apply {
                putSerializable("info", "old")
                putInt("id", art.id)
            }
            navController.navigate(R.id.action_navigation_home_to_navigation_dashboard, bundle)
        }





    }
}