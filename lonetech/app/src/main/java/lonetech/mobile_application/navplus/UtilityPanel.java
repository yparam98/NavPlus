package lonetech.mobile_application.navplus;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import map.finalproject.lonetech.R;

public class UtilityPanel extends Fragment
{
    private MaterialCardView utility_panel;
    private MaterialTextView location_text_view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View utility_panel_layout = inflater.inflate(R.layout.utility_panel, container, false);

        utility_panel = utility_panel_layout.findViewById(R.id.utilitiesContainer);
        location_text_view = utility_panel_layout.findViewById(R.id.searchedLocationAddress);

        return utility_panel_layout;
    }

    public void setLocationTextView(String incoming_text)
    {
        location_text_view.setText(incoming_text);
    }

    public void show()
    {
        utility_panel.setVisibility(View.VISIBLE);
    }

    public void hide()
    {
        utility_panel.setVisibility(View.INVISIBLE);
    }


    UtilityPanel()
    {

    }
}
