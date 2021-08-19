package com.applutions.t2y.ui.auth;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.applutions.t2y.R;
import com.applutions.t2y.data.response.login.signIn.DriverLicense;
import com.applutions.t2y.data.response.login.signUp.SignUpUserModel;
import com.applutions.t2y.ui.auth.signup.DrivingLicenseFragment;
import com.applutions.t2y.ui.auth.signup.SignUp;
import com.applutions.t2y.utils.ApiResponse;
import com.applutions.t2y.utils.Constants;
import com.github.dhaval2404.imagepicker.ImagePicker;

public class AuthActivity extends AppCompatActivity implements DrivingLicenseFragment.DrivingLicenseFragmentListener {

    private static final String TAG = "AuthActivity";
    private AuthViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        mViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
    }


    @Override
    public void selectDrivingLicense(DriverLicense license, String licenseUriString) {
        Log.v(TAG, license.toString()+licenseUriString);
        mViewModel.setLicense(license);
        mViewModel.setLicenseUri(licenseUriString);
        SignUp.updateDrivingLicenseLabel(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.IMAGE_PICK) {
            Uri fileUri = data.getData();
            String filePath = ImagePicker.Companion.getFilePath(data);//"/storage/emulated/0/DCIM/1594823279508.jpg";
            mViewModel.setImageUri(filePath);
            SignUp.updateProfileImage(fileUri);
        }
    }
}