import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bs.sriwilis.nasabah.R

@Suppress("DEPRECATION")
class TabAdapter(
    private val tabs: List<String>,
    private val onTabSelected: (Int) -> Unit
) : RecyclerView.Adapter<TabAdapter.TabViewHolder>() {

    private var selectedPosition: Int = 0  // Track the currently selected position

    inner class TabViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tabText: TextView = itemView.findViewById(R.id.tv_tab_penarikan)

        init {
            itemView.setOnClickListener {
                val previousPosition = selectedPosition
                selectedPosition = adapterPosition
                notifyItemChanged(previousPosition)  // Notify previous item
                notifyItemChanged(selectedPosition)   // Notify current item
                onTabSelected(selectedPosition)        // Notify fragment
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TabViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_tab_penarikan, parent, false)
        return TabViewHolder(view)
    }

    override fun onBindViewHolder(holder: TabViewHolder, position: Int) {
        holder.tabText.text = tabs[position]
        if (position == selectedPosition) {
            holder.itemView.setBackgroundColor(holder.itemView.context.getColor(R.color.blue_calm))
        } else {
            holder.itemView.setBackgroundColor(holder.itemView.context.getColor(android.R.color.white))
        }
    }

    override fun getItemCount(): Int {
        return tabs.size
    }
}
