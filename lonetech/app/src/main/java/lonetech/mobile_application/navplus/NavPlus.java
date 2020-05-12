package lonetech.mobile_application.navplus;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mapbox.mapboxsdk.maps.MapView;

import map.finalproject.lonetech.R;

public class NavPlus extends Fragment
{
    private FloatingActionButton myLocationButton;

    private UserLocation userLocationModule;
    private Map mapModule;
    private Search searchModule;
    private Navigation navigationModule;
    private UtilityPanel utilityPanel;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        final View myView = inflater.inflate(R.layout.mapbox, container, false);

        myLocationButton = myView.findViewById(R.id.myLocationButton);

        mapModule = new Map(getActivity().getApplicationContext());
        userLocationModule = mapModule.getUserLocation();
        searchModule = new Search(userLocationModule, mapModule);
        utilityPanel = new UtilityPanel();

        FragmentTransaction fragmentTransaction_1 = getFragmentManager().beginTransaction();
        fragmentTransaction_1.replace(R.id.map_replaceable, mapModule).commit();
        FragmentTransaction fragmentTransaction_2 = getFragmentManager().beginTransaction();
        fragmentTransaction_2.replace(R.id.replaceablePanel, searchModule).commit();

        myLocationButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                utilityPanel.hide();
                mapModule.zoom(userLocationModule.getLocation());
            }
        });

        return myView;
    }

    @Override
    @SuppressWarnings( {"MissingPermission"})
    public void onStart()
    {
        super.onStart();
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onStop()
    {
        super.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}