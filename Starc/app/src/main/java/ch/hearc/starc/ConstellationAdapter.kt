package ch.hearc.starc

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ConstellationAdapter(val dataset: ArrayList<String>): RecyclerView.Adapter<ConstellationAdapter.ViewHolder>() {

    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val name = itemView.findViewById<TextView>(R.id.name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val viewItem =inflater.inflate(R.layout.item_constellation, parent, false)

        return ViewHolder(viewItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val constellation = dataset.elementAt(position)
        holder.name.text= constellation
    }

    override fun getItemCount(): Int {
        return dataset.size
    }
}