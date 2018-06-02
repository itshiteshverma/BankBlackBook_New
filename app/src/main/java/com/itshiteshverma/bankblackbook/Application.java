package com.itshiteshverma.bankblackbook;

import com.crashlytics.android.Crashlytics;

import net.danlew.android.joda.JodaTimeAndroid;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Wilmar Africa Ltd on 19-06-17.
 */

public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        JodaTimeAndroid.init(this);

        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
