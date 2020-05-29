package com.example.acmcovidapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;

import com.example.acmcovidapplication.db.DatabaseHelper;
import com.example.acmcovidapplication.services.CustomService;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import pub.devrel.easypermissions.EasyPermissions;

import static com.example.acmcovidapplication.Util.isMyServiceRunning;

public class permission_list extends AppCompatActivity implements Switch.OnCheckedChangeListener {
    private static final String TAG = "permission_list" ;
    Switch aSwitch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission_list);
        aSwitch = findViewById(R.id.switch1);
        aSwitch.setOnCheckedChangeListener(this);
        aSwitch.setChecked(DatabaseHelper.getInstance(this).isLocationTrackable());
    }

    public void goToShare(View view) {
        Intent intent = new Intent(permission_list.this, share.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        DatabaseHelper.getInstance(this).insertAllowed(true);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            DatabaseHelper.getInstance(this).insertLocationTrackable(isChecked);
            LocalBroadcastManager.getInstance(this).sendBroadcast(CustomService.makeIntent(isChecked));
            RelativeLayout box=findViewById(R.id.l2);
            if (isChecked){
                box.setVisibility(View.VISIBLE);
            }else{
                box.setVisibility(View.GONE);
            }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: is running");
        if (!EasyPermissions.hasPermissions(this, Util.getPermissions()) ) {
            DatabaseHelper.getInstance(this).insertAllowed(false);
            stopService(new Intent(this,CustomService.class));
            Intent intent = new Intent(this, appPermission.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        } else if( !isMyServiceRunning(this,CustomService.class)) {
            Log.d(TAG, "onResume: service is not running");
            Intent serviceIntent = new Intent(this, CustomService.class);

            ContextCompat.startForegroundService(this, serviceIntent);
        }
    }






}
