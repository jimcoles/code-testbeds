package org.jkcsoft.jasmin.platform;

import com.google.inject.Inject;

import javax.inject.Singleton;

/**
 * @author Jim Coles
 */
@Singleton
public class AppHome {

    private static AppHome instance;

//    public static AppHome getInstance() {
//        if (instance == null)
//            instance = new AppHome();
//
//        return instance;
//    }

    private AppConfig appConfig;

    @Inject
    public AppHome(AppConfig appConfig) {
        //no instance
        this.appConfig = appConfig;
    }

    public AppConfig getAppConfig() {
        return appConfig;
    }

}
