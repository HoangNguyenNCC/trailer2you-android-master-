package com.applutions.t2y.ui.auth.forgotpassword;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.applutions.t2y.R;
import com.applutions.t2y.databinding.FragmentResetLinkBinding;
import com.applutions.t2y.utils.ApiResponse;
import com.applutions.t2y.utils.Utils;

import org.jetbrains.annotations.NotNull;

public class SendResetLinkFragment extends Fragment {

    private FragmentResetLinkBinding binding;
    private ForgotPasswordViewModel mViewModel;
    private NavController navController;

    public SendResetLinkFragment() {}

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
        binding = FragmentResetLinkBinding.inflate(inflater, container, false);
        mViewModel = new ViewModelProvider(requireActivity()).get(ForgotPasswordViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        navController = Navigation.findNavController(view);
        binding.btnResetLink.setOnClickListener(v -> {
            String email = binding.emailIdET.getText().toString().trim();
            if (!Utils.validateEmail(email)) {
                binding.emailIdET.setError("Invalid email");
                return;
            }
            mViewModel.sendPasswordResetLink(requireContext(), email);
        });

        binding.tvIRemember.setOnClickListener(v -> navController.navigate(R.id.action_forgotPasswordFragment_to_signIn));

        mViewModel.getResetLinkListener().observe(requireActivity(), response -> {
            ApiResponse.ApiResponseStatus status = response.getApiStatus();
            switch (status) {
                case SUCCESS:
                    binding.btnResetLink.revertAnimation();
                    navController.navigate(R.id.action_forgotPasswordFragment_to_forgotPasswordFragment2);
                    break;
                case FAILED:
                    binding.btnResetLink.revertAnimation();
                    Utils.getErrorSnackBar(requireActivity(), binding.btnResetLink ,response.getErrorMessage().message()).show();
                    break;
                case LOADING:
                    Toast.makeText(requireActivity(), "LOADING....", Toast.LENGTH_SHORT).show();
                    binding.btnResetLink.startAnimation();
            }
        });
    }


}
