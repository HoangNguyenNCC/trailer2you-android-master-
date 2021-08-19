package com.applutions.t2y.ui.auth.signin;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.applutions.t2y.BottomNavActivity;
import com.applutions.t2y.R;
import com.applutions.t2y.data.response.login.signIn.SignInObj;
import com.applutions.t2y.databinding.FragmentSignInBinding;
import com.applutions.t2y.utils.ApiResponse;
import com.applutions.t2y.utils.SharedPrefs;
import com.applutions.t2y.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SignIn extends Fragment {
    private FragmentSignInBinding binding;
    private NavController navController;
    private SignInViewModel signInViewModel;
    private String TAG = "SignIn";

    public SignIn() {
        // Required empty public constructor
    }

    private String token="";
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Window window = requireActivity().getWindow();
        Drawable bg = ContextCompat.getDrawable(getContext(), R.drawable.truck_login);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getActivity().getColor(android.R.color.transparent));
        window.setBackgroundDrawable(bg);
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        binding.signInButton.setOnClickListener(v -> {
            Boolean isInputVerified = verifyInput();
            if (isInputVerified) {
                FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        token=task.getResult();
                        String email = binding.emailIdET.getText().toString().trim();
                        String password = binding.passwordET.getText().toString().trim();
                        signInViewModel.signInUser(requireActivity(),new SignInObj(email, password, token));
                    }
                });

            }
        });

        binding.createAccountBtn.setOnClickListener(v -> navController.navigate(R.id.action_signIn_to_signUp));
        binding.tvForgotPassword.setOnClickListener(v -> navController.navigate(R.id.action_signIn_to_forgotPasswordFragment));

        signInViewModel.getLoginApiListener().observe(getViewLifecycleOwner(), stringApiResponse -> {
            ApiResponse.ApiResponseStatus apiStatus = stringApiResponse.getApiStatus();
            switch (apiStatus) {
                case FAILED:
                    binding.signInButton.revertAnimation();
                    binding.signInButton.setDrawableBackground(getResources().getDrawable(R.drawable.dark_rounded_button));
                    Utils.getErrorSnackBar(requireActivity(), binding.signInButton, stringApiResponse.getErrorMessage().message()).show();
                    break;
                case SUCCESS:
                    binding.signInButton.revertAnimation();
                    String token = stringApiResponse.getData().getDataObj().getToken();
                    SharedPrefs.getInstance(requireContext()).setAuthToken(token);
                    Intent intent = new Intent(requireActivity(), BottomNavActivity.class);
                    startActivity(intent);
                    requireActivity().finish();

                    break;
                case LOADING:
                    binding.signInButton.startAnimation();
                    break;
            }
        });
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSignInBinding.inflate(inflater, container, false);
        signInViewModel = new ViewModelProvider(this).get(SignInViewModel.class);
        return binding.getRoot();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private Boolean verifyInput() {
        String emailId = binding.emailIdET.getText().toString();
        String password = binding.passwordET.getText().toString();
        if (!isValidEmail(emailId)) {
            binding.emailIdET.setError("Enter Valid Email Id !");
            return false;
        }
        if (!isValidPassword(password) && password.length() < 8) {
            binding.passwordET.setError("Enter Valid Password !");
            return false;
        }
        return true;
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public static boolean isValidPassword(final String password) {
        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);
        return matcher.matches();
    }


}