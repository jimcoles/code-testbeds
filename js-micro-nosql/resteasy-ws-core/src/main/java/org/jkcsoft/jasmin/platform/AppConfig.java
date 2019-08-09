package org.jkcsoft.jasmin.platform;

import javax.inject.Singleton;
import java.util.Properties;

/**
 * @author Jim Coles
 */
@Singleton
public class AppConfig {

    private String[] serviceClasses;

    void setRestServiceClasses(String[] classes) {
        this.serviceClasses = classes;
    }

    public AppConfig(Properties bootstrapProperties) {
        String[] classNames = bootstrapProperties.getProperty(GenericBootstrapConstants.REST_EASY_CLASSES).split(",");
        for (int i = 0; i < classNames.length; i++) {
            classNames[i] = classNames[i].trim();
        }
        this.setRestServiceClasses(classNames);
    }

    public String[] getServiceClasses() {
        return serviceClasses;
    }
}
