// CardAdapter.kt
// Import statement untuk mendapatkan akses ke kelas dan metode yang diperlukan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.transferingfileapp.R
import com.example.transferingfileapp.model.DataItem
import com.example.transferingfileapp.model.DataResponseClass

// Definisi kelas CardAdapter, yang merupakan turunan dari RecyclerView.Adapter
class CardAdapter(private val dataList: List<DataItem>, private val onDownloadClick: (Int) -> Unit) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    // Definisi kelas dalam CardAdapter untuk menampung tampilan setiap item dalam RecyclerView
    class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Inisialisasi variabel untuk menampung elemen UI dalam tampilan item kartu
        val textTitle: TextView = itemView.findViewById(R.id.textTitle)
        val textFrom: TextView = itemView.findViewById(R.id.textFrom)
        val textTo: TextView = itemView.findViewById(R.id.textTo)
        val btnDownload: Button = itemView.findViewById(R.id.btnDownload)
    }

    // Metode yang dipanggil ketika RecyclerView memerlukan tampilan baru untuk ditampilkan
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        // Membuat tampilan item baru dari layout XML menggunakan LayoutInflater
        // LayoutInflater adalah kelas yang digunakan untuk mengubah tampilan XML menjadi tampilan
        // UI yang sesungguhnya di dalam kotlin atau java
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_item, parent, false)
        // Mengembalikan tampilan item ke dalam CardViewHolder
        return CardViewHolder(itemView)
    }

    // Metode yang dipanggil ketika RecyclerView perlu menampilkan data
    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        // Mendapatkan data pada posisi tertentu dari dataList
        val data = dataList[position]
        // Menetapkan nilai ke elemen UI dalam tampilan item
        holder.textTitle.text = data.name
        holder.textFrom.text = "From: ${data.from}"
        holder.textTo.text = "To: ${data.to}"
        // Menetapkan aksi klik pada tombol Download
        holder.btnDownload.setOnClickListener {
            // Menjalankan fungsi onDownloadClick dengan id data sebagai argumen
            onDownloadClick(data.id)
        }
    }

    // Metode yang mengembalikan jumlah total item dalam RecyclerView
    override fun getItemCount(): Int {
        return dataList.size
    }
}
