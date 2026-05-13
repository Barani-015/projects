package com.carebank.smsreader

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

data class SmsMessage(
    val id: Long,
    val address: String,
    val body: String,
    val timestamp: Long
)

class SmsAdapter(private val messages: List<SmsMessage>) :
    RecyclerView.Adapter<SmsAdapter.SmsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SmsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sms, parent, false)
        return SmsViewHolder(view)
    }

    override fun onBindViewHolder(holder: SmsViewHolder, position: Int) {
        holder.bind(messages[position])
    }

    override fun getItemCount(): Int = messages.size

    class SmsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val addressText: TextView = itemView.findViewById(R.id.smsAddress)
        private val bodyText: TextView = itemView.findViewById(R.id.smsBody)
        private val timestampText: TextView = itemView.findViewById(R.id.smsTimestamp)

        fun bind(message: SmsMessage) {
            addressText.text = message.address
            bodyText.text = message.body
            val date = Date(message.timestamp)
            val format = SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.getDefault())
            timestampText.text = format.format(date)
        }
    }
}