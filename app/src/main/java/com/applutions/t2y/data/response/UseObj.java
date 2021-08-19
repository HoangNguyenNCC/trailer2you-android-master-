package com.applutions.t2y.data.response;

import com.applutions.t2y.data.items.PhotoObj;

import java.util.ArrayList;

public class UseObj {
    private String _id,name,email,dob,mobile,userRef;
    private PhotoObj photo;
    private Adress address;
    private boolean isMobileVerified,isEmailVerified;
    private DriverLicense driverLicense;


    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public PhotoObj getPhoto() {
        return photo;
    }

    public void setPhoto(PhotoObj photo) {
        this.photo = photo;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getUserRef() {
        return userRef;
    }

    public void setUserRef(String userRef) {
        this.userRef = userRef;
    }

    public Adress getAddress() {
        return address;
    }

    public void setAddress(Adress address) {
        this.address = address;
    }

    public boolean isMobileVerified() {
        return isMobileVerified;
    }

    public void setMobileVerified(boolean mobileVerified) {
        isMobileVerified = mobileVerified;
    }

    public boolean isEmailVerified() {
        return isEmailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        isEmailVerified = emailVerified;
    }

    public DriverLicense getDriverLicense() {
        return driverLicense;
    }

    public void setDriverLicense(DriverLicense driverLicense) {
        this.driverLicense = driverLicense;
    }

    public class DriverLicense{
        private String card,expiry,state;
        private boolean verified,accepted;
        private PhotoObj scan;

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getCard() {
            return card;
        }

        public void setCard(String card) {
            this.card = card;
        }

        public String getExpiry() {
            return expiry;
        }

        public void setExpiry(String expiry) {
            this.expiry = expiry;
        }

        public PhotoObj getScan() {
            return scan;
        }

        public void setScan(PhotoObj scan) {
            this.scan = scan;
        }

        public boolean isVerified() {
            return verified;
        }

        public void setVerified(boolean verified) {
            this.verified = verified;
        }

        public boolean isAccepted() {
            return accepted;
        }

        public void setAccepted(boolean accepted) {
            this.accepted = accepted;
        }
    }
    public class Adress {
        private String text,state,country,pincode,city;
        private ArrayList<Double> coordinates;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getPincode() {
            return pincode;
        }

        public void setPincode(String pincode) {
            this.pincode = pincode;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public ArrayList<Double> getCoordinates() {
            return coordinates;
        }

        public void setCoordinates(ArrayList<Double> coordinates) {
            this.coordinates = coordinates;
        }

        @Override
        public String toString() {
            return "Adress{" +
                    "text='" + text + '\'' +
                    ", state='" + state + '\'' +
                    ", country='" + country + '\'' +
                    ", pincode='" + pincode + '\'' +
                    ", city='" + city + '\'' +
                    ", coordinates=" + coordinates +
                    '}';
        }
    }

}
