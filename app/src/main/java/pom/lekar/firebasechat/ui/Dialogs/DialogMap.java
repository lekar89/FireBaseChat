package pom.lekar.firebasechat.ui.Dialogs;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import pom.lekar.firebasechat.R;

/**
 * Created by lekar on 19.05.17.
 */

public class DialogMap extends DialogFragment {


    private MapView mMapView;
    private GoogleMap googleMap;
    private double lat;
    private double lon;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setTitle("MAP!");
        lat=Double.parseDouble(getArguments().getString("LAT"));
        lon=Double.parseDouble(getArguments().getString("LON"));
        View rootView = inflater.inflate(R.layout.dialog_item_map, container, false);

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                // For showing a move to my location button
                //googleMap.setMyLocationEnabled(true);

                // For dropping a marker at a point on the Map
                LatLng sydney = new LatLng(lat, lon);
                googleMap.addMarker(new MarkerOptions().position(sydney));

                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(16).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}

//    final String LOG_TAG = "myLogs";
//
//    SupportMapFragment mapFragment;
//    GoogleMap map;
//
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        getDialog().setTitle("Title!");
//        View v = inflater.inflate(R.layout.dialog_item_map, null);
//
//
//        mapFragment = (SupportMapFragment) getFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(new OnMapReadyCallback() {
//            @Override
//            public void onMapReady(GoogleMap mGoogleMap) {
//                map.addMarker(new MarkerOptions()
//                        .position(new LatLng(0, 0))
//                        .title("Marker"));
//            }
//        });
//        return v;
//    }
//
//    public void onClick(View v) {
//        Log.d(LOG_TAG, "Dialog 1: " + ((Button) v).getText());
//        dismiss();
//    }
//
//    public void onDismiss(DialogInterface dialog) {
//        super.onDismiss(dialog);
//        Log.d(LOG_TAG, "Dialog 1: onDismiss");
//    }
//
//    public void onCancel(DialogInterface dialog) {
//        super.onCancel(dialog);
//        Log.d(LOG_TAG, "Dialog 1: onCancel");
//    }
//}