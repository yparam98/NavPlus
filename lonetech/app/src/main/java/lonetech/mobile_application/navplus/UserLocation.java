package lonetech.mobile_application.navplus;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;

import java.lang.ref.WeakReference;
import java.util.List;

public class UserLocation implements PermissionsListener
{
    private Context application_context;
    private Location location;

    private PermissionsManager permissionsManager;
    private LocationEngine locationEngine;
    private static final long DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L;
    private static final long DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5;
    private UserLocation.LocationChangeListeningActivityLocationCallback callback = new UserLocation.LocationChangeListeningActivityLocationCallback(this);
    private Location userLocation;
    private LocationComponent locationComponent;
    private FragmentActivity fragmentActivity;
    private static MapboxMap mapboxMap;

    UserLocation(Context context, MapboxMap incoming_mapbox_map, FragmentActivity incoming_activity)
    {
        application_context = context;
        locationComponent = incoming_mapbox_map.getLocationComponent();
        mapboxMap = incoming_mapbox_map;
        fragmentActivity = incoming_activity;
    }

    @SuppressWarnings({"MissingPermission"})
    public void getPermissions(@NonNull Style loadedMapStyle)
    {
        if (PermissionsManager.areLocationPermissionsGranted(application_context))
        {
            locationComponent.activateLocationComponent(LocationComponentActivationOptions.builder(application_context, loadedMapStyle).build());
            locationComponent.setLocationComponentEnabled(true);

            locationComponent.setCameraMode(CameraMode.TRACKING);
            locationComponent.setRenderMode(RenderMode.COMPASS);

            initializeLocationEngine();
        }
        else
        {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(fragmentActivity);
        }
    }

    @SuppressLint("MissingPermission")
    private void initializeLocationEngine()
    {
        locationEngine = LocationEngineProvider.getBestLocationEngine(application_context);

        LocationEngineRequest request = new LocationEngineRequest.Builder(
                DEFAULT_INTERVAL_IN_MILLISECONDS
        ).setPriority(
                LocationEngineRequest.PRIORITY_HIGH_ACCURACY
        ).setMaxWaitTime(
                DEFAULT_MAX_WAIT_TIME
        ).build();

        locationEngine.requestLocationUpdates(request, callback, Looper.getMainLooper());
        locationEngine.getLastLocation(callback);
    }

    public LatLng getLocation()
    {
        return new LatLng(location.getLatitude(),location.getLongitude());
    }

    public Location getLocationModule()
    {
        return location;
    }


    public void track()
    {

    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain)
    {
        Toast.makeText(application_context, "need location permissions", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPermissionResult(boolean granted)
    {
        if (granted)
        {
            if (mapboxMap.getStyle() != null)
            {
                getPermissions(mapboxMap.getStyle());
            }
        }
        else
        {
            Toast.makeText(application_context, "permissions not granted", Toast.LENGTH_LONG).show();
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }



    public FragmentActivity getFragmentActivity()
    {
        return fragmentActivity;
    }

    private static class LocationChangeListeningActivityLocationCallback implements LocationEngineCallback<LocationEngineResult>
    {
        private final WeakReference<UserLocation> activityWeakReference;

        LocationChangeListeningActivityLocationCallback(UserLocation activity)
        {
            this.activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void onSuccess(LocationEngineResult result)
        {
            UserLocation activity = activityWeakReference.get();

            if (activity != null)
            {
                Location location = result.getLastLocation();

                if (location == null)
                {
                    return;
                }



                if (UserLocation.mapboxMap != null && result.getLastLocation() != null)
                {
                    activity.mapboxMap.getLocationComponent().forceLocationUpdate(result.getLastLocation());
                    activity.userLocation = location;
//                    activity.userLocation = new Location();
                }
            }
        }

        @Override
        public void onFailure(@NonNull Exception exception)
        {
            Log.d("LocationChangeActivity", exception.getLocalizedMessage());

            UserLocation activity = activityWeakReference.get();

            if (activity != null)
            {
                Toast.makeText(activity.getFragmentActivity().getApplicationContext(), exception.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
