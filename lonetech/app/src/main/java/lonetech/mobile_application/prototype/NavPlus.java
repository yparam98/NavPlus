package lonetech.mobile_application.prototype;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.geocoding.v5.GeocodingCriteria;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.geojson.Point;
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
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.mapbox.mapboxsdk.plugins.markerview.MarkerView;
import com.mapbox.mapboxsdk.plugins.markerview.MarkerViewManager;
import com.mapbox.mapboxsdk.utils.BitmapUtils;
import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigation;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import map.finalproject.lonetech.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    private ImageButton mySearchButton;
    private MapboxNavigation navigation;
    private EditText myLocationSearchField;
    private MaterialCardView utilitiesContainer;
    private MaterialTextView locationAddress;
    private SearchResultsAdapter adapter;
    private ListView search_results_list;
    private List<SearchResult> search_results = new ArrayList<>();
    private SymbolManager symbolManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        myContext = getActivity().getApplicationContext();

        Mapbox.getInstance(myContext, ACCESS_TOKEN);
//        navigation = new MapboxNavigation(myContext, ACCESS_TOKEN);
        final View myView = inflater.inflate(R.layout.mapbox, container, false);

        myLocationButton = myView.findViewById(R.id.myLocationButton);
        mySearchButton = myView.findViewById(R.id.mySearchButton);
        myLocationSearchField = myView.findViewById(R.id.myLocationSearchField);
        utilitiesContainer = myView.findViewById(R.id.utilitiesContainer);
        locationAddress = myView.findViewById(R.id.searchedLocationAddress);
        search_results_list = myView.findViewById(R.id.search_results_list_view);

        myLocationButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                utilitiesContainer.setVisibility(View.INVISIBLE);
                zoomTo(new LatLng(userLocation.getLatitude(), userLocation.getLongitude()));
            }
        });

        mySearchButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // close keyboard
                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(myView.getWindowToken(), 0);

                // close utility panel
                utilitiesContainer.setVisibility(View.INVISIBLE);

                searchOnMap(myLocationSearchField.getText());
            }
        });

        search_results_list.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                symbolManager.deleteAll();

                search_results_list.setVisibility(View.INVISIBLE);
                Log.i("AT&T", "item #" + i + " clicked!");
                locationAddress.setText(search_results.get(i).getAddress());
                utilitiesContainer.setVisibility(View.VISIBLE);
                myLocationSearchField.setText(search_results.get(i).getAddress(), TextView.BufferType.NORMAL);

                Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_my_location_white_24dp, null);
                Bitmap bitmap = BitmapUtils.getBitmapFromDrawable(drawable);

                mapboxMap.getStyle().addImage("my-marker", bitmap);

                symbolManager.create(new SymbolOptions()
                        .withLatLng(new LatLng(
                                search_results.get(i).getCoordinates().latitude(),
                                search_results.get(i).getCoordinates().longitude()
                        ))
                        .withIconImage("my-marker")
                        .withIconSize(0.75f)
                        .withTextField(search_results.get(i).getAddress().substring(0, search_results.get(i).getAddress().indexOf(',')))
                        .withTextAnchor("top")
                        .withTextHaloWidth(5.0f)
                        .withTextSize(12f)
                        .withDraggable(false)
                        .withTextOffset(new Float[] {0f, 1.5f})
                );

                zoomTo(new LatLng(
                        search_results.get(i).getCoordinates().latitude(),
                        search_results.get(i).getCoordinates().longitude()
                ));
            }
        });

        mapView = myView.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        return myView;
    }

    private void searchOnMap(Editable text)
    {
        Log.e("VERIZON", "searchOnMap entered...");
        // add functionality to mark location on map using Geocoding API...
        final MapboxGeocoding mapboxGeocoding = MapboxGeocoding.builder().accessToken(
                ACCESS_TOKEN
        ).query(
                text.toString()
        ).geocodingTypes(
                GeocodingCriteria.TYPE_POI,
                GeocodingCriteria.TYPE_PLACE,
                GeocodingCriteria.TYPE_ADDRESS,
                GeocodingCriteria.TYPE_COUNTRY,
                GeocodingCriteria.TYPE_DISTRICT,
                GeocodingCriteria.TYPE_LOCALITY,
                GeocodingCriteria.TYPE_NEIGHBORHOOD,
                GeocodingCriteria.TYPE_POI_LANDMARK,
                GeocodingCriteria.TYPE_POSTCODE,
                GeocodingCriteria.TYPE_REGION
        ).mode(
                GeocodingCriteria.MODE_PLACES
        ).fuzzyMatch(
                true
        ).proximity(
                Point.fromLngLat(userLocation.getLongitude(), userLocation.getLatitude())
        ).autocomplete(
                true
        ).limit(
                15
        ).build();

        mapboxGeocoding.enqueueCall(new Callback<GeocodingResponse>()
        {
            @Override
            public void onResponse(Call<GeocodingResponse> call, Response<GeocodingResponse> response)
            {
                if (response.body() != null)
                {
                    List<CarmenFeature> results = response.body().features();

                    search_results.clear();

                    for (int index = 0; index < results.size(); index++)
                    {
                        search_results.add(
                                new SearchResult(results.get(index), userLocation)
                        );
                    }

                    adapter = new SearchResultsAdapter(myContext, search_results);

                    search_results_list.setAdapter(adapter);

                    search_results_list.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<GeocodingResponse> call, Throwable t)
            {
                Log.e("VERIZON", "search failed...");
            }
        });
    }


    private void zoomTo(LatLng incomingCoordinates)
    {
        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(incomingCoordinates).zoom(15).build()), 3000);
    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap)
    {
        NavPlus.this.mapboxMap = mapboxMap;

        // change map style here...
        mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded()
        {
            @Override
            public void onStyleLoaded(@NonNull Style style)
            {
                enableLocationComponent(style);
//                mapboxMap.addOnMapClickListener(NavPlus.this);
                symbolManager = new SymbolManager(mapView, mapboxMap, style);
                symbolManager.setTextAllowOverlap(true);
                symbolManager.setIconAllowOverlap(true);
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
//                    activity.userLocation = new Location();
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