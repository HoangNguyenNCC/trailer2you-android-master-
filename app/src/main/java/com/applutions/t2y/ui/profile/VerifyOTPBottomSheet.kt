package com.applutions.t2y.ui.profile

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.applutions.t2y.R
import com.applutions.t2y.data.response.login.otp.VerifyMobileResponse
import com.applutions.t2y.ui.auth.otp.VerifyOtpViewModel
import com.applutions.t2y.utils.ApiResponse
import com.applutions.t2y.utils.ApiResponse.ApiResponseStatus
import com.applutions.t2y.utils.Utils
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.sheet_verify_otp.view.*

class VerifyOTPBottomSheet(mobile: String, country: String) : BottomSheetDialogFragment() {


    val mobile=mobile
    val country=country

    private var mViewModel: VerifyOtpViewModel? = null
    lateinit var mView:View
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView=inflater.inflate(R.layout.sheet_verify_otp, container, false)
        mViewModel = ViewModelProvider(requireActivity()).get(VerifyOtpViewModel::class.java)

        return mView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme);
    }
    override fun getTheme(): Int {
        return R.style.RoundedBottomSheetTheme
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mView.otp_view.requestFocusOTP()
        // mViewModel!!.resendOtp(requireActivity(), mobile, country)
        mView.btnCancel.setOnClickListener { dismiss() }
        mView.btn_confirm_number.setOnClickListener{ v ->
            val otp: String = mView.otp_view.otp
            if (TextUtils.isEmpty(otp)) {
                mView.otp_view.showError()
                Utils.getErrorSnackBar(requireActivity(), mView.otp_view, "Invalid OTP").show()
                return@setOnClickListener
            }
            mViewModel!!.verifyPhoneNumber(requireActivity(), mobile, country, otp)
        }
        mView.tv_reset_otp.setOnClickListener{ v ->
            mViewModel!!.resendOtp(requireActivity(), mobile, country)
        }

        mViewModel!!.verifyMobileListener.observe(requireActivity(), Observer { response: ApiResponse<VerifyMobileResponse?> ->
            val status = response.apiStatus
            when (status) {
                ApiResponseStatus.FAILED -> {
                    mView.btn_confirm_number.revertAnimation()
                    Utils.getErrorSnackBar(requireActivity(), mView.otp_view, response.errorMessage.message())
                }
                ApiResponseStatus.LOADING -> mView.btn_confirm_number.startAnimation()
                ApiResponseStatus.SUCCESS -> {
                    mView.btn_confirm_number.revertAnimation()
                    dismiss()
                }
            }
        })

        mViewModel!!.resendOtpListener.observe(requireActivity(), Observer { response: ApiResponse<VerifyMobileResponse?> ->
            val status = response.apiStatus
            when (status) {
                ApiResponseStatus.FAILED -> {
                    mView.btn_confirm_number.revertAnimation()
                    Utils.getErrorSnackBar(requireActivity(), mView.otp_view, response.errorMessage.message())
                }
                ApiResponseStatus.LOADING ->mView.btn_confirm_number.startAnimation()
                ApiResponseStatus.SUCCESS -> {
                    mView.btn_confirm_number.revertAnimation()
                    Utils.getSuccessSnackBar(requireActivity(), mView.otp_view, "OTP sent...").show()
                }
            }
        })
        /*btnCancel.setOnClickListener {
            dismiss()
        }

        btnUpdate.setOnClickListener {
            if(isValidInputs()) {
                val apiService = RetrofitInstance.getRetrofitInstance(context).create(ApiService::class.java)
                val resetPasswordBody = ChangePasswordBody(edtoldPwd.text.toString(), edtNewPwd.text.toString());
                val resetPasswordBodyCall: Call<ResetPasswordResponse> = apiService.changePassword(resetPasswordBody)
                resetPasswordBodyCall.enqueue(object : Callback<ResetPasswordResponse?> {
                    override fun onResponse(call: Call<ResetPasswordResponse?>, response: Response<ResetPasswordResponse?>) {
                        if (response.isSuccessful) {
                            HelperMethods.showToastbar(context, ApiResponse.getSuccessResponse(response.message()).toString()+", Please login again!")
                            SharedPrefs.getInstance(requireContext()).setAuthToken("")
                            startActivity(Intent(activity, AuthActivity::class.java))
                            dismiss()

                            // passwordResetLiveData.postValue(ApiResponse.getSuccessResponse(response.body()))
                        } else {
                            val error = ErrorUtils.parseError(context, response)
                            HelperMethods.showToastbar(context,ApiResponse.getFailureResponse<ResetPasswordResponse>(error).errorMessage.message())
                            //passwordResetLiveData.postValue(ApiResponse.getFailureResponse<ResetPasswordResponse>(error))
                        }
                    }

                    override fun onFailure(call: Call<ResetPasswordResponse?>, t: Throwable) {
                        HelperMethods.showToastbar(context, ApiResponse.getFailureResponse<PasswordResetLinkResponse>(ApiError("Error resetting password")).toString())

                        //passwordResetLinkLiveData.postValue(ApiResponse.getFailureResponse<PasswordResetLinkResponse>(ApiError("Error resetting password")))
                    }
                })
            }
        }*/

    }

    /*private fun isValidInputs(): Boolean {
        if(edtoldPwd.text.toString().isEmpty()){
            HelperMethods.showToastbar(context,"Please enter old password")
            edtoldPwd.requestFocus()
            return false
        }
        if(edtNewPwd.text.toString().isEmpty()){
            HelperMethods.showToastbar(context,"Please enter new password")
            edtNewPwd.requestFocus()
            return false
        }
        if(edtConfimPwd.text.toString().isEmpty()){
            HelperMethods.showToastbar(context,"Please enter confirm password")
            edtConfimPwd.requestFocus()
            return false
        }
        if(edtConfimPwd.text.toString()!=edtNewPwd.text.toString()){
            HelperMethods.showToastbar(context,"Please match confirm password")
            edtConfimPwd.requestFocus()
            return false
        }
        return true
    }*/
}