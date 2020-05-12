package lonetech.mobile_application.navplus;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;

import map.finalproject.lonetech.R;

public class Map extends Fragment implements OnMapReadyCallback, MapboxMap.OnMapClickListener
{
    private Context application_context;

    private MapboxMap mapboxMap;
    private MapView mapView;

    private UserLocation userLocation;
    private SymbolManager symbolManager;

    Map(Context incoming_context, MapView incoming_map, @Nullable Bundle incoming_saved_instance)
    {
        application_context = incoming_context;
        Mapbox.getInstance(application_context, getResources().getString(R.string.ACCESS_TOKEN));
        mapView = incoming_map;
        mapView.onCreate(incoming_saved_instance);
        mapView.getMapAsync(this);
    }

    public UserLocation getUserLocation()
    {
        return userLocation;
    }

    @Override
    public void onMapReady(@NonNull MapboxMap incoming_mapboxMap)
    {
        Map.this.mapboxMap = incoming_mapboxMap;

        userLocation = new UserLocation(application_context, mapboxMap, getActivity());

        mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded()
        {
            @Override
            public void onStyleLoaded(@NonNull Style style)
            {
                // need this "style" object to get permissions... so just get it over with right now...
                userLocation.getPermissions(style);

                // symbol manager to displaying markers on the map
                symbolManager = new SymbolManager(mapView, mapboxMap, style);
                symbolManager.setTextAllowOverlap(true);
                symbolManager.setIconAllowOverlap(true);
            }
        });
    }

    @Override
    public boolean onMapClick(@NonNull LatLng point)
    {
        return false;
    }

    public void zoom(LatLng incoming_coordinates)
    {
        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(incoming_coordinates).zoom(15).build()), 3000);
    }

    public SymbolManager getSymbolManager()
    {
        return symbolManager;
    }

    public MapboxMap getMapboxMap()
    {
        return mapboxMap;
    }

    public MapView getMapView()
    {
        return mapView;
    }
}
