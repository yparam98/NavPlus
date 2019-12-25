package map.finalproject.lonetech;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;

import java.util.List;

public class NavPlus extends Fragment implements OnMapReadyCallback, MapboxMap.OnMapClickListener, PermissionsListener
{

    private static final String ACCESS_TOKEN = "pk.eyJ1Ijoicml2YWxkZXZ5cCIsImEiOiJjazRqYjg2MzUwamIzM2lxczg2OWl0bm44In0.nqM7hrdpKkqnEQ4Bk0bNVw";
    private MapView mapView;
    private MapboxMap mapboxMap;
    private CameraPosition senecaCollegeNewnhamCampus = new CameraPosition.Builder().target(new LatLng(43.794413, -79.350118)).zoom(17).build();
    private Context myContext;
    private PermissionsManager permissionsManager;
    private LocationEngine locationEngine;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        Mapbox.getInstance(getActivity().getApplicationContext(), ACCESS_TOKEN);
        View myView = inflater.inflate(R.layout.mapbox, container, false);

        myContext = getActivity().getApplicationContext();

        mapView = myView.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        return myView;
    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap)
    {
        NavPlus.this.mapboxMap = mapboxMap;

        mapboxMap.setStyle(Style.TRAFFIC_NIGHT, new Style.OnStyleLoaded()
        {
            @Override
            public void onStyleLoaded(@NonNull Style style)
            {
//                Toast.makeText(getActivity().getApplicationContext(), "Map loaded", Toast.LENGTH_SHORT).show();
//                mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(senecaCollegeNewnhamCampus), 7000);
//                mapboxMap.addOnMapClickListener(NavPlus.this);
                enableLocationComponent(style);
                mapboxMap.addOnMapClickListener(NavPlus.this);
            }
        });
    }

    @SuppressWarnings({"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle)
    {
        if (PermissionsManager.areLocationPermissionsGranted(myContext))
        {
            LocationComponent locationComponent = mapboxMap.getLocationComponent();

            locationComponent.activateLocationComponent(LocationComponentActivationOptions.builder(myContext, loadedMapStyle).build());
            locationComponent.setLocationComponentEnabled(true);

            locationComponent.setCameraMode(CameraMode.TRACKING);
            locationComponent.setRenderMode(RenderMode.COMPASS);

            initLocationEngine();
        }
        else
        {
            Log.e("ERROR","NO LOCATION PERMISSIONS");
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(getActivity());
        }
    }

    @SuppressLint("MissingPermission")
    private void initLocationEngine()
    {
        locationEngine = LocationEngineProvider.getBestLocationEngine(myContext);

        LocationEngineRequest request = new LocationEngineRequest.Builder(DEFAULT)
    }

    @Override
    public boolean onMapClick(@NonNull LatLng point)
    {
//        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(;), 7000);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain)
    {
        Toast.makeText(myContext, "need location permissions", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPermissionResult(boolean granted)
    {
        if (granted)
        {
            mapboxMap.getStyle(new Style.OnStyleLoaded()
            {
                @Override
                public void onStyleLoaded(@NonNull Style style)
                {
                    Toast.makeText(myContext, "permissions granted", Toast.LENGTH_LONG).show();
                    enableLocationComponent(style);
                }
            });
        }
        else
        {
            Toast.makeText(myContext, "permissions not granted", Toast.LENGTH_LONG).show();
        }
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
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}