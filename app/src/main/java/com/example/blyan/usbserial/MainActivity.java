package com.example.blyan.usbserial;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        permissionIntent = PendingIntent.getBroadcast(this, 0, new Intent("com.example.blyan.usbserial.USB_PERMISSION"), 0);

        buttonConnect = findViewById(R.id.connect);
        buttonSend = findViewById(R.id.send);
        buttonSetup = findViewById(R.id.setup);
        buttonClear = findViewById(R.id.clear);
        textView = findViewById(R.id.textView);

        buttonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cp2102.connection != null){
                    return;
                }

                log += cp2102.connect(manager, permissionIntent, new com.example.blyan.usbserial.UsbReadCallback() {
                    @Override
                    public void onReceive(byte[] data, int length) {
                        log += "received: " + String.valueOf(length) + " bytes\n";
                        for (int i = 0; i < length; i++){
                            log += String.format("%02x", data[i]) + ", ";
                        }
                        log += "\n";
                        runOnUiThread(new Thread(new Runnable() {
                            @Override
                            public void run() {
                                textView.setText(log);
                            }
                        }));
                    }
                }) + "\n";
                textView.setText(log);
            }
        });

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] data = new byte[] {0x1, 0x2, 0x3, (byte)0xfd, (byte)0xfe, (byte)0xff};
                log += "send: " + cp2102.send(data) + "\n";
                textView.setText(log);
            }
        });

        buttonSetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                log += cp2102.setUsbCom(9600) + "\n";
                textView.setText(log);
            }
        });

        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setText(log = "");
            }
        });
    }

    @Override
    protected void onDestroy(){
        cp2102.releaseDevice();
        super.onDestroy();
    }

    private Button buttonConnect, buttonSend, buttonSetup, buttonClear;
    private TextView textView;
    private UsbManager manager;
    private PendingIntent permissionIntent;
    private UsbSerialCP210x cp2102 = new UsbSerialCP210x();
    private String log = "";
}
