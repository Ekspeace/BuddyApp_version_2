package com.ekspeace.buddyapp_v2.Fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ekspeace.buddyapp_v2.Common.Common;
import com.ekspeace.buddyapp_v2.Common.PopUp;
import com.ekspeace.buddyapp_v2.Model.EventBus.CategoryLoadDoneEvent;
import com.ekspeace.buddyapp_v2.Model.EventBus.EnableNextButton;
import com.ekspeace.buddyapp_v2.Model.EventBus.GetUserAddressEvent;
import com.ekspeace.buddyapp_v2.Model.EventBus.NetworkConnectionEvent;
import com.ekspeace.buddyapp_v2.Model.EventBus.loadingEvent;
import com.ekspeace.buddyapp_v2.R;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.okhttp.Connection;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class BookingFragmentThree extends Fragment implements PermissionListener {
    private static BookingFragmentThree instance;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private String sAddress;
    public FusedLocationProviderClient fusedLocationProviderClient;
    private GoogleMap mMap;
    private View layout;
    private SearchView searchView;

    public static BookingFragmentThree getInstance() {
        if (instance == null) {
            instance = new BookingFragmentThree();
        }
        return instance;
    }

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            if (isPermissionGiven()) {
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                getCurrentLocation();
            } else {
                givePermission();
            }
        }
    };
        private boolean isPermissionGiven(){
            return ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        }
        private void givePermission() {
            Dexter.withActivity(getActivity())
                    .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    .withListener(this)
                    .check();
        }

    private BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {
        // below line is use to generate a drawable.
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);

        // below line is use to set bounds to our vector drawable.
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());

        // below line is use to create a bitmap for our
        // drawable which we have added.
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        // below line is use to add bitmap in our canvas.
        Canvas canvas = new Canvas(bitmap);

        // below line is use to draw our
        // vector drawable in canvas.
        vectorDrawable.draw(canvas);

        // after generating our bitmap we are returning our bitmap.
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_booking_three, container, false);
        searchView = view.findViewById(R.id.idSearchView);
        LayoutInflater in = getLayoutInflater();
        layout = in.inflate(R.layout.custom_toast, view.findViewById(R.id.custom_toast_container));
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            fusedLocationProviderClient = new FusedLocationProviderClient(getContext());
            setSearchView();
            mapFragment.getMapAsync(callback);
        }
    }
    private void setSearchView(){
        EventBus.getDefault().postSticky(new loadingEvent(true));
        if(Common.isOnline(getContext())) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    String location = searchView.getQuery().toString().trim();
                    if (location != "") {
                        Geocoder gcd = new Geocoder(getActivity());
                        List<Address> addressList = null;
                        getLocation(location, null, addressList, gcd);
                        EventBus.getDefault().postSticky(new loadingEvent(false));
                        return true;
                    } else {
                        EventBus.getDefault().postSticky(new loadingEvent(false));
                        return false;
                    }

                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    EventBus.getDefault().postSticky(new loadingEvent(false));
                    return false;
                }
            });
        }else {
            PopUp.Toast(getContext(), "Connection...", "Please check your internet connectivity and try again");
            EventBus.getDefault().postSticky(new loadingEvent(false));
        }
    }

    @Override
    public void onPermissionGranted(PermissionGrantedResponse response) {
        getCurrentLocation();
    }

    @Override
    public void onPermissionDenied(PermissionDeniedResponse response) {
        PopUp.smallToast(getContext(), layout, R.drawable.error,"Permission required for showing location",Toast.LENGTH_SHORT);
        getActivity().finish();
    }

    @Override
    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
     /*   permission: PermissionRequest?,
                token: PermissionToken?
    ) {
            token!!.continuePermissionRequest()
        }*/
        token.continuePermissionRequest();
    }
    private void getLocation(String location, Location mLastLocation, List<Address> addressList, Geocoder geocoder){
        Address address = null;
        if (location != null || mLastLocation != null) {
            try {
                if (location != null) {
                    addressList = geocoder.getFromLocationName(location, 1);
                    if (!addressList.isEmpty()) {
                        address = addressList.get(0);
                    } else {
                        PopUp.smallToast(getContext(), layout, R.drawable.error,"Please make sure you are not offline, and enter a correct address",Toast.LENGTH_SHORT);
                        return;
                    }
                } else
                    addressList = geocoder.getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1);
                if (!addressList.isEmpty()) {
                    address = addressList.get(0);
                } else {
                    PopUp.smallToast(getContext(), layout, R.drawable.error,"Please make sure you are not offline, and enter a correct address",Toast.LENGTH_SHORT);
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            LatLng latLng = null;
            if (address != null){
                latLng = new LatLng(address.getLatitude(), address.getLongitude());
                sAddress = address.getAddressLine(0);
                mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title("Current Location")
                        .icon(BitmapFromVector(getContext(), R.drawable.map_marker))
                );

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(latLng)
                        .zoom(17f)
                        .build();
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        }
    }
    private void getCurrentLocation() {

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval((10 * 1000));
        locationRequest.setFastestInterval(2000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getContext()).checkLocationSettings(locationSettingsRequest);
        result.addOnCompleteListener(task -> {
            try {
                LocationSettingsResponse response = task.getResult(ApiException.class);
                if (response.getLocationSettingsStates().isLocationPresent()) {
                    getLastLocation();
                }
            } catch (ApiException exception) {
            }
        });

    }
    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Location mLastLocation = task.getResult();
                String address = "No known address";
                Geocoder gcd = new Geocoder(getActivity(), Locale.getDefault());
                List<Address> addresses = null;
               getLocation(null, mLastLocation, addresses, gcd);
            } else {
                PopUp.smallToast(getContext(), layout, R.drawable.error,"No current location found",Toast.LENGTH_SHORT);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @org.jetbrains.annotations.Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            getCurrentLocation();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }
    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void userAddress(GetUserAddressEvent event) {
        if (event.isUserAddress()) {
            if(sAddress != null)
            EventBus.getDefault().postSticky(new EnableNextButton(3, sAddress));
            else {
                PopUp.smallToast(getContext(), layout, R.drawable.error,"Please turn on your location and try again, or search your location",Toast.LENGTH_SHORT);
                Common.step--;
            }
        }
    }
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void Connection(NetworkConnectionEvent event) {
        if (event.isNetworkConnected()) {
            setSearchView();
        }
    }
}