package com.example.headsupsqlite_saveonly

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.headsupsqlite_saveonly.databinding.InformationsViewBinding

class RVAdaptar (private val informationList: ArrayList<ArrayList<String>>): RecyclerView.Adapter<RVAdaptar.ItemViewHolder>() {

    private lateinit var myListener: OnItemClickListener

    class ItemViewHolder(val binding: InformationsViewBinding, listener: OnItemClickListener ): RecyclerView.ViewHolder(binding.root){

        init {
            itemView.setOnClickListener{
                listener.onItemClick(adapterPosition)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener:OnItemClickListener ){
        myListener=listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(InformationsViewBinding.inflate(LayoutInflater.from(parent.context), parent, false),myListener)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val name = informationList[position][1]
        val taboo1 = informationList[position][2]
        val taboo2 = informationList[position][3]
        val taboo3 = informationList[position][4]

        holder.binding.apply {
            nameTV.text = "Name: $name"
            taboo1TV.text = taboo1
            taboo2TV.text = taboo2
            taboo3TV.text = taboo3

        }
    }

    override fun getItemCount() = informationList.size

}