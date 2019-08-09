package org.jkcsoft.jasmin.platform.ws;

import com.google.inject.Inject;
import org.jkcsoft.jasmin.platform.AppConfig;

import javax.ws.rs.core.UriBuilder;

/**
 * @author Jim Coles
 */
public class ServiceRegistryImpl implements ServiceRegsitry {

    private AppConfig appConfig;

    @Inject
    public ServiceRegistryImpl(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    @Override
    public UriBuilder getServiceUri(Object key) {
        return null;
    }

}
