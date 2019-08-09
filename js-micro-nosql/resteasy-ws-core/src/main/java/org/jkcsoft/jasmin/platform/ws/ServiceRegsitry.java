package org.jkcsoft.jasmin.platform.ws;

import javax.ws.rs.core.UriBuilder;

/**
 * @author Jim Coles
 */
public interface ServiceRegsitry {

    UriBuilder getServiceUri(Object key);
}
