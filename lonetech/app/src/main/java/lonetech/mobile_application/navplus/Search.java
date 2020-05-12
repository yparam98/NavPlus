package lonetech.mobile_application.navplus;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.mapbox.api.geocoding.v5.GeocodingCriteria;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.mapbox.mapboxsdk.utils.BitmapUtils;

import java.util.ArrayList;
import java.util.List;

import map.finalproject.lonetech.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Search extends Fragment
{
    private Context application_context;

    private EditText search_box;
    private ListView search_results_list;
    private SymbolManager symbolManager;

    private SearchResultsAdapter adapter;
    private List<SearchResult> search_results = new ArrayList<>();

    UserLocation userLocation;
    UtilityPanel utilityPanel;
    Map map;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        application_context = getActivity();

        View search_layout = inflater.inflate(R.layout.search_bar, container, false);

        search_box = search_layout.findViewById(R.id.myLocationSearchField);
        ImageButton search_button = search_layout.findViewById(R.id.mySearchButton);
        search_results_list = search_layout.findViewById(R.id.search_results_list_view);

        search_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                utilityPanel.hide();
                search_on_map();
            }
        });

        return search_layout;
    }

    Search(UserLocation userLocationObj, Map mapObj)
    {
        userLocation = userLocationObj;
        utilityPanel = new UtilityPanel();
        map = mapObj;

        // once an item has been selected for search, display a marker and zoom in to the location
        search_results_list.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                symbolManager.deleteAll();

                search_results_list.setVisibility(View.INVISIBLE);
                Log.i("AT&T", "item #" + i + " clicked!");
                utilityPanel.setLocationTextView(search_results.get(i).getAddress());
                utilityPanel.show();
                search_box.setText(search_results.get(i).getAddress(), TextView.BufferType.NORMAL);

                Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_my_location_white_24dp, null);
                Bitmap bitmap = BitmapUtils.getBitmapFromDrawable(drawable);

                map.getMapboxMap().getStyle().addImage("my-marker", bitmap);

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

                map.zoom(new LatLng(search_results.get(i).getCoordinates().latitude(),search_results.get(i).getCoordinates().longitude()));
            }
        });
    }

    private void search_on_map()
    {
        String search_term = search_box.getText().toString();

        // use Geocoding API to search for places

        // creating search query
        final MapboxGeocoding mapboxGeocoding = MapboxGeocoding.builder().accessToken(
                getResources().getString(R.string.ACCESS_TOKEN)
        ).query(
                search_term
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
                Point.fromLngLat(userLocation.getLocation().getLongitude(), userLocation.getLocation().getLatitude())
        ).autocomplete(
                true
        ).limit(
                15
        ).build();

        // make call to API
        mapboxGeocoding.enqueueCall(new Callback<GeocodingResponse>()
        {
            @Override
            public void onResponse(Call<GeocodingResponse> call, Response<GeocodingResponse> response)
            {
                if (response.body() != null)
                {
                    List<CarmenFeature> results = response.body().features();

                    search_results.clear();

                    // generate auto completion prompts
                    for (int index = 0; index < results.size(); index++)
                    {
                        search_results.add(new SearchResult(results.get(index)));
                    }

                    // display auto completion prompts
                    adapter = new SearchResultsAdapter(application_context, search_results);
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




}
