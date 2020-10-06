package com.example.uberforhotels.fragments;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.uberforhotels.Other.DBHelper;
import com.example.uberforhotels.Other.Helper;
import com.example.uberforhotels.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;

public class Hotel_address extends Fragment implements OnMapReadyCallback {

    SearchView searchView;
    View bottomSheetLayout;
    TextView sheetAddressLine, sheetCity;
    Button sheetButton;
    ImageView sheetArrowImage;

    com.example.uberforhotels.models.Address hotel_address;

    MarkerOptions markerOptions;
    Marker marker;

    private GoogleMap map;
    private Geocoder geocoder;
    private LatLng position;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_hotel_address, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        hotel_address = new com.example.uberforhotels.models.Address();

        searchView = view.findViewById(R.id.search_bar);
        bottomSheetLayout = view.findViewById(R.id.bottom_sheet);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        geocoder = new Geocoder(getContext());
        position = new LatLng(0,0);
        markerOptions = new MarkerOptions().draggable(true);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        sheetAddressLine = bottomSheetLayout.findViewById(R.id.address_line);
        sheetCity = bottomSheetLayout.findViewById(R.id.sheet_city);
        sheetButton = bottomSheetLayout.findViewById(R.id.save_btn);
        sheetArrowImage = bottomSheetLayout.findViewById(R.id.arrow_img);

        final String uid = Helper.getHotelIdFromPreference(getContext());
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("Hotels").child(uid).child("address").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    hotel_address = dataSnapshot.getValue(com.example.uberforhotels.models.Address.class);
                    placeMarker(new LatLng(hotel_address.getLat(), hotel_address.getLng()));
                }else{
                    placeMarker(position);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Helper.toast(getContext(), databaseError.toString());
            }
        });

        sheetButton.setOnClickListener(view -> DBHelper.addAddress(hotel_address, getContext()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if(s != null || !s.equals("")){
                    try {
                        map.clear();
                        List<Address> addressList = geocoder.getFromLocationName(s, 1);
                        Address address = addressList.get(0);
                        position = new LatLng(address.getLatitude(), address.getLongitude());
                        placeMarker(position);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        map = googleMap;

        map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                updateUI(marker);
            }
        });
    }

    private void placeMarker(LatLng defaultPosition){
        markerOptions.position(defaultPosition);
        marker = map.addMarker(markerOptions);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(defaultPosition, 12));

        updateUI(marker);
    }

    private void updateUI(Marker marker) {

        try {
            LatLng latLng = marker.getPosition();
            hotel_address.setLat(latLng.latitude);
            hotel_address.setLng(latLng.longitude);
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if(addresses.size() > 0){
                Address address = addresses.get(0);
                marker.setTitle(address.getAddressLine(0));
                Helper.toast(getContext(), address.getSubLocality());
                if(address.getSubLocality() == null) {
                    sheetAddressLine.setText(address.getFeatureName());
                    hotel_address.setAddressLine(address.getFeatureName());
                }
                else {
                    sheetAddressLine.setText(address.getFeatureName() + ", " + address.getSubLocality());
                    hotel_address.setAddressLine(address.getFeatureName() + ", " + address.getSubLocality());
                }
                sheetCity.setText(address.getSubAdminArea());
                hotel_address.setCity(address.getSubAdminArea());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("error", e.toString());
        }
    }

}
