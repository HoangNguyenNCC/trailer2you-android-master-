package com.applutions.t2y.ui.notifications

import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.applutions.t2y.R
import com.applutions.t2y.common.AnimUtils
import com.applutions.t2y.common.HelperMethods
import com.applutions.t2y.customViews.BoldTextView
import com.applutions.t2y.data.response.booking.BookingEditResponse
import com.applutions.t2y.ui.booking.FinalActivity
import com.applutions.t2y.ui.rentTrailer.BookActivity
import com.applutions.t2y.utils.ApiResponse
import com.applutions.t2y.utils.ApiResponse.ApiResponseStatus
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.sheet_reschedule.*
import java.text.SimpleDateFormat
import java.util.*


class RescheduleRequestBottomSheet(rentalID: String, var bookingId: String, val code: String, val startDT: String) : BottomSheetDialogFragment() {


    val rentalId=rentalID
    var mViewModel: NotificationViewModel? = null
    private var selectedStartDate = ""
    private var selectedEndDate = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.sheet_reschedule, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setStyle(STYLE_NORMAL,R.style.BottomSheetDialogTheme)
    }
    override fun getTheme(): Int {
        return R.style.RoundedBottomSheetTheme
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel = ViewModelProvider(this).get(NotificationViewModel::class.java)
        txtEndTime.setOnClickListener {

            showTimePicker("end_time",txtEndTime)
            //dismiss()
        }
        txtStartTime.setOnClickListener {

            showTimePicker("start_time",txtStartTime)
            //dismiss()
        }
        btnRecheduleBooking.setOnClickListener {
            val selectedDates = calenderView.selectedDates

            if(selectedDates.isEmpty()){
                Toast.makeText(context,"Please select dates",Toast.LENGTH_LONG).show()
            }
            else {
                val dateStart = Date(selectedDates[0].timeInMillis)
                val dateEnd = Date(selectedDates[selectedDates.size.minus(1)].timeInMillis)

                val simpleDateFormat1 = SimpleDateFormat("yyyy-MM-dd", Locale.US)

                selectedStartDate = simpleDateFormat1.format(dateStart)
                selectedEndDate = simpleDateFormat1.format(dateEnd)

                if(txtStartTime.text=="Start Time"){
                    AnimUtils.shake(txtStartTime)
                    Toast.makeText(context,"Please select start time",Toast.LENGTH_LONG).show()
                }
                else if(txtEndTime.text=="End Time"){
                    AnimUtils.shake(txtEndTime)
                    Toast.makeText(context,"Please select end time",Toast.LENGTH_LONG).show()
                }
                else if(!HelperMethods.checktimings((selectedStartDate+" "+txtStartTime.text.toString()),(selectedEndDate+" "+txtEndTime.text.toString()))){
                    AnimUtils.shake(txtEndTime)
                    Toast.makeText(context,"End time should be after start time",Toast.LENGTH_LONG).show()
                }
                else{
                    if (startDT == "") {
                        mViewModel!!.rescheduleBooking(context, rentalId, bookingId, selectedStartDate + " " + txtStartTime.text.toString(), selectedEndDate + " " + txtEndTime.text.toString(), code)
                    }
                    else{
                        mViewModel!!.rescheduleBooking(context, rentalId, bookingId, startDT, selectedEndDate + " " + txtEndTime.text.toString(), code)
                    }
                    mViewModel!!.getrescheduleLiveData().observe(this, androidx.lifecycle.Observer { response: ApiResponse<BookingEditResponse> ->
                        val status = response.apiStatus

                        when (status) {
                            ApiResponseStatus.LOADING -> {
                                Toast.makeText(context, "Loading.....", Toast.LENGTH_SHORT).show()
                                Log.d("HERE","LOADING")
                            }
                            ApiResponseStatus.FAILED -> {
                                Toast.makeText(context, "Failed to reschedule booking", Toast.LENGTH_SHORT).show()
                                Log.d("HERE","FAILED")
                            }
                            ApiResponseStatus.SUCCESS -> if (response.data != null) {
                                dismiss()
                                Log.d("HERE","SUCCESS")
                                if (response.data.actionRequired == "payment") {
                                    val i = Intent(activity, BookActivity::class.java)
                                    i.putExtra("from", "reschedule")
                                    i.putExtra("item", response.data)
                                    startActivity(i)
                                }
                                else if (response.data.actionRequired == "none"){
                                    val i = Intent(activity, FinalActivity::class.java)
                                    i.putExtra("from","none")
                                    startActivity(i)
//
                                }
                                else if (response.data.actionRequired == "refund"){
                                    val i = Intent(activity, FinalActivity::class.java)
                                    i.putExtra("from","refund")
                                    startActivity(i)
                                }
                                else if (response.data.message == "Trailer already Booked for these Dates"){
                                    Toast.makeText(context, "Trailer not available for these dates.", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(context, "Couldn't fetch data, Please try again later!", Toast.LENGTH_SHORT).show()
                                dismiss()
                            }
                        }
                    })
                }
            }

        }

    }

    var picker: TimePickerDialog? = null
    private fun showTimePicker(flag: String, txtView: BoldTextView) {
        val cldr: Calendar = Calendar.getInstance()
        val hour: Int = cldr.get(Calendar.HOUR_OF_DAY)
        val minutes: Int = cldr.get(Calendar.MINUTE)
        picker = TimePickerDialog(context,
                OnTimeSetListener { tp, sHour, sMinute -> txtView.text = "$sHour:$sMinute" }, hour, minutes, false)
        picker!!.show()
    }

}