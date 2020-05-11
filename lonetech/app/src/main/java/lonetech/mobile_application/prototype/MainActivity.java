package lonetech.mobile_application.prototype;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import map.finalproject.lonetech.R;

public class MainActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setLandingPage();
    }

    private void setLandingPage()
    {
        LandingPageFragment landingPageFragment = new LandingPageFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.replaceablePanel, landingPageFragment).commit();
    }
}
