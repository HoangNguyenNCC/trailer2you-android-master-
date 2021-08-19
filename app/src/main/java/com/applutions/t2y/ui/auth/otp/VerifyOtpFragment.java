package com.applutions.t2y.ui.auth.otp;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.applutions.t2y.R;
import com.applutions.t2y.data.response.login.otp.VerifyMobileResponse;
import com.applutions.t2y.databinding.FragmentDrivingLicenseBinding;
import com.applutions.t2y.databinding.FragmentVerifyOtpBinding;
import com.applutions.t2y.utils.ApiResponse;
import com.applutions.t2y.utils.Utils;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class VerifyOtpFragment extends Fragment {

    private FragmentVerifyOtpBinding binding;
    private VerifyOtpViewModel mViewModel;
    private VerifyOtpFragmentArgs args;
    private NavController navController;

    public VerifyOtpFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Window window = getActivity().getWindow();
        Drawable bg = getActivity().getDrawable(R.color.white);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getActivity().getColor(android.R.color.transparent));
        window.setBackgroundDrawable(bg);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentVerifyOtpBinding.inflate(inflater, container, false);
        args = VerifyOtpFragmentArgs.fromBundle(requireArguments());

        mViewModel = new ViewModelProvider(requireActivity()).get(VerifyOtpViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        navController = Navigation.findNavController(view);
        binding.otpView.requestFocusOTP();

        String mobile1 = args.getMobile();
        String country1 = args.getCountry();
        mViewModel.resendOtp(requireActivity(),mobile1, country1);

        binding.btnConfirmNumber.setOnClickListener(v -> {
            String otp = binding.otpView.getOTP();
            if (TextUtils.isEmpty(otp)) {
                binding.otpView.showError();
                Utils.getErrorSnackBar(requireActivity(), binding.otpView, "Invalid OTP").show();
                return;
            }
            String mobile = args.getMobile();
            String country = args.getCountry();
            mViewModel.verifyPhoneNumber(requireActivity(), mobile, country, otp);
        });
        binding.tvResetOtp.setOnClickListener(v -> {
            String mobile = args.getMobile();
            String country = args.getCountry();
            mViewModel.resendOtp(requireActivity(),mobile, country);
        });

        mViewModel.getVerifyMobileListener().observe(requireActivity(), response -> {
            ApiResponse.ApiResponseStatus status = response.getApiStatus();
            switch (status) {
                case FAILED:
                    binding.btnConfirmNumber.revertAnimation();
                    Utils.getErrorSnackBar(requireActivity(), binding.otpView, response.getErrorMessage().message());
                    break;
                case LOADING:
                    binding.btnConfirmNumber.startAnimation();
                    break;
                case SUCCESS:
                    binding.btnConfirmNumber.revertAnimation();
                    navController.navigate(R.id.action_verifyOtpFragment_to_signIn);
            }
        });

        mViewModel.getResendOtpListener().observe(requireActivity(), response -> {
            ApiResponse.ApiResponseStatus status = response.getApiStatus();
            switch (status) {
                case FAILED:
                    binding.btnConfirmNumber.revertAnimation();
                    Utils.getErrorSnackBar(requireActivity(), binding.otpView, response.getErrorMessage().message());
                    break;
                case LOADING:
                    binding.btnConfirmNumber.startAnimation();
                    break;
                case SUCCESS:
                    binding.btnConfirmNumber.revertAnimation();
                    Utils.getSuccessSnackBar(requireActivity(), binding.otpView, "OTP sent...").show();
                    break;
            }
        });

    }
}