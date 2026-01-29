package com.xoraano.deliveryboy.service;

import android.Manifest;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.xoraano.deliveryboy.R;
import com.xoraano.deliveryboy.Common.Common;
import com.xoraano.deliveryboy.Model.MyLocation;

import static com.xoraano.deliveryboy.application.App.CHANNEL_ID;

public class MyLocationService extends Service {
    private static final String TAG = "MyLocationService";

    FirebaseDatabase db;
    DatabaseReference OrderRequests, delivery_persons;

    private PowerManager.WakeLock wakeLock;

    FusedLocationProviderClient mFusedLocationProviderClient;

    DatabaseReference app_parameters;
    int track_location_interval = 50;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //Log.d(TAG, "onCreate");

        db = FirebaseDatabase.getInstance();
        OrderRequests = db.getReference("OrderRequests");
        delivery_persons = db.getReference("deliveryPersons");
        app_parameters = db.getReference("AppParameters");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "ExampleApp:Wakelock");
        wakeLock.acquire(3*1000*60*10);
        //Log.d(TAG, "Wakelock acquired");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Sending Location updates")
                    .setContentText("Running...")
                    .setSmallIcon(R.drawable.ic_arrow_left_black_48dp)
                    .build();

            startForeground(20, notification);
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Log.d(TAG, "onStartCommand: called.");

        app_parameters.child("track_location_interval").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                track_location_interval = Integer.parseInt(dataSnapshot.getValue(String.class));
                getLocation();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                getLocation();
            }
        });

        return START_NOT_STICKY;
    }

    private void getLocation() {

        // ---------------------------------- LocationRequest ------------------------------------
        // Create the location request to start receiving updates
        LocationRequest mLocationRequestHighAccuracy = new LocationRequest();
        mLocationRequestHighAccuracy.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequestHighAccuracy.setInterval(track_location_interval);
        mLocationRequestHighAccuracy.setFastestInterval(track_location_interval);


        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Log.d(TAG, "getLocation: stopping the location service.");
            stopSelf();
            return;
        }
        //Log.d(TAG, "getLocation: getting location information.");
        mFusedLocationProviderClient.requestLocationUpdates(mLocationRequestHighAccuracy, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {

                        Location location = locationResult.getLastLocation();

                        if (location != null) {
                            MyLocation del_boy_lcation = new MyLocation(location.getLatitude(), location.getLongitude());
                            delivery_persons.child(Common.currentperson.getMobileNo()).child("my_location").setValue(del_boy_lcation).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(MyLocationService.this, "Tracking your Location....", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                },
                Looper.myLooper()); // Looper.myLooper tells this to repeat forever until thread is destroyed
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
    }
}
