package com.applutions.t2y.ui.auth.forgotpassword;

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
import com.applutions.t2y.data.response.login.forgotPassword.ResetPasswordResponse;
import com.applutions.t2y.databinding.FragmentForgotPasswordBinding;
import com.applutions.t2y.utils.ApiResponse;
import com.applutions.t2y.utils.Utils;

import org.jetbrains.annotations.NotNull;

public class ForgotPasswordFragment extends Fragment {

    private FragmentForgotPasswordBinding binding;
    private ForgotPasswordViewModel mViewModel;
    private NavController navController;

    public ForgotPasswordFragment() {
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
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentForgotPasswordBinding.inflate(inflater, container, false);
        mViewModel = new ViewModelProvider(requireActivity()).get(ForgotPasswordViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        navController = Navigation.findNavController(view);
        binding.btnResetPassword.setOnClickListener(v -> {
            String token = binding.edtToken.getText().toString().trim();
            String password = binding.edtPassword.getText().toString().trim();

            if (TextUtils.isEmpty(token)) {
                binding.edtToken.setError("Invalid Token");
                return;
            }

            if (TextUtils.isEmpty(password) || password.length() < 8) {
                binding.edtPassword.setError("Invalid Password");
                return;
            }

            mViewModel.resetPassword(requireActivity(),token, password);
        });

        mViewModel.getResetPasswordListener().observe(requireActivity(), response -> {
            ApiResponse.ApiResponseStatus status = response.getApiStatus();
            switch (status) {
                case LOADING:
                    Toast.makeText(requireActivity(), "Reset Password....", Toast.LENGTH_SHORT).show();
                    binding.btnResetPassword.startAnimation();
                    break;
                case SUCCESS:
                    binding.btnResetPassword.revertAnimation();
                    navController.navigate(R.id.action_forgotPasswordFragment2_to_signIn);
                    break;
                case FAILED:
                    binding.btnResetPassword.revertAnimation();
                    Utils.getErrorSnackBar(requireActivity(), binding.btnResetPassword, response.getErrorMessage().message()).show();
            }
        });
    }
}