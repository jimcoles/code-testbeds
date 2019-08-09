package org.jkcsoft.jasmin.platform.guice;

import com.google.inject.name.Names;
import com.google.inject.servlet.ServletModule;
import org.apache.shiro.guice.web.GuiceShiroFilter;
import org.jkcsoft.jasmin.platform.AppConfig;
import org.jkcsoft.jasmin.platform.AppHome;
import org.jkcsoft.jasmin.platform.GenericBootstrapConstants;
import org.jkcsoft.jasmin.platform.shiro.BootstrapShiroModule;
import org.jkcsoft.jasmin.platform.shiro.ShiroAnnotationsModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * This class bootstraps the application Servlet (JBoss RestEasy 3). If you want
 * the Shiro annotations to work, you will need to inject every Web Service's
 * constructor, so Guice's injector can handle the creation of the WS.
 *
 * @author pablo.biagioli
 *
 */
public class GuiceModuleRegistrar extends ServletModule {

    private static Logger log = LoggerFactory.getLogger(GuiceModuleRegistrar.class);
    private Properties bootstrapProperties;

    /*
    Guice
    Module.install( ) -
    Module.bind( ) - https://github.com/google/guice/wiki/Motivation
     */
    @Override
    protected void configureServlets() {
        super.configureServlets();
        log.info("Bootstrap Main Servlet");
        // get the bootstrapping Properties file
//        install(new BootstrapPropertiesModule());
        // Initialize Persistence JPA Unit of Work if present
        // install(new MyUnitOfWorkModule());

        loadBootstrapProperties();
        // Initialize Apache Shiro if present
        install(new BootstrapShiroModule(getServletContext()));
        //This allows Shiro AOP Annotations http://shiro.apache.org/java-authorization-guide.html
        install(new ShiroAnnotationsModule());

        // Binds the REST services -- the real reason for doing all of this!
        install(new RestServiceBindModule());
        //
        filter("/*").through(GuiceShiroFilter.class);
        filter("/*").through(ResteasyFilterDispatcher.class);
    }

    public Properties getBootstrapProperties() {
        return bootstrapProperties;
    }

    private void loadBootstrapProperties() {
        bootstrapProperties = new Properties();
        try {
            InputStream is = getClass().getResourceAsStream("/" + GenericBootstrapConstants.BOOTSTRAP_PROPERTIES_FILE);
            bootstrapProperties.load(is);
            // binds individual properties values to their name/keys for use of @Named injection ...
            Names.bindProperties(binder(), bootstrapProperties);
            //
            bind(AppConfig.class).toInstance(new AppConfig(bootstrapProperties));
            bind(AppHome.class);
        } catch (FileNotFoundException e) {
            log.error("The configuration file " + GenericBootstrapConstants.BOOTSTRAP_PROPERTIES_FILE
                          + " can not be found", e);
        } catch (IOException e) {
            log.error("I/O Exception during loading configuration", e);
        }
    }
}
