package br.ufscar.dc;

import com.google.inject.*;
import com.google.inject.servlet.*;

import org.xtext.example.DefaultDSLRuntimeModule;
import org.xtext.example.DefaultDSLStandaloneSetupGenerated;

//import br.ufscar.dc.modules.ServicesModule;
import br.ufscar.dc.web.FileUploadServlet;
import br.ufscar.dc.web.FileDownloadServlet;

public class TccBootstrap extends GuiceServletContextListener {

    @Override
    protected Injector getInjector() {

	// Bind all of our services dependencies.
	//final Module services = new ServicesModule();
	//
	// Xtext-related binding.
	org.eclipse.xtext.common.TerminalsStandaloneSetup.doSetup();
	final Module xtextModule = new DefaultDSLRuntimeModule();

	// All configuration regarding servlets will be done here.
	final Module servlets = new ServletModule() {
	    @Override
	    protected void configureServlets() {

		//filter("/fileUpload").through(FileUploadExtensionFilter.class);

		serve("/index").with(FileUploadServlet.class);
		serve("/download").with(FileDownloadServlet.class);
		//serve("/FileUpload.do").with(FileUploadServlet.class);
	    }
	};

	// First attempt: register the same way Xtext does.
	// Create the Injector and return all the configuration.
	Injector injector = Guice.createInjector(xtextModule, servlets);

	DefaultDSLStandaloneSetupGenerated defaultStandalone =
	    new DefaultDSLStandaloneSetupGenerated();

	// Perform EMF Registration.
	defaultStandalone.register(injector);

	return injector;
    }
}
