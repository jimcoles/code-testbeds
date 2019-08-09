package org.jkcsoft.jasmin.platform.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

/**
 * This class is mapped in web.xml and is used to inject Google Guice's Injector into the
 * Web Application Context.
 *
 * @author pablo.biagioli
 *
 */
public class GuiceInjectorContextListener extends GuiceServletContextListener {

    @Override
    protected Injector getInjector() {
        return Guice.createInjector(new GuiceModuleRegistrar());
    }

}
