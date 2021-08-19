package com.applutions.t2y.ui.profile

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.applutions.t2y.R
import com.applutions.t2y.common.HelperMethods
import com.applutions.t2y.data.network.ApiError
import com.applutions.t2y.data.network.ApiService
import com.applutions.t2y.data.network.RetrofitInstance
import com.applutions.t2y.data.response.ChangePasswordBody
import com.applutions.t2y.data.response.login.forgotPassword.PasswordResetLinkResponse
import com.applutions.t2y.data.response.login.forgotPassword.ResetPasswordResponse
import com.applutions.t2y.ui.auth.AuthActivity
import com.applutions.t2y.utils.ApiResponse
import com.applutions.t2y.utils.ErrorUtils
import com.applutions.t2y.utils.SharedPrefs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.sheet_change_pwd.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePasswordBottomSheet(flag: String) : BottomSheetDialogFragment() {

    val tempStr=flag
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.sheet_change_pwd, container, false)
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

        btnCancel.setOnClickListener {
            dismiss()
        }

        btnUpdate.setOnClickListener {
            if(isValidInputs()) {
                val apiService = RetrofitInstance.getRetrofitInstance(context).create(ApiService::class.java)
                val resetPasswordBody = ChangePasswordBody(edtoldPwd.text.toString(), edtNewPwd.text.toString())
                val resetPasswordBodyCall: Call<ResetPasswordResponse> = apiService.changePassword(resetPasswordBody)
                resetPasswordBodyCall.enqueue(object : Callback<ResetPasswordResponse?> {
                    override fun onResponse(call: Call<ResetPasswordResponse?>, response: Response<ResetPasswordResponse?>) {
                        if (response.isSuccessful) {
                            HelperMethods.showToastbar(context, ApiResponse.getSuccessResponse(response.message()).toString()+", Please login again!")
                            SharedPrefs.getInstance(requireContext()).authToken = ""
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
        }

    }

    private fun isValidInputs(): Boolean {
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
    }
}