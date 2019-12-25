package map.finalproject.lonetech;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.card.MaterialCardView;

public class LandingPageFragment extends Fragment
{
    private NavPlus navPlus;
    private MaterialCardView navPlusPanel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.landing_page, container, false);

        navPlus = new NavPlus();
        navPlusPanel = view.findViewById(R.id.mapsPanel);
        navPlusPanel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.replaceablePanel, navPlus).commit();
            }
        });

        return view;
    }
}
