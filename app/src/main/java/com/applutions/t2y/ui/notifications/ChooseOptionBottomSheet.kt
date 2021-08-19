package com.applutions.t2y.ui.notifications

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.applutions.t2y.R
import com.applutions.t2y.ui.tracking.TrackingActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.sheet_change_pwd.btnCancel
import kotlinx.android.synthetic.main.sheet_choose_option.*

class ChooseOptionBottomSheet(invoiceId: String) : BottomSheetDialogFragment() {

    val invoiceId=invoiceId
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.sheet_choose_option, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }
    override fun getTheme(): Int {
        return R.style.RoundedBottomSheetTheme
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnStartTracking.setOnClickListener {
            var intent=Intent(context,TrackingActivity::class.java)
            intent.putExtra("invoiceId",invoiceId)
            startActivity(intent)
            dismiss()
        }
        btnCancel.setOnClickListener {
            dismiss()
        }
        btnViewDetails.setOnClickListener {
            var intent=Intent(context,BookingDetailsActivity::class.java)
            intent.putExtra("invoiceId",invoiceId)
            startActivity(intent)
            dismiss()
        }

    }

}