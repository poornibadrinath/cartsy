package com.poorni.cartitest;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    SupportMapFragment mapFragment;

    String intent_extra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Intent intent = getIntent();
        intent_extra = intent.getStringExtra("name");

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        SQLiteDatabase database = About.dbHelper.getDataBase();

        if (intent_extra != null) {
            Cursor dbCursor = database.rawQuery("SELECT * FROM dresden_locations WHERE hint LIKE '" + intent_extra + "';", null);
            dbCursor.moveToFirst();

            LatLng uni_pos = new LatLng(dbCursor.getDouble(2), dbCursor.getDouble(3));

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(uni_pos, 14));

            mMap.addMarker(new MarkerOptions()
                    .position(uni_pos)
                    .title(dbCursor.getString(0))
                    .snippet(dbCursor.getString(1)));
        }
        else {
            Cursor dbCursor = database.rawQuery("SELECT * FROM dresden_locations;", null);
            dbCursor.moveToFirst();

            LatLngBounds.Builder builder = LatLngBounds.builder();

            for (int i = 0; i < dbCursor.getCount(); i++) {
                LatLng uni_pos = new LatLng(dbCursor.getDouble(2), dbCursor.getDouble(3));

                mMap.addMarker(new MarkerOptions()
                        .position(uni_pos)
                        .title(dbCursor.getString(0))
                        .snippet(dbCursor.getString(1)));

                builder.include(uni_pos);

                dbCursor.moveToNext();
            }

            LatLngBounds bounds = builder.build();
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 200));

        }
    }

    @Override
    protected void onDestroy() {
        About.dbHelper.close();
        super.onDestroy();
    }

}