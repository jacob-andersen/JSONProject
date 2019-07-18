package com.example.jsonproject;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

public class ShibeResultReceiver extends ResultReceiver {
    private Receiver receiver;

    public ShibeResultReceiver(Handler handler) {
        super(handler);
    }

    public void setReceiver(Receiver receiver) {
        this.receiver = receiver;
    }

    public interface Receiver {
        void onReceiveResult(int resultCode, Bundle resultData);
    }

    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (receiver != null) {
            receiver.onReceiveResult(resultCode, resultData);
        }
    }
}
