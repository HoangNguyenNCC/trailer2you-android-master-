package com.applutions.t2y.ui.auth.signup;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.applutions.t2y.R;
import com.applutions.t2y.common.HelperMethods;
import com.applutions.t2y.common.PermissionUtils;
import com.applutions.t2y.customViews.RegularTextView;
import com.applutions.t2y.data.response.login.signIn.DriverLicense;
import com.applutions.t2y.data.response.login.signUp.SignUpUserModel;
import com.applutions.t2y.databinding.FragmentSignUpBinding;
import com.applutions.t2y.ui.auth.AuthViewModel;
import com.applutions.t2y.utils.ApiResponse;
import com.applutions.t2y.utils.Constants;
import com.applutions.t2y.utils.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.applutions.t2y.ui.auth.signin.SignIn.isValidEmail;
import static com.applutions.t2y.ui.auth.signin.SignIn.isValidPassword;


public class SignUp extends Fragment implements OnMapReadyCallback {

    public static FragmentSignUpBinding binding;
    private NavController navController;
    String state = "";

    private static int PICK_IMG = 3;
    private DriverLicense license;
    private String licenseUri;
    private static final String TAG = "SignUpFragment";
//    private SignUpListener mListener;
    private AuthViewModel mViewModel;
    private String imageUriString;
    private Uri imageUri;
    Address address;

    public SignUp() {
        // Required empty public constructor
    }

    public static void updateDrivingLicenseLabel(boolean b) {
        if (b)
            binding.addLicenseButton.setText("Driving License Added");
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
        binding = FragmentSignUpBinding.inflate(inflater, container, false);
        mViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        return binding.getRoot();
    }

    String apiKey = "AIzaSyDhK8wA8WVQbEqcVZyAVaxYM1kaoIp2jys";
    PlacesClient placesClient;

    private GoogleMap mMap;
    SupportMapFragment mapFragment;
    BottomSheetDialog bottomSheetDialog;

    View bottomSheetView;
    RegularTextView mTxtAddress;
    EditText mTxtAddressLine,mEdtHomeNumber;
    LinearLayout mLytClose,mLytAdd;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        navController = Navigation.findNavController(view);

        HelperMethods.disableEditText(binding.addressEt);
        HelperMethods.disableEditText(binding.dobET);
        if (!Places.isInitialized()) {
            Places.initialize(binding.getRoot().getContext(), apiKey);
            Log.d("Here",Boolean.toString(Places.isInitialized()));
        }
        placesClient = Places.createClient(binding.getRoot().getContext());

        binding.signUpButton.setOnClickListener(v -> {
            try {
                if (!verifyInputs()) return;
            } catch (ParseException e) {
                e.printStackTrace();
            }
            SignUpUserModel signUpData = getUserSignUpData();
            if (mViewModel.getLicense() == null) {
                Toast.makeText(requireActivity(), "Please enter License details", Toast.LENGTH_SHORT).show();
                return;
            }
            if (mViewModel.getProfileImageUri() == null) {
                Toast.makeText(requireActivity(), "Profile Image not set", Toast.LENGTH_SHORT).show();
                return;
            }
            mViewModel.signUpUser(requireActivity(), signUpData);
        });
        binding.alreadyHaveAccountBtn.setOnClickListener(v -> navController.navigate(R.id.action_signUp_to_signIn));
        binding.userProfileImageView.setOnClickListener(v -> {
            getImg();

        });
        binding.addLicenseButton.setOnClickListener(v -> {
            BottomSheetDialogFragment drivingLicenseFragment;
            if (mViewModel.getLicense()!=null)
                drivingLicenseFragment = new DrivingLicenseFragment(mViewModel.getLicense(), mViewModel.getLicenseUriString());
            else
                drivingLicenseFragment = new DrivingLicenseFragment();
            drivingLicenseFragment.show(requireActivity().getSupportFragmentManager(), drivingLicenseFragment.getTag());
        });

        binding.addressEt.setOnClickListener(v-> {
            showBottomDialog();
            //BottomSheetDialogFragment locationBottomSheetFragment = new LocationBottomSheetFragment("");
            //locationBottomSheetFragment.show(requireActivity().getSupportFragmentManager(), locationBottomSheetFragment.getTag());
        });
        binding.dobET.setOnClickListener(v-> {

            HelperMethods.showDatePicker(binding.dobET, getActivity());
        });
        mViewModel.getSignUpListener().observe(requireActivity(), apiResponse -> {
            ApiResponse.ApiResponseStatus status = apiResponse.getApiStatus();
            if (status == ApiResponse.ApiResponseStatus.LOADING) {
                binding.signUpButton.startAnimation();
            } else if (status == ApiResponse.ApiResponseStatus.FAILED) {
                binding.signUpButton.revertAnimation();
                Utils.getErrorSnackBar(requireActivity(), binding.signUpButton, apiResponse.getErrorMessage().message()).show();
            } else {
                binding.signUpButton.revertAnimation();
                String mobile = (binding.mobileNumberET.getText().toString().contains("+61"))?binding.mobileNumberET.getText().toString().substring(3):binding.mobileNumberET.getText().toString();
                String country = address.getCountryName();
                SignUpDirections.ActionSignUpToVerifyOtpFragment direction = SignUpDirections.actionSignUpToVerifyOtpFragment(mobile, country);
                navController.navigate(direction);
            }
        });


    }
    JsonObject tempAddress=new JsonObject();
    private void showBottomDialog() {

        bottomSheetDialog = new BottomSheetDialog(binding.getRoot().getContext(),R.style.BottomSheetDialogTheme);
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
                        (ConstraintLayout) binding.getRoot().findViewById(R.id.bottomSheetContainer)
                );

        mTxtAddress = bottomSheetView.findViewById(R.id.addressText);
        mTxtAddressLine = bottomSheetView.findViewById(R.id.landmarkET);
        mEdtHomeNumber = bottomSheetView.findViewById(R.id.houseNumberET);
        mLytAdd = bottomSheetView.findViewById(R.id.addAddressLayout);
        mLytClose = bottomSheetView.findViewById(R.id.closeDialog);
        //findViewsByID(1);
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.ADDRESS_COMPONENTS, Place.Field.LAT_LNG));
        getMap();

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
                LatLng l = place.getLatLng();
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(l).title("Marker in"+place.getName()));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(l));
                Log.d("here",place.getLatLng().toString());
                Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocation(
                            l.latitude,
                            l.longitude,
                            1);
                    address = addresses.get(0);
                    mTxtAddress.setText(address.getAddressLine(0)+" "+address.getCountryName());
                    mTxtAddressLine.setText(address.getAddressLine(0));

                    JsonObject addObj=new JsonObject();
                    addObj.addProperty("text",(address.getAddressLine(0)));

                    addObj.addProperty("pincode",(address.getPostalCode()==null||address.getPostalCode()=="")?"":address.getPostalCode());
                    addObj.addProperty("city",(address.getSubAdminArea()==null||address.getSubAdminArea()=="")?"":address.getSubAdminArea());
                    addObj.addProperty("state",(address.getAdminArea()==null||address.getAdminArea()=="")?"":address.getAdminArea());
                    addObj.addProperty("country",(address.getCountryName()==null||address.getCountryName()=="")?"":address.getCountryName());
                    JsonArray jsonArray=new JsonArray();
                    jsonArray.add(l.latitude);
                    jsonArray.add(l.longitude);
                    addObj.add("coordinates",jsonArray);
                    tempAddress=addObj;


                } catch (IOException e) {
                    e.printStackTrace();
                    mTxtAddressLine.setText("");
                    mTxtAddress.setText("");
                }

            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        mLytClose.setOnClickListener(v1 -> {

            bottomSheetDialog.cancel();
        });
        mLytAdd.setOnClickListener(v12 ->{
            JsonObject jsonObject=new JsonObject();
            if(mEdtHomeNumber.getText().toString().isEmpty()) {
                jsonObject.addProperty("text", tempAddress.get("text").getAsString());
            }
            else{
                jsonObject.addProperty("text", mEdtHomeNumber.getText().toString()+","+tempAddress.get("text"));
            }
            jsonObject.addProperty("pincode",tempAddress.get("pincode").getAsString());
            jsonObject.addProperty("city",tempAddress.get("city").getAsString());
            jsonObject.addProperty("state",tempAddress.get("state").getAsString());
            jsonObject.addProperty("country",tempAddress.get("country").getAsString());
            jsonObject.add("coordinates",tempAddress.get("coordinates"));

            isAddressSet = true;
            setUserAddress(jsonObject);
            bottomSheetDialog.cancel();
        });
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                getChildFragmentManager().beginTransaction().remove(autocompleteFragment).remove(mapFragment).commit();
            }
        });
        bottomSheetDialog.show();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    }
    void getMap(){
        mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    boolean isSelectUserAddress = false;
    JsonObject userAddressInfo =new  JsonObject();
    boolean isAddressSet = false;
    void setUserAddress(JsonObject addressObject) {
        if (isAddressSet) {
            userAddressInfo = addressObject;
            binding.addressEt.setText(  addressObject.get("text").toString());
        }
    }
    private SignUpUserModel getUserSignUpData() {
        String emailId = binding.emailET.getText().toString();
        String password = binding.passwordETtext.getText().toString();
        String mobile = (binding.mobileNumberET.getText().toString().contains("+61"))?binding.mobileNumberET.getText().toString().substring(3):binding.mobileNumberET.getText().toString();
        String name = binding.fullnameET.getText().toString();
        String dob = binding.dobET.getText().toString();
        String address = binding.addressEt.getText().toString();

        return new SignUpUserModel(emailId, mobile, name, dob, password, userAddressInfo, mViewModel.getLicense());
    }
    private Boolean verifyInputs() throws ParseException {
        String emailId = binding.emailET.getText().toString();
        String password = binding.passwordETtext.getText().toString();
        String confirmPassword = binding.confirmPasswordET.getText().toString();
        String fullName = binding.fullnameET.getText().toString();
        String address = binding.addressEt.getText().toString();
        if (TextUtils.isEmpty(fullName)) {
            binding.fullnameET.setError("Please enter name");
            showErrorSnack("Please enter name");
            return false;
        }
        if (TextUtils.isEmpty(binding.mobileNumberET.getText().toString())) {
            binding.mobileNumberET.setError("Please enter mobile number");
            showErrorSnack("Please enter mobile number");
            return false;
        }
        if (!isValidEmail(emailId)) {
            binding.emailET.setError("Please enter valid email address");
            showErrorSnack("Please enter valid email address");
            return false;
        }

        if (TextUtils.isEmpty(password) || !isValidPassword(password) || password.length() < 8) {
            binding.passwordETtext.setError("Please enter valid password");
            showErrorSnack("Please enter valid password");
            return false;
        }

        if (TextUtils.isEmpty(confirmPassword) || !isValidPassword(confirmPassword)) {
            binding.confirmPasswordET.setError("Please enter valid password");
            showErrorSnack("Please enter valid password");
            return false;
        }


        if (!password.trim().equals(confirmPassword.trim())) {
            binding.passwordETtext.setError("Passwords must match");
            binding.confirmPasswordET.setError("Passwords must match");
            showErrorSnack("Passwords must match");
            return false;
        }



        if(userAddressInfo==null){
            binding.addressEt.setError("Please enter name address");
            showErrorSnack("Please enter name address");
            return false;
        }
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
        Date birthdate=simpleDateFormat.parse(binding.dobET.getText().toString());
        if(HelperMethods.calculateAge(birthdate)<17){
            showErrorSnack("Your age must be above 16 years");
            return false;
        }


        return true;
    }

    private void getImg() {

        if (PermissionUtils.hasPermissions(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
            if (PermissionUtils.hasPermissions(getActivity(), Manifest.permission.CAMERA)) {
                showMenuOption(binding.userProfileImageView);
            } else
                PermissionUtils.requestPermissions(
                        getActivity(),
                        new String[]{Manifest.permission.CAMERA},
                        PermissionUtils.CAMERA_ACCESS
                );
        } else
            PermissionUtils.requestPermissions(
                    getActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PermissionUtils.READ_EXTERNAL_STORAGE
            );

    }
    private void showMenuOption(ImageView mImg) {
        PopupMenu popupMenu = new PopupMenu(getActivity(), mImg);
        popupMenu.inflate(R.menu.menu_item);
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            state = "Profile";
            switch (menuItem.getItemId()){
                case R.id.camera_selection:
                    ImagePicker.Companion.with(getParentFragment().getActivity())
                            .cameraOnly()
                            .start();
                    break;
                case R.id.gallery_selection:
                    ImagePicker.Companion.with(getParentFragment().getActivity())
                            .galleryOnly()
                            .start();
                    break;
            }
            return false;
        });
        popupMenu.show();
    }
    private void showErrorSnack(String errorMessage) {
        Utils.getErrorSnackBar(requireActivity(), binding.signUpButton ,errorMessage).show();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            if (requestCode == PermissionUtils.READ_EXTERNAL_STORAGE) {

                if (PermissionUtils.hasPermissions(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    if(!Environment.isExternalStorageManager()){
                        Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                        Uri uri = Uri.fromParts("package", getContext().getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                    if (PermissionUtils.hasPermissions(getActivity(), Manifest.permission.CAMERA)) {
                        showMenuOption(binding.userProfileImageView);
                    } else
                        PermissionUtils.requestPermissions(
                                getActivity(),
                                new String[]{Manifest.permission.CAMERA},
                                PermissionUtils.CAMERA_ACCESS
                        );
                } else
                    PermissionUtils.requestPermissions(
                            getActivity(),
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            PermissionUtils.READ_EXTERNAL_STORAGE
                    );
            }
            else if (requestCode == PermissionUtils.CAMERA_ACCESS) {
                if (PermissionUtils.hasPermissions(getActivity(), Manifest.permission.CAMERA)) {
                    showMenuOption(binding.userProfileImageView);
                } else
                    PermissionUtils.requestPermissions(
                            getActivity(),
                            new String[]{Manifest.permission.CAMERA},
                            PermissionUtils.CAMERA_ACCESS
                    );
            }
        }
        else {
            if (requestCode == PermissionUtils.READ_EXTERNAL_STORAGE) {
                if (PermissionUtils.hasPermissions(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    if (PermissionUtils.hasPermissions(getActivity(), Manifest.permission.CAMERA)) {
                        showMenuOption(binding.userProfileImageView);
                    } else
                        PermissionUtils.requestPermissions(
                                getActivity(),
                                new String[]{Manifest.permission.CAMERA},
                                PermissionUtils.CAMERA_ACCESS
                        );
                } else
                    PermissionUtils.requestPermissions(
                            getActivity(),
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            PermissionUtils.READ_EXTERNAL_STORAGE
                    );
            } else if (requestCode == PermissionUtils.CAMERA_ACCESS) {
                if (PermissionUtils.hasPermissions(getActivity(), Manifest.permission.CAMERA)) {
                    showMenuOption(binding.userProfileImageView);
                } else
                    PermissionUtils.requestPermissions(
                            getActivity(),
                            new String[]{Manifest.permission.CAMERA},
                            PermissionUtils.CAMERA_ACCESS
                    );
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if(state.equals("Profile")) {
            Log.d("HERE","Profile");
            if (resultCode == Activity.RESULT_OK && data != null) {
                if(requestCode == Constants.IMAGE_PICK) {
                    //Image Uri will not be null for RESULT_OK
                    Uri fileUri = data.getData();
                    String filePath = ImagePicker.Companion.getFilePath(data);//"/storage/emulated/0/DCIM/1594823279508.jpg";
                    //ImagePicker.Companion.getFilePath(data);

                    mViewModel.setImageUri(filePath);
                    Glide.with(this)
                            .load(fileUri)
                            .apply(RequestOptions.circleCropTransform())
                            .into(binding.userProfileImageView);
                }
                // binding.icon.setVisibility(View.GONE);
                // binding.ringView.setVisibility(View.GONE);
            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(requireActivity(), ImagePicker.Companion.getError(data), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onAttach(@NonNull Context context1) {
        super.onAttach(context1);
        context=context1;
    }

    public static Context context;
    public static void updateProfileImage(Uri fileUri) {
        Glide.with(context)
                .load(fileUri)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.userProfileImageView);
        binding.userProfileImageView.setBackground(null);
        binding.icon.setVisibility(View.GONE);
    }
}