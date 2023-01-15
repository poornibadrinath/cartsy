package com.poorni.cartitest;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;

public class About extends AppCompatActivity {
    
    static DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // Initialize and assign variable
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);

        // Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.about);

        // Perform item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId())
                {
                    case R.id.dashboard:
                        startActivity(new Intent(getApplicationContext(),Dashboard.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.about:
                        return true;
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

        @SuppressLint("MissingInflatedId") ListView list_view = findViewById(R.id.list);


        dbHelper = new DatabaseHelper(this);
        try {
            dbHelper.createDataBase();
        } catch (IOException ioe) {
        }
        SQLiteDatabase database = dbHelper.getDataBase();

        Cursor dbCursor = database.rawQuery("SELECT name FROM dresden_locations;", null);

        int length = dbCursor.getCount();
        dbCursor.moveToFirst();

        String[] univ_names = new String[length];

        for (int i = 0; i < length; i++) {

            univ_names[i] = dbCursor.getString(0);
            dbCursor.moveToNext();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.list_item, univ_names);

        list_view.setAdapter(adapter);

        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dbCursor.moveToPosition(position);
                String uni_name = dbCursor.getString(0);
                Intent intent = new Intent (About.this, MapsActivity.class);
                intent.putExtra("name_extra", uni_name);
                startActivity(intent);

            }
        });
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }

    public void onClickStartMap(View view) {
        Intent intent = new Intent (About.this, MapsActivity.class);
        startActivity(intent);
    }

}
