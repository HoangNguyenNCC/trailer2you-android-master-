package com.applutions.t2y.data.response.search;

import com.applutions.t2y.data.items.PhotoObj;
import com.applutions.t2y.data.response.trailer.TrailerObj;
import com.applutions.t2y.ui.notifications.response.RentalObj;

import java.util.ArrayList;

public class TrailerViewResponse {

    private LicenseeObj licenseeObj;
    private String message;
    private boolean success;
    private TrailerObj trailerObj;
    private ArrayList<Upsell> upsellItemsList;

    public class LicenseeObj{
        String licenseeId;
        String licenseeName;
        String ownerName;

        public String getLicenseeId() {
            return licenseeId;
        }

        public String getLicenseeName() {
            return licenseeName;
        }

        public String getOwnerName() {
            return ownerName;
        }
    }

    public class Upsell{
        String _id;
        String description;
        String distance;
        boolean insured;
        boolean isAvailableForRent;
        String name;
        ArrayList<PhotoObj> photo;
        boolean serviced;
        String total;
        RentalObj.TrailerCharges totalCharges;
        String type;
        int btnID=0;

        public int getBtnID() {
            return btnID;
        }

        public void setBtnID(int btnID) {
            this.btnID = btnID;
        }

        public String get_id() {
            return _id;
        }

        public String getDescription() {
            return description;
        }

        public String getDistance() {
            return distance;
        }

        public boolean isInsured() {
            return insured;
        }

        public boolean isAvailableForRent() {
            return isAvailableForRent;
        }

        public String getName() {
            return name;
        }

        public ArrayList<PhotoObj> getPhoto() {
            return photo;
        }

        public boolean isServiced() {
            return serviced;
        }

        public String getTotal() {
            return total;
        }

        public RentalObj.TrailerCharges getTotalCharges() {
            return totalCharges;
        }

        public String getType() {
            return type;
        }
    }

    public LicenseeObj getLicenseeObj() {
        return licenseeObj;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return success;
    }

    public TrailerObj getTrailerObj() {
        return trailerObj;
    }

    public ArrayList<Upsell> getUpsellItemsList() {
        return upsellItemsList;
    }
}
