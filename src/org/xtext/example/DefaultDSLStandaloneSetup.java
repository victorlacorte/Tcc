/*
* generated by Xtext
*/
package org.xtext.example;

/**
 * Initialization support for running Xtext languages 
 * without equinox extension registry
 */
public class DefaultDSLStandaloneSetup extends DefaultDSLStandaloneSetupGenerated{

	public static void doSetup() {
		new DefaultDSLStandaloneSetup().createInjectorAndDoEMFRegistration();
	}
}

