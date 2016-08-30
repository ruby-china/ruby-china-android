package org.ruby_china.android;

import com.facebook.drawee.backends.pipeline.Fresco;

public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
    }
}
