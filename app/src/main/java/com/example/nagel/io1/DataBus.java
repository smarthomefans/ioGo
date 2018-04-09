package com.example.nagel.io1;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

public class DataBus {
    private static Bus sBus;
    public static Bus getBus() {
        if (sBus == null)
            sBus = new Bus(ThreadEnforcer.ANY);
        return sBus;
    }
}