package org.jkcsoft.jasmin.platform.guice.servlet;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import org.jkcsoft.jasmin.platform.shiro.BootstrapServletModule;

/**
 * This class goes mapped in web.xml and is used to inject Google Guice's Injector into the Web Application Context.
 * 
 * @author pablo.biagioli
 *
 */
public class GenericGuiceContextListener extends GuiceServletContextListener{

	@Override
	protected Injector getInjector() {
		return Guice.createInjector(new BootstrapServletModule());
	}

	
}
