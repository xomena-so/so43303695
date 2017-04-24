package com.xomena.so43303695;

import android.graphics.Color;
import android.graphics.Point;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnCameraMoveStartedListener,
        GoogleMap.OnCameraIdleListener {

    private GoogleMap mMap;
    private LatLng sydney1;
    private LatLng sydney2;
    private int iconRadiusPixels = 192;
    Polyline poly;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnCameraMoveStartedListener(this);
        mMap.setOnCameraIdleListener(this);

        mMap.getUiSettings().setZoomControlsEnabled(true);

        // Add a marker in Sydney and move the camera
        sydney1 = new LatLng(-33.904438,151.249852);
        sydney2 = new LatLng(-33.905823,151.252422);

        BitmapDescriptor iconDescr = BitmapDescriptorFactory.fromResource(R.drawable.ic_circle);

        mMap.addMarker(new MarkerOptions().position(sydney1).anchor(0.5F, 0.5F)
                .draggable(false).visible(true).title("Marker in Sydney 1")
                .icon(iconDescr));
        mMap.addMarker(new MarkerOptions().position(sydney2).anchor(0.5F, 0.5F)
                .draggable(false).visible(true).title("Marker in Sydney 2")
                .icon(iconDescr));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney1, 17F));
    }

    @Override
    public void onCameraMoveStarted(int reason) {

    }

    @Override
    public void onCameraIdle() {
        if (this.poly != null) {
            this.poly.remove();
            this.poly = null;
        }
        this.showCustomPolyline();
    }

    private void showCustomPolyline () {
        //Get projection
        Projection proj = mMap.getProjection();
        //Get a point on the screen that corresponds to first marker
        Point p = proj.toScreenLocation(sydney1);
        //Lets create another point that is shifted on number of pixels iqual to icon radius
        //This point is located on circle border
        Point b = new Point(p.x + iconRadiusPixels, p.y);
        //Get the LatLng for a point on the circle border
        LatLng l = proj.fromScreenLocation(b);
        //Calculate the radius of the icon (distance between center and point on the circle border)
        double r = SphericalUtil.computeDistanceBetween(sydney1,l);
        //Calculate heading from point 1 to point 2 and from point 2 to point 1
        double heading1 = SphericalUtil.computeHeading(sydney1, sydney2);
        double heading2 = SphericalUtil.computeHeading(sydney2, sydney1);

        //Calculate real position where the polyline starts and ends taking into account radius and heading
        LatLng pos1 = SphericalUtil.computeOffset(sydney1, r, heading1);
        LatLng pos2 = SphericalUtil.computeOffset(sydney2, r, heading2);

        //Create polyline
        PolylineOptions options = new PolylineOptions();
        options.add(pos1);
        options.add(pos2);

        poly = mMap.addPolyline(options.width(4).color(Color.RED).geodesic(false));
    }
}
