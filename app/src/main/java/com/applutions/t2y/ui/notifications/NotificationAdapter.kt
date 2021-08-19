package com.applutions.t2y.ui.notifications

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.applutions.t2y.R
import com.applutions.t2y.common.HelperMethods
import com.applutions.t2y.ui.notifications.response.RemindersItem


class NotificationAdapter(
    private val notificationsFragment: Context,
    private var rentalList: List<RemindersItem>,
    private val manager: FragmentManager
) :
    RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.sgl_item_notification, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val myTrailerData = rentalList[position]
        HelperMethods.downloadImage(myTrailerData.items[0].itemPhoto.data, notificationsFragment, holder.imgTrailer)
        holder.txtTrailerName.text = myTrailerData.items[0].itemName
        holder.txtLicenseeName.text = myTrailerData.licenseeName
        //ChooseOptionBottomSheet(myTrailerData.invoiceId).show(notificationsFragment.parentFragmentManager, "")

        when (myTrailerData.isApproved) {
            0 -> {
                holder.txtStatus.setTextColor(Color.parseColor("#FF9200"))
                holder.txtStatusSub.setTextColor(Color.parseColor("#FF9200"))
                holder.txtStatus.text="Waiting"
                holder.txtStatusSub.text = "For Approval"
            }
            1 -> {
                holder.txtStatus.setTextColor(Color.parseColor("#00D756"))
                holder.txtStatusSub.setTextColor(Color.parseColor("#00D756"))
                holder.txtStatus.text=myTrailerData.reminderText.substring(0,myTrailerData.reminderText.indexOf("t")-1)
                holder.txtStatusSub.text = "to receive"
            }
            2 -> {
                holder.txtStatus.setTextColor(Color.parseColor("#FF002E"))
                holder.txtStatusSub.setTextColor(Color.parseColor("#FF002E"))
                holder.txtStatus.text="Cancelled"
                holder.txtStatusSub.text = "Request"
            }

            /* holder.txtTrailerName.text = "name"
             holder.txtLicenseeName.text = "licenseeName"
             holder.txtStatus.text="reminderType"*/
            // holder.txtStatus.setTextColor(Color.parseColor(myTrailerData.reminderColor))
        }

       /* holder.txtTrailerName.text = "name"
        holder.txtLicenseeName.text = "licenseeName"
        holder.txtStatus.text="reminderType"*/
       // holder.txtStatus.setTextColor(Color.parseColor(myTrailerData.reminderColor))
        holder.itemView.setOnClickListener {
            //ChooseOptionBottomSheet(myTrailerData.invoiceId).show(notificationsFragment.parentFragmentManager, "")
            if(myTrailerData.isTracking) {
                ChooseOptionBottomSheet(myTrailerData.invoiceId).show(manager, "")
            }
            else{
                var intent=Intent(notificationsFragment,BookingDetailsActivity::class.java)
                intent.putExtra("status",myTrailerData.isApproved.toString())
                intent.putExtra("invoiceId",myTrailerData.invoiceId)
                intent.putExtra("licensee",myTrailerData.licenseeName)
                notificationsFragment.startActivity(intent)
            }
        }

    }

    override fun getItemCount(): Int {
        return rentalList.size
    }

    fun notifyDataSetChanged(mList: ArrayList<RemindersItem>) {
        rentalList= mList
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val txtTrailerName: TextView = itemView.findViewById(R.id.txtTrailerName)
        val txtLicenseeName: TextView = itemView.findViewById(R.id.txtLicenseeName)
        val txtStatus: TextView = itemView.findViewById(R.id.txtStatus)
        val imgTrailer: ImageView = itemView.findViewById(R.id.imgTrailer)
        val txtStatusSub : TextView = itemView.findViewById(R.id.txtStatusSub)
    }
    
}