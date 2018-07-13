package com.futronictech.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.R;

public class StartActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Button mOnBoardButton = findViewById(R.id.btn_onboard);
        Button mAttendanceButton = findViewById(R.id.btn_attendance);

        mOnBoardButton.setOnClickListener(this);
        mAttendanceButton.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_start_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case R.id.abuja_branch:
                //TODO: Set the api to abuja branch;
                Toast.makeText(this,"Abuja branch selected", Toast.LENGTH_LONG).show();
                return true;
            case R.id.enugu_branch:
                //
                Toast.makeText(this,"Enugu branch selected", Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        int itemId = v.getId();
        switch (itemId) {
            case R.id.btn_onboard:
                // Open the onBoard activity
                break;
            case R.id.btn_attendance:
                // Open the attendance activity
                break;
        }
    }
}
