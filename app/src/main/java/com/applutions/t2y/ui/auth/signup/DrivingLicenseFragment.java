package com.applutions.t2y.ui.auth.signup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.applutions.t2y.R;
import com.applutions.t2y.common.PDFTools;
import com.applutions.t2y.data.response.UseObj;
import com.applutions.t2y.data.response.login.signIn.DriverLicense;
import com.applutions.t2y.databinding.FragmentDrivingLicenseBinding;
import com.applutions.t2y.utils.Constants;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.hbisoft.pickit.PickiT;
import com.hbisoft.pickit.PickiTCallbacks;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class DrivingLicenseFragment extends BottomSheetDialogFragment implements PickiTCallbacks {

    private String selectedState="";
    private String state = null;
    PickiT pickiT;

    private Activity appCompatActivity= null;
    public interface DrivingLicenseFragmentListener {
        void selectDrivingLicense(DriverLicense license, String licenseUriString);

    }
    private FragmentDrivingLicenseBinding binding;
    private static int PICK_FILE = 4;
    private DrivingLicenseFragmentListener mCallback = null;
    private String fileUri="";
    private String url = "none";

    JsonObject licenseForEdit;
    DriverLicense license;

    public DrivingLicenseFragment(DriverLicense license, String uri) {
        this.license = license;
        if (uri == null)
            Log.d("Here", "null");
        fileUri = uri;
    }

    public DrivingLicenseFragment(JsonObject license) {
        // Required empty public constructor
        licenseForEdit=new JsonObject();
        licenseForEdit=license;
    }
    public DrivingLicenseFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme);
        pickiT = new PickiT(getActivity(), this);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDrivingLicenseBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.closeDLDialog.setOnClickListener(v -> dismiss());
        binding.scanImageTv.setOnClickListener(this::pickLicense);
        binding.addDriverLicenceLayout.setOnClickListener(v -> fetchData());

        setUpStateListSpinner();
        if(licenseForEdit!=null) {
            binding.cardNumberET.setText(licenseForEdit.get("card").getAsString());
            binding.DLMonthExpiry.setText(licenseForEdit.get("expiry").getAsString().split("-")[1]);
            binding.DLYearExpiry.setText(licenseForEdit.get("expiry").getAsString().split("-")[0]);
            //binding.stateSpinner.setSelection(stateList.contains(licenseForEdit.get("state").toString()));
            if(!licenseForEdit.get("state").isJsonNull()){
            binding.stateSpinner.setSelection(stateList.indexOf(licenseForEdit.get("state").getAsString()));}
            if (!licenseForEdit.get("url").isJsonNull()){
                binding.scanImageTv.setText("License Added");
                url = licenseForEdit.get("url").getAsString();
            }
        }
        if (license!=null){
            binding.cardNumberET.setText(license.getCard());
            binding.DLMonthExpiry.setText(license.getExpiry().substring(0, license.getExpiry().indexOf("-")));
            binding.DLYearExpiry.setText(license.getExpiry().substring(license.getExpiry().indexOf("-")+1));
            //binding.stateSpinner.setSelection(stateList.contains(licenseForEdit.get("state").toString()));
            if(license.getState()!=null){
                binding.stateSpinner.setSelection(stateList.indexOf(license.getState()));}
            if (!fileUri.equals("")){
                binding.scanImageTv.setText("License Added");
            }
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try{
            mCallback = (DrivingLicenseFragmentListener) context;
            //appCompatActivity = (Activity) context;
        }catch (Exception e){
            throw new ClassCastException("Must implement authData" + requireActivity().toString());
        }
    }

    private void fetchData(){
        String cardNumber  = binding.cardNumberET.getText().toString();
        String expiry =binding.DLYearExpiry.getText().toString()+"-"+ binding.DLMonthExpiry.getText().toString();
        String state = selectedState;
        if(cardNumber.isEmpty()){
            errorSnack("Enter Valid Card Number!").show();
            return;
        }
        if (binding.DLMonthExpiry.getText().toString().isEmpty()) {
            errorSnack( "Please enter expiry month").show();
            return;

        }

        if (Integer.parseInt(binding.DLMonthExpiry.getText().toString()) > 12) {
            errorSnack(  "Please enter valid expiry month").show();
            return;

        }

        if (binding.DLYearExpiry.getText().toString().isEmpty()) {
            errorSnack(   "Please enter expiry year").show();
            return;

        }

        if (Integer.parseInt(binding.DLYearExpiry.getText().toString()) <= 2020) {
            errorSnack(   "Please make sure driving license is not expired. Expecting year > 2020").show();
            return;

        }

        if (selectedState.isEmpty() || selectedState == "" || selectedState == "SELECT STATE") {
            errorSnack("Enter Valid State!").show();
            return;
        }
        if(fileUri==""){
            errorSnack("Please select photo!").show();
            return;
        }
        Date date = new Date();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        if (Integer.parseInt(binding.DLYearExpiry.getText().toString()) < year){
            errorSnack("Please enter a valid Expiry Date");
            return;
        }
        else if (Integer.parseInt(binding.DLYearExpiry.getText().toString()) == year)
            if (Integer.parseInt(binding.DLMonthExpiry.getText().toString())<month){
                errorSnack("Please enter a valid Expiry Date");
                return;
            }
        DriverLicense driverLicense = new DriverLicense(expiry, cardNumber, selectedState);
        mCallback.selectDrivingLicense(driverLicense, fileUri);

        //appCompatActivity.setActionBar();
        dismiss();
    }
    ArrayList<String> stateList = new ArrayList<String>();
    private void setUpStateListSpinner() {
        stateList.clear();
        stateList.add("Select State");
        stateList.add("New South Wales");
        stateList.add("Queensland");
        stateList.add("Tasmania");
        stateList.add("Western Australia");
        stateList.add("Victoria");
        stateList.add("Northern Territory");
        stateList.add("Australian Capital Territory");
        stateList.add("South Australia");

        ArrayAdapter adapter =new ArrayAdapter(getContext(), R.layout.sgl_item_spinner, stateList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.stateSpinner.setAdapter(adapter);

        binding.stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedState = stateList.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectedState = "Select State";
            }
        });

    }
    private void pickLicense(View view){
        PopupMenu popupMenu = new PopupMenu(getActivity(), view);
        if (!url.equals("none")||!fileUri.equals("")){
            popupMenu.inflate(R.menu.view_license);
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                state = "DL";
                switch (menuItem.getItemId()){
                    case R.id.view_license_option:
                        if (!url.equals("none"))
                            PDFTools.openPDFThroughGoogleDrive(getActivity(),url);
                        else {
                            File file = new File(fileUri);
                            Uri tempUri = FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName() + ".provider", file);
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(tempUri, "application/pdf");
                            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            startActivity(intent);
                        }
                        break;
                    case R.id.camera_selection:
                        openPDFAndImageChooser();
                        break;
                }
                return false;
            });
            popupMenu.show();
        }
        else {
            openPDFAndImageChooser();
//            popupMenu.inflate(R.menu.menu_item);
//            popupMenu.setOnMenuItemClickListener(menuItem -> {
//                switch (menuItem.getItemId()) {
//                    case R.id.camera_selection:
//                        ImagePicker.Companion.with(this)
//                                .cameraOnly()
//                                .start();
//                        break;
//                    case R.id.gallery_selection:
//                        ImagePicker.Companion.with(this)
//                                .galleryOnly()
//                                .start();
//                        break;
//                }
//                return false;
//            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Log.d("Here", "Called");
            pickiT.getPath(data.getData(), Build.VERSION.SDK_INT);
        } else {
            Toast.makeText(requireActivity(), "File Not Selected", Toast.LENGTH_SHORT).show();
        }
    }

    public Snackbar errorSnack(String message){
        Snackbar snackbar = Snackbar.make(getActivity().getWindow().getDecorView().findViewById(android.R.id.content),message,Snackbar.LENGTH_SHORT);
        snackbar.setBackgroundTint(ContextCompat.getColor(requireActivity(),R.color.red_gradient_start));
        return snackbar;
    }

    private void openPDFAndImageChooser() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        String[] mimeTypes = {"application/pdf"};
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);

        startActivityForResult(intent, Constants.FILE_SELECTION_REQUEST_CODE);
    }

    @Override
    public void PickiTonStartListener() {

    }

    @Override
    public void PickiTonProgressUpdate(int progress) {

    }

    @Override
    public void PickiTonCompleteListener(String path, boolean wasDriveFile, boolean wasUnknownProvider, boolean wasSuccessful, String Reason) {
        Log.d("Here", "pickit"+path);
        fileUri = path;
        binding.scanImageTv.setText("File Selected");
    }


}