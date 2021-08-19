package com.applutions.t2y.ui.profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.applutions.t2y.R;
import com.applutions.t2y.common.HelperMethods;
import com.applutions.t2y.common.PermissionUtils;
import com.applutions.t2y.customViews.RegularTextView;
import com.applutions.t2y.data.response.login.signIn.DriverLicense;
import com.applutions.t2y.ui.auth.otp.VerifyOtpViewModel;
import com.applutions.t2y.ui.auth.signup.DrivingLicenseFragment;
import com.applutions.t2y.utils.ApiResponse;
import com.applutions.t2y.utils.Constants;
import com.applutions.t2y.utils.Utils;
import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.bumptech.glide.load.engine.DiskCacheStrategy.NONE;

public class EditProfileActivity extends AppCompatActivity implements OnMapReadyCallback, DrivingLicenseFragment.DrivingLicenseFragmentListener {

   // private ActivityEditProfileBinding binding;
    CircleImageView mProfile,mImgLicense;
    EditText mFullname,mEmail,mMobile,mDob;
    CardView mCardAddress,mCardLicense;
    Button mBtnChangePwd,mBtnVerifyEmail,mBtnVerifyMobile,mBtnSave,mBtnCancel;
    private ProfileViewModel mViewModel;

    //address
    JsonObject address=null;
    boolean isAddressChanged=false;
    JsonObject license=null;
    boolean isLicenseChanged=false;
    boolean isProfileChanged=false;
    //image file path
    String profileImagePath="";
    String houseNo;

    //address dialog
    String apiKey = "AIzaSyDhK8wA8WVQbEqcVZyAVaxYM1kaoIp2jys";
    PlacesClient placesClient;

    private GoogleMap mMap;
    SupportMapFragment mapFragment;
    BottomSheetDialog bottomSheetDialog;

    View bottomSheetView;
    RegularTextView mTxtAddress;
    EditText mTxtAddressLine,mEdtHomeNumber;
    LinearLayout mLytClose,mLytAdd;

    private UpdateProfileViewModel mUpdateViewModel;

    private VerifyOtpViewModel mVerifyOTPViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        initializeView();
        setListners();
        if (!Places.isInitialized()) {
            Places.initialize(this, apiKey);
            Log.d("Here",Boolean.toString(Places.isInitialized()));
        }
        placesClient = Places.createClient(this);

        mViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        mVerifyOTPViewModel = new ViewModelProvider(this).get(VerifyOtpViewModel.class);

        mUpdateViewModel = new ViewModelProvider(this).get(UpdateProfileViewModel.class);

        mViewModel.getDetails(this);

        mViewModel.getRentalLiveData().observe(this, response -> {
            ApiResponse.ApiResponseStatus status = response.getApiStatus();
            switch (status) {
                case LOADING:
                    Toast.makeText(this, "Loading.....", Toast.LENGTH_SHORT).show();
                    break;
                case FAILED:
                    Toast.makeText(this, "Failed request", Toast.LENGTH_SHORT).show();
                    break;
                case SUCCESS:
                    //Toast.makeText(this, "GOT DATA....", Toast.LENGTH_SHORT).show();
                    if(response.getData().getUserObj().isEmailVerified()){
                        mBtnVerifyEmail.setVisibility(View.GONE);
                    }
                    if(response.getData().getUserObj().isMobileVerified()){
                        mBtnVerifyMobile.setVisibility(View.GONE);
                    }
                    mFullname.setText(response.getData().getUserObj().getName());
                    mEmail.setText(response.getData().getUserObj().getEmail());
                    mMobile.setText(response.getData().getUserObj().getMobile());
                    mDob.setText(response.getData().getUserObj().getDob());

                    //get address object
                    address=new JsonObject();
                    houseNo = response.getData().getUserObj().getAddress().getText();
                    address.addProperty("text",response.getData().getUserObj().getAddress().getText());
                    address.addProperty("pincode",response.getData().getUserObj().getAddress().getPincode());
                    address.addProperty("city",response.getData().getUserObj().getAddress().getCity());
                    address.addProperty("state",response.getData().getUserObj().getAddress().getState());
                    address.addProperty("country",response.getData().getUserObj().getAddress().getCountry());
                    JsonArray jsonArray=new JsonArray();
                    jsonArray.add(response.getData().getUserObj().getAddress().getCoordinates().get(0));
                    jsonArray.add(response.getData().getUserObj().getAddress().getCoordinates().get(1));
                    address.add("coordinates",jsonArray);

                    //get license object
                    license=new JsonObject();
                    license.addProperty("card",response.getData().getUserObj().getDriverLicense().getCard());
                    license.addProperty("state",response.getData().getUserObj().getDriverLicense().getState());
                    license.addProperty("expiry",response.getData().getUserObj().getDriverLicense().getExpiry());
                    license.addProperty("url", response.getData().getUserObj().getDriverLicense().getScan().getData());


                    //mAddress.setText(response.getData().getUserObj().getAddress().getText());
                    Glide.with(this)
                            .load(response.getData().getUserObj().getPhoto().getData())
                            .diskCacheStrategy(NONE)
                            .into(mProfile);
                    //HelperMethods.downloadImage(response.getData().getUserObj().getDriverLicense().getScan(),this,mImgLicense);
                    Glide.with(this)
                            .load(response.getData().getUserObj().getDriverLicense().getScan().getData())
                            .diskCacheStrategy(NONE)
                            .into(mImgLicense);
                    break;
            }
        });


    }
    private void showBottomDialog() {
        bottomSheetDialog = new BottomSheetDialog(this,R.style.BottomSheetDialogTheme);
        BottomSheetBehavior behaviorField = bottomSheetDialog.getBehavior();
        behaviorField.setState(BottomSheetBehavior.STATE_EXPANDED);
        behaviorField.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING){
                    behaviorField.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {}
        });
        bottomSheetView = getLayoutInflater()
                .inflate(
                        R.layout.fragment_location_bottom_sheet,
                        (ConstraintLayout) findViewById(R.id.bottomSheetContainer)
                );

        mTxtAddress = bottomSheetView.findViewById(R.id.addressText);
        mTxtAddressLine = bottomSheetView.findViewById(R.id.landmarkET);
        mEdtHomeNumber = bottomSheetView.findViewById(R.id.houseNumberET);
        mLytAdd = bottomSheetView.findViewById(R.id.addAddressLayout);
        mLytClose = bottomSheetView.findViewById(R.id.closeDialog);

        mEdtHomeNumber.setText(houseNo.substring(0, houseNo.indexOf(",")));
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.ADDRESS_COMPONENTS, Place.Field.LAT_LNG));
        getMap();

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
               // Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());

                LatLng l = place.getLatLng();
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(l));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(l, 15));
                //Log.d("here",place.getLatLng().toString());
                getLocationFromLatLong(l);
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
               // Log.i(TAG, "An error occurred: " + status);
            }
        });

        mLytClose.setOnClickListener(v1 -> {
            bottomSheetDialog.cancel();
        });
        mLytAdd.setOnClickListener(v12 ->{
            bottomSheetDialog.cancel();
        });
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                getSupportFragmentManager().beginTransaction().remove(autocompleteFragment).remove(mapFragment).commit();
            }
        });
        bottomSheetDialog.show();
    }

    private void getLocationFromLatLong(LatLng l) {
        Geocoder geocoder = new Geocoder(EditProfileActivity.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(
                    l.latitude,
                    l.longitude,
                    1);
            if (addresses.size() == 0) {
                Utils.getErrorSnackBar(this, mBtnSave,"No address found").show();
                bottomSheetDialog.cancel();
                return;
            }
            Address address = addresses.get(0);
            mTxtAddress.setText(address.getAddressLine(0)+" "+address.getCountryName());
            mTxtAddressLine.setText(address.getAddressLine(0));

            JsonObject addObj=new JsonObject();
            addObj.addProperty("text",mEdtHomeNumber.getText()
                    .toString()+","+address.getAddressLine(0));

            addObj.addProperty("pincode",(address.getPostalCode()==null||address.getPostalCode()=="")?"":address.getPostalCode());
            addObj.addProperty("city",(address.getSubAdminArea()==null||address.getSubAdminArea()=="")?"":address.getSubAdminArea());
            addObj.addProperty("state",(address.getAdminArea()==null||address.getAdminArea()=="")?"":address.getAdminArea());
            addObj.addProperty("country",(address.getCountryName()==null||address.getCountryName()=="")?"":address.getCountryName());
            JsonArray jsonArray=new JsonArray();
            jsonArray.add(l.latitude);
            jsonArray.add(l.longitude);
            addObj.add("coordinates",jsonArray);

            //isAddressChanged = true;
            setUserAddress(addObj);
        } catch (IOException e) {
            e.printStackTrace();
            mTxtAddressLine.setText("");
            mTxtAddress.setText("");
            setUserAddress(null);
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if(address!=null&&mMap!=null) {
            LatLng l = new LatLng(address.getAsJsonArray("coordinates").get(0).getAsDouble(),address.getAsJsonArray("coordinates").get(1).getAsDouble());
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(l));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(l, 15));
            getLocationFromLatLong(l);
        }

    }
    void getMap(){
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    void setUserAddress(JsonObject addressObject1) {
        if(addressObject1!=null) {
            address = addressObject1;
            isAddressChanged = true;
        }
        else{
            address=null;
            isAddressChanged=false;
        }
    }
    private void setListners() {
        mProfile.setOnClickListener(view -> {
            showImageSelectionPopUp();
        });
        mDob.setOnClickListener(view -> {
            HelperMethods.showDatePicker(mDob,EditProfileActivity.this);
        });
        mCardAddress.setOnClickListener(view -> {
            showBottomDialog();
        });
        mCardLicense.setOnClickListener(view -> {
            BottomSheetDialogFragment drivingLicenseFragment = new DrivingLicenseFragment(license);
            drivingLicenseFragment.show(getSupportFragmentManager(), drivingLicenseFragment.getTag());
        });
        mBtnChangePwd.setOnClickListener(view -> {
            new ChangePasswordBottomSheet("").show(getSupportFragmentManager(), "");
        });
        mBtnVerifyEmail.setOnClickListener(view -> {
            mUpdateViewModel.sendVerifyLink(this,mEmail.getText().toString());
            mUpdateViewModel.getVerifyLinkListner().observe(this, response -> {
                ApiResponse.ApiResponseStatus status = response.getApiStatus();
                switch (status) {
                    case LOADING:
                        Toast.makeText(this, "Loading.....", Toast.LENGTH_SHORT).show();
                        break;
                    case FAILED:
                        Toast.makeText(this, "Failed request", Toast.LENGTH_SHORT).show();
                        break;
                    case SUCCESS:
                        Toast.makeText(this, "Email verify link sent Successfully! ", Toast.LENGTH_SHORT).show();
                        break;
                }
            });
        });
        mBtnVerifyMobile.setOnClickListener(view -> {
            mVerifyOTPViewModel.resendOtp(this,mMobile.getText().toString(), address.get("country").getAsString());
            mVerifyOTPViewModel.getResendOtpListener().observe(this, response -> {
                ApiResponse.ApiResponseStatus status = response.getApiStatus();
                switch (status) {
                    case FAILED:
                        Toast.makeText(this, response.getErrorMessage().message(), Toast.LENGTH_SHORT).show();
                        break;
                    case LOADING:
                        Toast.makeText(this, "Loading.....", Toast.LENGTH_SHORT).show();
                        break;
                    case SUCCESS:
                        Toast.makeText(this, "OTP sent...", Toast.LENGTH_SHORT).show();
                        //Utils.getSuccessSnackBar(this, mBtnVerifyMobile, "OTP sent...").show();
                        new VerifyOTPBottomSheet(mMobile.getText().toString(), address.get("country").getAsString()).show(getSupportFragmentManager(), "");
                        break;
                }
            });
        });
        mBtnSave.setOnClickListener(view -> {
            try {
                if(isValidInputs()){
                    saveDetails();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        });
        mBtnCancel.setOnClickListener(view -> {
            finish();
        });
    }


    private void saveDetails() {
        //SignUpUserModel signUpData = getUserSignUpData();
        if(isLicenseChanged) {
            if (mUpdateViewModel.getLicense() == null) {
                Toast.makeText(this, "LICENSE DATA NOT SET..", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("email", mEmail.getText().toString());
        //jsonObject.addProperty("password", mEmail.getText().toString());
        jsonObject.addProperty("mobile",mMobile.getText().toString());
        jsonObject.addProperty("name", mFullname.getText().toString());
        jsonObject.addProperty("dob", mDob.getText().toString());

        if(isAddressChanged||address!=null) {
            jsonObject.add("address", address);
        }
        if(isLicenseChanged){
            jsonObject.add("driverLicense",license);
        }

        mUpdateViewModel.setmBitmap(((BitmapDrawable)mProfile.getDrawable()).getBitmap());

        mUpdateViewModel.UpdateProfile(this, jsonObject);
        mUpdateViewModel.getUpdateListener().observe(this, response -> {
            ApiResponse.ApiResponseStatus status = response.getApiStatus();
            switch (status) {
                case LOADING:
                    Toast.makeText(this, "Loading.....", Toast.LENGTH_SHORT).show();
                    break;
                case FAILED:
                    Toast.makeText(this, "Failed request", Toast.LENGTH_SHORT).show();
                    break;
                case SUCCESS:
                    Toast.makeText(this, "Profile updated Successfully! ", Toast.LENGTH_SHORT).show();
                   finish();
                    break;
            }
        });
    }

    private boolean isValidInputs() throws ParseException {
        if(mFullname.getText().toString().isEmpty()){
            mFullname.requestFocus();
            HelperMethods.showToastbar(this,"Please enter full name");
            return false;
        }

        if(mEmail.getText().toString().isEmpty()){
            mEmail.requestFocus();
            HelperMethods.showToastbar(this,"Please enter email");
            return false;
        }
        if(!HelperMethods.isEmailValid(mEmail.getText().toString())){
            mEmail.requestFocus();
            HelperMethods.showToastbar(this,"Please enter valid email");
            return false;
        }
        if(mMobile.getText().toString().isEmpty()){
            mMobile.requestFocus();
            HelperMethods.showToastbar(this,"Please enter mobile");
            return false;
        }
        if(mMobile.getText().toString().length()<10){
            mMobile.requestFocus();
            HelperMethods.showToastbar(this,"Please enter valid mobile number");
            return false;
        }
        if(mDob.getText().toString().isEmpty()){
            mDob.requestFocus();
            HelperMethods.showToastbar(this,"Please enter date of birth");
            return false;
        }
        SimpleDateFormat df =new  SimpleDateFormat("yyyy-MM-dd");
        Date birthdate = df.parse(mDob.getText().toString());
        if (HelperMethods.calculateAge(birthdate)<17) {
            mDob.requestFocus();
            HelperMethods.showToastbar(this,"Your age must be above 16 years");
            return false;
        }

        return true;
    }

    private void initializeView() {
        mProfile=findViewById(R.id.imgProfile);
        mImgLicense=findViewById(R.id.imgLicense);
        mFullname=findViewById(R.id.fullnameET);
        mEmail=findViewById(R.id.emailET);
        mMobile=findViewById(R.id.mobileNumberET);
        mDob=findViewById(R.id.dobET);
        mCardAddress=findViewById(R.id.cardAddress);
        mCardLicense=findViewById(R.id.cardLicense);
        mBtnChangePwd=findViewById(R.id.changePwdBtn);
        mBtnVerifyEmail=findViewById(R.id.verifyEmailBtn);
        mBtnVerifyMobile=findViewById(R.id.verifyMobileBtn);
        mBtnSave=findViewById(R.id.saveBtn);
        mBtnCancel=findViewById(R.id.cancelBtn);

        HelperMethods.disableEditText(mDob);
    }

    private void  showImageSelectionPopUp() {
        if (PermissionUtils.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            if (PermissionUtils.hasPermissions(this, Manifest.permission.CAMERA)) {
                showMenuOption(mProfile);
            } else
                PermissionUtils.requestPermissions(
                        this,
                        new String[]{Manifest.permission.CAMERA},
                        PermissionUtils.CAMERA_ACCESS
                );
        } else
            PermissionUtils.requestPermissions(
                    this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PermissionUtils.READ_EXTERNAL_STORAGE
            );
    }

    private void showMenuOption(CircleImageView mImg) {
        PopupMenu popupMenu = new PopupMenu(this, mImg);
        popupMenu.inflate(R.menu.menu_item);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.camera_selection:
                        ImagePicker.Companion.with(EditProfileActivity.this)
                                .cameraOnly()
                                .start();
                        break;
                    case R.id.gallery_selection:
                        ImagePicker.Companion.with(EditProfileActivity.this)
                                .galleryOnly()
                                .start();
                        break;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionUtils.READ_EXTERNAL_STORAGE) {
            if (PermissionUtils.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                if (PermissionUtils.hasPermissions(this, Manifest.permission.CAMERA)) {
                    showMenuOption(mProfile);
                } else
                    PermissionUtils.requestPermissions(
                            this,
                            new String[]{Manifest.permission.CAMERA},
                            PermissionUtils.CAMERA_ACCESS
                    );
            } else
                PermissionUtils.requestPermissions(
                        this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PermissionUtils.READ_EXTERNAL_STORAGE
                );
        }
        else  if (requestCode == PermissionUtils.CAMERA_ACCESS) {
            if (PermissionUtils.hasPermissions(this, Manifest.permission.CAMERA)) {
                showMenuOption(mProfile);
            } else
                PermissionUtils.requestPermissions(
                        this,
                        new String[]{Manifest.permission.CAMERA},
                        PermissionUtils.CAMERA_ACCESS
                );
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.LOCATION_RESOLUTION_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
               // locationBottomSheet.onActivityResult(requestCode, resultCode, data);
            }
               // showToast("Location Permission Cancelled");
        }
        else if  (requestCode == 2404) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                //Image Uri will not be null for RESULT_OK
                Uri fileUri = data.getData();
                String filePath = ImagePicker.Companion.getFilePath(data);
                profileImagePath = filePath;
                isProfileChanged = true;
                Glide.with(this)
                        .load(fileUri)
                        .into(mProfile);
                        //.apply(RequestOptions.circleCropTransform())

                //binding.icon.setVisibility(View.GONE);
                //binding.ringView.setVisibility(View.GONE);
            }
            else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(this, ImagePicker.Companion.getError(data), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void selectDrivingLicense(DriverLicense license1, String licenseUriString) {
        mUpdateViewModel.setLicense(license1);
        mUpdateViewModel.setLicenseUri(licenseUriString);
        //get license object
        license=new JsonObject();
        license.addProperty("card",license1.getCard());
        license.addProperty("state",license1.getState());
        license.addProperty("expiry",license1.getExpiry());

        isLicenseChanged=true;

    }
}