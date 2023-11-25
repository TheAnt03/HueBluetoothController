package com.anthony.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.util.Log;

import com.anthony.logs.Logger;

import java.util.UUID;

public class BluetoothConnection {
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothGatt bluetoothGatt;
    private String DEVICE_ADDRESS = "CF:D3:6C:87:77:8F";
    private UUID SERVICE_UUID = UUID.fromString("932c32bd-0000-47a2-835a-a8d455b859dd");
    private UUID ON_OFF_UUID = UUID.fromString("932c32bd-0002-47a2-835a-a8d455b859dd");
    private UUID COLOR_UUID = UUID.fromString("932c32bd-0005-47a2-835a-a8d455b859dd");

    private Context context;
    private BluetoothGattService sendService;
    private BluetoothGattCharacteristic onOffCharacteristic;
    private BluetoothGattCharacteristic colorCharacteristic;

    private boolean isOn;
    public BluetoothConnection(Context context) {
        this.isOn = true;
        this.context = context;
    }

    private BluetoothGattCallback establishCallback = new BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if(newState == BluetoothProfile.STATE_CONNECTED) {
                Logger.getInstance().append("Connected to GATT");
                gatt.discoverServices();
            }

            if(newState == BluetoothProfile.STATE_DISCONNECTED) {
                Logger.getInstance().append("Disconnected from GATT");
            }
        }

        @SuppressLint("MissingPermission")
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                // Get the specified service
                sendService = gatt.getService(SERVICE_UUID);
                if (sendService != null) {
                    colorCharacteristic = sendService.getCharacteristic(COLOR_UUID);
                    onOffCharacteristic = sendService.getCharacteristic(ON_OFF_UUID);

                    if (onOffCharacteristic != null) {
                        Logger.getInstance().append("Test Successful");
                        onOffCharacteristic.setValue(new byte[]{0x01});
                        gatt.writeCharacteristic(onOffCharacteristic);
                    }
                }
            }
        }

        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Logger.getInstance().append("Write successful");
            } else {
                Logger.getInstance().append("Write failed");
            }
        }
    };


    @SuppressLint("MissingPermission")
    public void connect() {
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(DEVICE_ADDRESS);
        bluetoothGatt = device.connectGatt(this.context, false, establishCallback);
    }

    private void initializeCharacteristics() {
        this.colorCharacteristic = sendService.getCharacteristic(COLOR_UUID);
        this.onOffCharacteristic = sendService.getCharacteristic(ON_OFF_UUID);
    }

    public void initialize(Object systemService) {
        final BluetoothManager bluetoothManager = (BluetoothManager) systemService;
        bluetoothAdapter = bluetoothManager.getAdapter();
        connect();
    }

    public BluetoothGatt getBluetoothGatt() {
        return bluetoothGatt;
    }

    public BluetoothGattCharacteristic getOnOffCharacteristic() {
        return onOffCharacteristic;
    }

    public BluetoothGattCharacteristic getColorCharacteristic() {
        return colorCharacteristic;
    }

    @SuppressLint("MissingPermission")
    private void writeToCharacteristic(BluetoothGattCharacteristic characteristic, byte[] message) {
        characteristic.setValue(message);
        this.bluetoothGatt.writeCharacteristic(characteristic);
    }

    public void writeColor(byte[] message) {
        BluetoothGattCharacteristic characteristic = this.colorCharacteristic;
        this.writeToCharacteristic(characteristic, message);
    }

    private void setState() {
        byte state = (byte)0x00;

        if(this.isOn) {
            state = (byte)0x01;
        }

        this.writeToCharacteristic(this.onOffCharacteristic, new byte[]{state});
    }

    public void toggleOnOff() {
        this.isOn = !this.isOn;
        this.setState();
    }

    public void switchOn() {
        this.isOn = true;
    }

    public void switchOff() {
        this.isOn = false;
    }

}
