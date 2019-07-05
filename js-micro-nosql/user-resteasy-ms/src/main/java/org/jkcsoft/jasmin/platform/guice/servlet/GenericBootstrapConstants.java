package org.jkcsoft.jasmin.platform.guice.servlet;

/**
 * Generic constants for Bootstrapping the WebApp
 * 
 * @author pablo.biagioli
 *
 */
public class GenericBootstrapConstants {

	/**
	 * packages where the RestEasy Resources are, separated by commas
	 */
	public static final String REST_EASY_REST_PACKAGES="org.jkcsoft.jasmin.services";
	
	public static final String REST_EASY_REST_PACKAGES_SUFFIX="\\.";
	
	/**
	 * main properties file that gets loaded at first
	 */
	public static final String BOOTSTRAP_PROPERTIES_FILE="bootstrap.properties";
}
