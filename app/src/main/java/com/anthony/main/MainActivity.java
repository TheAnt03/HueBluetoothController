package com.anthony.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.anthony.bluetooth.BluetoothConnection;
import com.anthony.logs.Logger;
import com.example.huehack.R;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private BluetoothGatt bluetoothGatt;

    private Button waveButton;
    private Button alarmButton;

    private BluetoothConnection connection;
    private boolean on;
    private boolean toggle;
    private boolean toggle2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        on = false;
        toggle = false;
        toggle2 = false;

        Logger.getInstance().initializeLogView(findViewById(R.id.logView));

        connection = new BluetoothConnection(this);
        connection.initialize(getSystemService(Context.BLUETOOTH_SERVICE));

        // Initialize the sendButton
        waveButton = findViewById(R.id.button);

        // Set an onClickListener to the button
        waveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Call the function to send the value when the button is clicked
                try {
                    toggle2 = !toggle;
                    colorWave();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        alarmButton = findViewById(R.id.button2);

        alarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Call the function to send the value when the button is clicked
                try {
                    toggle = !toggle;

                    alarm();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void alarm() throws InterruptedException {
        connection.writeColor(new byte[]{(byte)0xB8, (byte)0x98, (byte)0x27, (byte)0x43});

        if(toggle) {
            MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.alarm);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }

        while(toggle) {
            connection.toggleOnOff();
            Thread.sleep(500);
        }
    }

    @SuppressLint("MissingPermission")
    private void colorWave() throws InterruptedException {
        int red = 255,green=0,blue=0,white=0;

        while(toggle2) {
            red -= 25;
            green +=25;
            blue += 10;
            white +=5;

            connection.writeColor((new byte[]{(byte)red, (byte)green, (byte)blue, (byte)white}));
            Thread.sleep(500);
        }
    }
}