package com.justm.ssip;

public class AddressClass {

    String placeName,placeAddress;

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getPlaceAddress() {
        return placeAddress;
    }

    public void setPlaceAddress(String placeAddress) {
        this.placeAddress = placeAddress;
    }

    public AddressClass() {
    }

    public AddressClass(String placeName, String placeAddress) {
        this.placeName = placeName;
        this.placeAddress = placeAddress;
    }
}
