/*
* Copyright 2013 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/


package com.example.android.bluetoothchat;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.InputDevice;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.RelativeLayout;
import android.widget.ViewAnimator;
import android.widget.Button;

import com.example.android.common.activities.SampleActivityBase;
import com.example.android.common.logger.Log;
import com.example.android.common.logger.LogFragment;
import com.example.android.common.logger.LogWrapper;
import com.example.android.common.logger.MessageOnlyLogFilter;

import static android.view.InputDevice.SOURCE_MOUSE;
import static android.view.KeyEvent.ACTION_UP;
import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_HOVER_MOVE;
import static android.view.MotionEvent.ACTION_MOVE;
import static com.example.android.bluetoothchat.R.id.button1;

/**
 * A simple launcher activity containing a summary sample description, sample log and a custom
 * {@link android.support.v4.app.Fragment} which can display a view.
 * <p>
 * For devices with displays with a width of 720dp or greater, the sample log is always visible,
 * on other devices it's visibility is controlled by an item on the Action Bar.
 */
public class MainActivity extends SampleActivityBase implements OnTouchListener {

    public static final String TAG = "MainActivity";

    // Whether the Log Fragment is currently shown
    //private boolean mLogShown;
    private boolean chatflag=false;
    //private boolean button1flag=false;
    //private boolean button2flag=false;
    private MotionEvent motionevent;
    private int mouseid;
    private String x;
    private String y;
    private String oldx;
    private String oldy;
    private BluetoothChatFragment fragment=new BluetoothChatFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button1=(Button)findViewById(R.id.button1);
        Button button2=(Button)findViewById(R.id.button2);
        button1.setOnTouchListener(this);
        button2.setOnTouchListener(this);



        if (savedInstanceState == null) {

        }

        View container=findViewById(R.id.activity_main);


        container.setOnHoverListener(new View.OnHoverListener() {
            @Override
            public boolean onHover(View v, MotionEvent event){
                motionevent=event;
                if(fragment.mChatService.getState() == BluetoothChatService.STATE_CONNECTED && chatflag==false) {
                    chatflag = true;
                    int[] ids=InputDevice.getDeviceIds();
                    for(int i=0;i<ids.length;i++){
                        InputDevice device=InputDevice.getDevice(ids[i]);
                        if(device.getSources()==InputDevice.SOURCE_CLASS_POINTER){
                            mouseid=ids[i];
                            break;
                        }
                    }
                    RelativeLayout rl = (RelativeLayout) findViewById(R.id.activity_main);
                    String width=String.valueOf(rl.getWidth()*100000*9);
                    String height=String.valueOf(rl.getHeight()*100000*9);
                    if(width.length()<10)
                        width="0"+width;
                    if(height.length()<10)
                        height="0"+height;
                    fragment.sendMessage(width+","+height+"\n");
                }
                if(fragment.mChatService.getState() != BluetoothChatService.STATE_CONNECTED && chatflag==true){
                    chatflag=false;
                }
                if(fragment.mChatService.getState() == BluetoothChatService.STATE_CONNECTED) {
                    double X = event.getX(event.findPointerIndex(mouseid));
                    double Y = event.getY(event.findPointerIndex(mouseid));
                    if (X < 0)
                        X = 0;
                    if (Y < 0) {
                        Y = 0;
                    }
                    x = String.valueOf((int) X * 1000000);
                    y = String.valueOf((int) Y * 1000000);
                    while (x.length() < 10) {
                        x = "0" + x;
                    }
                    while (y.length() < 10) {
                        y = "0" + y;
                    }
                    oldx = x;
                    oldy = y;
                    fragment.sendMessage(x + "," + y + ",0,0\n");
                }
                return false;
            }
        });
    }

    @Override
    public boolean onTouch(View v,MotionEvent event){
        double X;
        double Y;
        X = event.getX(event.findPointerIndex(mouseid));
        Y = event.getY(event.findPointerIndex(mouseid));
        if (X < 0)
            X = 0;
        if (Y < 0) {
            Y = 0;
        }
        x = String.valueOf((int) X * 1000000);
        y = String.valueOf((int) Y * 1000000);
        while (x.length() < 10) {
            x = "0" + x;
        }
        while (y.length() < 10) {
            y = "0" + y;
        }

        switch(event.getAction()) {

            case ACTION_DOWN:
                switch (v.getId()) {
                    case R.id.button1:
                        fragment.sendMessage(oldx + "," + oldy + ",1,0\n");
                        fragment.sendMessage(oldx + "," + oldy + ",1,0\n");
                        fragment.sendMessage(oldx + "," + oldy + ",1,0\n");
                        break;
                    case R.id.button2:
                        fragment.sendMessage(oldx + "," + oldy + ",0,1\n");
                        fragment.sendMessage(oldx + "," + oldy + ",0,1\n");
                        fragment.sendMessage(oldx + "," + oldy + ",0,1\n");
                        break;
                }
                break;
            case ACTION_UP:
                fragment.sendMessage(oldx+","+oldy+",0,0\n");
                fragment.sendMessage(oldx+","+oldy+",0,0\n");
                fragment.sendMessage(oldx+","+oldy+",0,0\n");
                break;
        }
        return false;

    }


    /** Create a chain of targets that will receive log data */
    @Override
    public void initializeLogging() {
        // Wraps Android's native log framework.
        LogWrapper logWrapper = new LogWrapper();
        // Using Log, front-end to the logging chain, emulates android.util.log method signatures.
        Log.setLogNode(logWrapper);

        // Filter strips out everything except the message text.
        MessageOnlyLogFilter msgFilter = new MessageOnlyLogFilter();
        logWrapper.setNext(msgFilter);

        // On screen logging via a fragment with a TextView.
        LogFragment logFragment = (LogFragment) getSupportFragmentManager()
                .findFragmentById(R.id.log_fragment);
        msgFilter.setNext(logFragment.getLogView());

        Log.i(TAG, "Ready");
    }
}

