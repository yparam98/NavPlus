package lonetech.mobile_application.navplus;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        Mapbox.getInstance(application_context, getResources().getString(R.string.ACCESS_TOKEN));
        View view = inflater.inflate(R.layout.map_view, container, false);
        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        return view;
    }


    Map(Context incoming_context)
    {
        application_context = incoming_context;
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

    public void getMapView(MapView incoming_map_view)
    {
        incoming_map_view = mapView;
    }

    @Override
    @SuppressWarnings( {"MissingPermission"})
    public void onStart()
    {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop()
    {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState)
    {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}
