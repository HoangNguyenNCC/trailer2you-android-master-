package com.applutions.t2y.data.response;

import com.applutions.t2y.ui.notifications.response.RentalObj;

import java.io.Serializable;
import java.util.ArrayList;

public class RatingsResponse implements Serializable {
    ArrayList<Obj> response;

    public class Obj{
        RentalObj invoice;
        private RentalObj.Item trailer;
        private RentalObj.Item licensee;

        public RentalObj getInvoice() {
            return invoice;
        }

        public RentalObj.Item getTrailer() {
            return trailer;
        }

        public RentalObj.Item getLicensee() {
            return licensee;
        }
    }

    public ArrayList<Obj> getResponse() {
        return response;
    }
}
