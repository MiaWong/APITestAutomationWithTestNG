package com.expedia.s3.cars.ecommerce.carbs.service.commonutils;

import java.util.ArrayList;
import java.util.List;

public final class VendorLatLongList {

    private VendorLatLongList() {

    }

    public static List<LatitudeAndLongitude> tfs57982CarBSLatLongSearchAgencyUS() {
        final List<LatitudeAndLongitude> vendorList = new ArrayList<>();
        vendorList.add(new LatitudeAndLongitude(47.442790, -122.298850));
        vendorList.add(new LatitudeAndLongitude(47.442820, -122.298800));
        vendorList.add(new LatitudeAndLongitude(47.442740, -122.298820));
        vendorList.add(new LatitudeAndLongitude(47.443549, -122.301586));
        vendorList.add(new LatitudeAndLongitude(47.445237, -122.291426));
        vendorList.add(new LatitudeAndLongitude(47.443495, -122.296269));
        vendorList.add(new LatitudeAndLongitude(47.445323, -122.296408));
        vendorList.add(new LatitudeAndLongitude(47.435838, -122.295692));
        vendorList.add(new LatitudeAndLongitude(47.434700, -122.303200));


        return vendorList;
    }

    public static List<LatitudeAndLongitude> carBSLatLongSearchUK() {
        final List<LatitudeAndLongitude> vendorList = new ArrayList<>();
        vendorList.add(new LatitudeAndLongitude(55.944645, -3.361504));
        vendorList.add(new LatitudeAndLongitude(55.945250, -3.361970));
        vendorList.add(new LatitudeAndLongitude(55.946341, -3.359976));
        vendorList.add(new LatitudeAndLongitude(55.946200, -3.360800));
        vendorList.add(new LatitudeAndLongitude(55.948100, -3.365030));
        vendorList.add(new LatitudeAndLongitude(55.948500, -3.364500));
        vendorList.add(new LatitudeAndLongitude( 55.950000, -3.372500));
        return vendorList;
    }

}