package lonetech.mobile_application.prototype;

import android.location.Location;

import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.geojson.Point;

public class SearchResult
{
    private String place_name, address, category;
    private Point coordinates;
    private double distanceFromLocation;
    private Location userLocation;

    SearchResult(CarmenFeature incomingObj, Location incomingUserLocation)
    {
        this.place_name = incomingObj.placeName();
        this.address = incomingObj.address();
        this.coordinates = incomingObj.center();
        this.userLocation = incomingUserLocation;

        calculateDistance();
    }

    private void calculateDistance()
    {
        double userLocation_lat = userLocation.getLatitude();
        double userLocation_long = userLocation.getLongitude();
        double searchResult_lat = coordinates.latitude();
        double searchResult_long = coordinates.longitude();

        final int R  = 6371;

        double latDistance = Math.toRadians(searchResult_lat - userLocation_lat);
        double longDistance = Math.toRadians(searchResult_long - userLocation_long);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(searchResult_lat)) * Math.cos(Math.toRadians(userLocation_lat))
                * Math.sin(longDistance / 2) * Math.sin(longDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        this.distanceFromLocation = R * c;
    }

    public String getAddress()
    {
        return place_name;
    }

    public Point getCoordinates()
    {
        return coordinates;
    }

    public double getDistanceFromLocation()
    {
        return distanceFromLocation;
    }
}
