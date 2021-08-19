package com.applutions.t2y.ui.trailerDetails.upsell;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.applutions.t2y.R;
import com.applutions.t2y.adapters.UpsellItemsAdapter;
import com.applutions.t2y.data.items.BookingUpsell;
import com.applutions.t2y.data.response.search.TrailerViewResponse;
import com.applutions.t2y.data.response.search.UpsellItem;
import com.applutions.t2y.ui.trailerDetails.TrailerDetailsActivity;
import com.applutions.t2y.ui.trailerDetails.TrailerDetailsViewModel;
import com.applutions.t2y.utils.ApiResponse;
import com.applutions.t2y.utils.SharedPrefs;
import com.applutions.t2y.utils.Utils;

import java.util.ArrayList;

public class TrailerUpsellFragment extends Fragment implements UpsellItemsAdapter.UpsellItemsListener {

    private TrailerDetailsViewModel mViewModel;
    private RecyclerView rvUpsellItems;
    private UpsellItemsAdapter mAdapter;
    private ProgressBar pbUpsell;
    private ArrayList<TrailerViewResponse.Upsell> upsellItems;
    private TextView noUpsell;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_trailer_upsell, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TrailerDetailsActivity a = (TrailerDetailsActivity)getActivity();
        mViewModel = a.getmViewModel();
        noUpsell = requireActivity().findViewById(R.id.tv_no_upsell);

        mViewModel.getViewLiveData().observe(requireActivity(), response->{
            ApiResponse.ApiResponseStatus status = response.getApiStatus();
            switch (status) {
                case LOADING:
//                    setLoadingUi();
                    break;
                case FAILED:
//                    removeLoadingUi();
//                    Utils.getErrorSnackBar(requireActivity(), btnRent, response.getErrorMessage().message()).show();
                    break;
                case SUCCESS:
                    mAdapter = new UpsellItemsAdapter(this);
                    upsellItems = response.getData().getUpsellItemsList();
                    pbUpsell = getActivity().findViewById(R.id.pb_upsell);
                    rvUpsellItems = getActivity().findViewById(R.id.recycler_view_upsell_trailers_fragments);
                    if (upsellItems.size()>0){
                        noUpsell.setVisibility(View.GONE);
                        rvUpsellItems.setVisibility(View.VISIBLE);
                    }
                    else{
                        noUpsell.setVisibility(View.VISIBLE);
                        rvUpsellItems.setVisibility(View.GONE);
                    }
                    LinearLayoutManager manager = new LinearLayoutManager(requireActivity());

                    rvUpsellItems.setHasFixedSize(true);
                    rvUpsellItems.setLayoutManager(manager);
                    rvUpsellItems.setAdapter(mAdapter);

                    SharedPrefs sharedPrefs = SharedPrefs.getInstance(requireActivity());
//        String trailerId = "5f1479a438858c54db9b760a";
                    String trailerId = sharedPrefs.getItemId();
                    mAdapter.setupsellItems(upsellItems);
                    hideLoadingUI();
//                    removeLoadingUi();
//                    setUiData(response.getData());
//                    mViewModel.getLiceseeDetails(getActivity(),response.getData().getLicenseeObj().getLicenseeId());
                    break;
            }
        });



    }

    private void hideLoadingUI() {
        pbUpsell.setVisibility(View.GONE);
        rvUpsellItems.setVisibility(View.VISIBLE);
    }

    private void showLoadingUI() {
        pbUpsell.setVisibility(View.VISIBLE);
        rvUpsellItems.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(UpsellItem item) {

    }

    public ArrayList<BookingUpsell> getBookedItems(){
        if(mAdapter!=null)
            return mAdapter.getBookedItems();
        else
            return new ArrayList<>();
    }

}