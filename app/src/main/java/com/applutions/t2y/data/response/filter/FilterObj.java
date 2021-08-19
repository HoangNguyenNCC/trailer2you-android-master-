package com.applutions.t2y.data.response.filter;

import java.util.ArrayList;

public class FilterObj{
    ArrayList<FilterType> trailerTypesList;
    ArrayList<FilterType> upsellItemTypesList;
    ArrayList<FilterType> deliveryTypesList;
    ArrayList<FilterType> trailerModelList;

    public FilterObj(ArrayList<FilterType> trailerTypesList, ArrayList<FilterType> upsellItemTypesList,
                     ArrayList<FilterType> deliveryTypesList, ArrayList<FilterType> trailerModelList) {
        this.trailerTypesList = trailerTypesList;
        this.upsellItemTypesList = upsellItemTypesList;
        this.deliveryTypesList = deliveryTypesList;
        this.trailerModelList = trailerModelList;
    }

    public ArrayList<FilterType> getTrailerTypesList() {
        return trailerTypesList;
    }

    public ArrayList<FilterType> getUpsellItemTypesList() {
        return upsellItemTypesList;
    }

    public ArrayList<FilterType> getDeliveryTypesList() {
        return deliveryTypesList;
    }

    public ArrayList<FilterType> getModelTypesList() {
        return trailerModelList;
    }
}