package com.applutions.t2y.ui.notifications.response;

import com.applutions.t2y.data.items.PhotoObj;

import java.io.Serializable;
import java.util.ArrayList;

public class RentalObj {
    private String _id,rentalStatus,invoiceNumber,invoiceReference,invoiceId;
    private boolean isTrackingPickUp,isTrackingDropOff,doChargeDLR,isPickUp;
    private ArrayList<Revisions> revisions=new ArrayList<Revisions>();
    private  String bookingId;

    private ArrayList<Items> rentedItems=new ArrayList<Items>();
    private Charges charges;
    private RentalPeriod rentalPeriod;
    private DropOffLocation dropOffLocation;
    private PickUpLocation pickUpLocation;



    public PickUpLocation getPickUpLocation() {
        return pickUpLocation;
    }

    public void setPickUpLocation(PickUpLocation pickUpLocation) {
        this.pickUpLocation = pickUpLocation;
    }


    public String getBookingId() {
        return bookingId;
    }

    public DropOffLocation getDropOffLocation() {
        return dropOffLocation;
    }

    public void setDropOffLocation(DropOffLocation dropOffLocation) {
        this.dropOffLocation = dropOffLocation;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getRentalStatus() {
        return rentalStatus;
    }

    public void setRentalStatus(String rentalStatus) {
        this.rentalStatus = rentalStatus;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getInvoiceReference() {
        return invoiceReference;
    }

    public void setInvoiceReference(String invoiceReference) {
        this.invoiceReference = invoiceReference;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public boolean isTrackingPickUp() {
        return isTrackingPickUp;
    }

    public void setTrackingPickUp(boolean trackingPickUp) {
        isTrackingPickUp = trackingPickUp;
    }

    public boolean isTrackingDropOff() {
        return isTrackingDropOff;
    }

    public void setTrackingDropOff(boolean trackingDropOff) {
        isTrackingDropOff = trackingDropOff;
    }

    public boolean isDoChargeDLR() {
        return doChargeDLR;
    }

    public void setDoChargeDLR(boolean doChargeDLR) {
        this.doChargeDLR = doChargeDLR;
    }

    public boolean isPickUp() {
        return isPickUp;
    }

    public void setPickUp(boolean pickUp) {
        isPickUp = pickUp;
    }

    public ArrayList<Revisions> getRevisions() {
        return revisions;
    }

    public void setRevisions(ArrayList<Revisions> revisions) {
        this.revisions = revisions;
    }

    public ArrayList<Items> getRentedItems() {
        return rentedItems;
    }

    public void setRentedItems(ArrayList<Items> rentedItems) {
        this.rentedItems = rentedItems;
    }

    public Charges getCharges() {
        return charges;
    }

    public void setCharges(Charges charges) {
        this.charges = charges;
    }

    public RentalPeriod getRentalPeriod() {
        return rentalPeriod;
    }

    public void setRentalPeriod(RentalPeriod rentalPeriod) {
        this.rentalPeriod = rentalPeriod;
    }

    public class Revisions {
        private String revisionId,revisionType;
        private Charges charges;

        public Charges getCharges() {
            return charges;
        }

        public String getRevisionId() {
            return revisionId;
        }

        public void setRevisionId(String revisionId) {
            this.revisionId = revisionId;
        }

        public String getRevisionType() {
            return revisionType;
        }

        public void setRevisionType(String revisionType) {
            this.revisionType = revisionType;
        }
    }

    public class Charges implements Serializable {
        private Double totalPayableAmount;
        private TrailerCharges trailerCharges;
        private ArrayList<UpsellCharges> upsellCharges;
        private String _id;

        public Double getTotalPayableAmount() {
            return totalPayableAmount;
        }

        public TrailerCharges getTrailerCharges() {
            return trailerCharges;
        }

        public ArrayList<UpsellCharges> getUpsellCharges() {
            return upsellCharges;
        }

        public String get_id() {
            return _id;
        }
    }

    public class TrailerCharges implements Serializable{
        Double cancellationCharges;
        Double discount;
        Double dlrCharges;
        Double lateFees;
        Double rentalCharges;
        Double t2yCommission;
        Double taxes;
        Double total;
        String _id;

        public Double getCancellationCharges() {
            return cancellationCharges;
        }

        public Double getDiscount() {
            return discount;
        }

        public Double getDlrCharges() {
            return dlrCharges;
        }

        public Double getLateFees() {
            return lateFees;
        }

        public Double getRentalCharges() {
            return rentalCharges;
        }

        public Double getT2yCommission() {
            return t2yCommission;
        }

        public Double getTaxes() {
            return taxes;
        }

        public Double getTotal() {
            return total;
        }

        public String get_id() {
            return _id;
        }
    }

    public class UpsellCharges implements Serializable{
        TrailerCharges charges;
        String id;
        Double payable;
        int quantity;
        String _id;

        public TrailerCharges getCharges() {
            return charges;
        }

        public String getId() {
            return id;
        }

        public Double getPayable() {
            return payable;
        }

        public int getQuantity() {
            return quantity;
        }

        public String get_id() {
            return _id;
        }
    }

    public class RentalPeriod {
        private String start,end;

        public String getStart() {
            return start;
        }

        public void setStart(String start) {
            this.start = start;
        }

        public String getEnd() {
            return end;
        }

        public void setEnd(String end) {
            this.end = end;
        }
    }

    public class DropOffLocation {
        private String text,pincode;
        private ArrayList<Double> coordinates=new ArrayList<>();

        public ArrayList<Double> getCoordinates() {
            return coordinates;
        }

        public void setCoordinates(ArrayList<Double> coordinates) {
            this.coordinates = coordinates;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getPincode() {
            return pincode;
        }

        public void setPincode(String pincode) {
            this.pincode = pincode;
        }
    }
    public class PickUpLocation {
        private String text,pincode;
        private ArrayList<Double> coordinates=new ArrayList<>();

        public ArrayList<Double> getCoordinates() {
            return coordinates;
        }

        public void setCoordinates(ArrayList<Double> coordinates) {
            this.coordinates = coordinates;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getPincode() {
            return pincode;
        }

        public void setPincode(String pincode) {
            this.pincode = pincode;
        }
    }

    public class Item implements Serializable{
        private String _id, name;
        private ArrayList<PhotoObj> photos;

        public String get_id() {
            return _id;
        }

        public String getName() {
            return name;
        }

        public ArrayList<PhotoObj> getPhotos() {
            return photos;
        }
    }
}
