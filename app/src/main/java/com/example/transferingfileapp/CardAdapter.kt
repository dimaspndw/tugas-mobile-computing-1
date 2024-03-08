// CardAdapter.kt
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.transferingfileapp.R
import com.example.transferingfileapp.model.DataItem
import com.example.transferingfileapp.model.DataResponseClass

class CardAdapter(private val dataList: List<DataItem>, private val onDownloadClick: (Int) -> Unit) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textTitle: TextView = itemView.findViewById(R.id.textTitle)
        val textFrom: TextView = itemView.findViewById(R.id.textFrom)
        val textTo: TextView = itemView.findViewById(R.id.textTo)
        val btnDownload: Button = itemView.findViewById(R.id.btnDownload)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_item, parent, false)
        return CardViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val data = dataList[position]
        holder.textTitle.text = data.name
        holder.textFrom.text = "From: ${data.from}"
        holder.textTo.text = "To: ${data.to}"
        holder.btnDownload.setOnClickListener {
            onDownloadClick(data.id)
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}
