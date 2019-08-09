package org.jkcsoft.jasmin.platform.ws.rs;

import org.jkcsoft.jasmin.platform.ws.ServiceRegsitry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Jim Coles
 */
public class AbstractWebService {

    private ServiceRegsitry serviceRegsitry;
    private final HttpServletRequest request;
    private final HttpServletResponse response;

    public AbstractWebService(ServiceRegsitry serviceRegsitry, HttpServletRequest request, HttpServletResponse response)
    {
        this.serviceRegsitry = serviceRegsitry;
        this.request = request;
        this.response = response;
    }

    public ServiceRegsitry getServiceRegsitry() {
        return serviceRegsitry;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }
}
