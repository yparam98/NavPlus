package map.finalproject.lonetech;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
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

import java.lang.ref.WeakReference;
import java.util.List;

public class NavPlus extends Fragment implements OnMapReadyCallback, MapboxMap.OnMapClickListener, PermissionsListener
{

    private static final String ACCESS_TOKEN = "pk.eyJ1Ijoicml2YWxkZXZ5cCIsImEiOiJjazRqYjg2MzUwamIzM2lxczg2OWl0bm44In0.nqM7hrdpKkqnEQ4Bk0bNVw";
    private MapView mapView;
    private MapboxMap mapboxMap;
    private Context myContext;
    private PermissionsManager permissionsManager;
    private LocationEngine locationEngine;
    private static final long DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L;
    private static final long DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5;
    private LocationChangeListeningActivityLocationCallback callback = new LocationChangeListeningActivityLocationCallback(this);
    private Location userLocation;
    private FloatingActionButton myLocationButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        myContext = getActivity().getApplicationContext();

        Mapbox.getInstance(myContext, ACCESS_TOKEN);
        View myView = inflater.inflate(R.layout.mapbox, container, false);

        myLocationButton = myView.findViewById(R.id.myLocationButton);

        myLocationButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(new LatLng(userLocation.getLatitude(), userLocation.getLongitude())).zoom(17).build()), 7000);
            }
        });

        mapView = myView.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        return myView;
    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap)
    {
        NavPlus.this.mapboxMap = mapboxMap;

        mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded()
        {
            @Override
            public void onStyleLoaded(@NonNull Style style)
            {
                enableLocationComponent(style);
//                mapboxMap.addOnMapClickListener(NavPlus.this);
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

            initializeLocationEngine();
        }
        else
        {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(getActivity());
        }
    }

    @SuppressLint("MissingPermission")
    private void initializeLocationEngine()
    {
        locationEngine = LocationEngineProvider.getBestLocationEngine(myContext);

        LocationEngineRequest request = new LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS).setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY).setMaxWaitTime(DEFAULT_MAX_WAIT_TIME).build();

        locationEngine.requestLocationUpdates(request, callback, Looper.getMainLooper());
        locationEngine.getLastLocation(callback);
    }

    @Override
    public boolean onMapClick(@NonNull LatLng point)
    {
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
            if (mapboxMap.getStyle() != null)
            {
                enableLocationComponent(mapboxMap.getStyle());
            }
        }
        else
        {
            Toast.makeText(myContext, "permissions not granted", Toast.LENGTH_LONG).show();
        }
    }

    private static class LocationChangeListeningActivityLocationCallback implements LocationEngineCallback<LocationEngineResult>
    {
        private final WeakReference<NavPlus> activityWeakReference;

        LocationChangeListeningActivityLocationCallback(NavPlus activity)
        {
            this.activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void onSuccess(LocationEngineResult result)
        {
            NavPlus activity = activityWeakReference.get();

            if (activity != null)
            {
                Location location = result.getLastLocation();

                if (location == null)
                {
                    return;
                }

                if (activity.mapboxMap != null && result.getLastLocation() != null)
                {
                    activity.mapboxMap.getLocationComponent().forceLocationUpdate(result.getLastLocation());
                    activity.userLocation = location;
                }
            }
        }

        @Override
        public void onFailure(@NonNull Exception exception)
        {
            Log.d("LocationChangeActivity", exception.getLocalizedMessage());

            NavPlus activity = activityWeakReference.get();

            if (activity != null)
            {
                Toast.makeText(activity.getActivity().getApplicationContext(), exception.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
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

        if (locationEngine != null)
        {
            locationEngine.removeLocationUpdates(callback);
        }

        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}