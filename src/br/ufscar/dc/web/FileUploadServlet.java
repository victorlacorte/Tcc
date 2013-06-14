package br.ufscar.dc.web;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.commons.io.IOUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

import org.eclipse.xtext.generator.IGenerator;
import org.eclipse.xtext.generator.JavaIoFileSystemAccess;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.util.CancelIndicator;
import org.eclipse.xtext.validation.CheckMode;
import org.eclipse.xtext.validation.IResourceValidator;
import org.eclipse.xtext.validation.Issue;

import br.ufscar.dc.fileupload.DiskFileItemFactorySetup;

import org.xtext.example.defaultDSL.Model;
import org.xtext.example.defaultDSL.Greeting;

// TODO the DiskFileItemFactory part of this servlet should be handled by Guice
@Singleton
public class FileUploadServlet extends HttpServlet {

    /*
     * Magically inject all dependencies.
     */
    private final Provider<ResourceSet> resourceSetProvider;
    private final IGenerator generator;
    private final IResourceValidator validator;
    private final JavaIoFileSystemAccess fileAccess;

    @Inject
    public FileUploadServlet(Provider<ResourceSet> resourceSetProvider,
	    IGenerator generator, IResourceValidator validator,
	    JavaIoFileSystemAccess fileAccess) {
	this.resourceSetProvider = resourceSetProvider;
	this.generator = generator;
	this.validator = validator;
	this.fileAccess = fileAccess;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
	// Preprocess request: we actually don't need to do any business
	// stuff, so just display the JSP.
	request.getRequestDispatcher("/WEB-INF/index.jsp")
		.forward(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {

	// Verify that we have an actual file upload request.
	boolean isMultipart = ServletFileUpload.isMultipartContent(request);

	if (!isMultipart) {
	    // Something wrong has happened. This servlet should not have been
	    // handled the request.
	    //
	    // TODO sendRedirect() accordingly.
	    return;
	}

	/*
	 * Prepare client error messages.
	 */
	Map<String, String> messages = new HashMap<String, String>();
	request.setAttribute("messages", messages);

	/*
	 *  Start processing the request. File items consist of form fields and
	 *  actual files; in this case, the uploaded file.
	 */
	List<FileItem> fileItems;

	// Configure a repository to ensure a secure temp location is used
	ServletContext servletContext = this.getServletConfig()
	    .getServletContext();
	File repository = (File) servletContext
	    .getAttribute("javax.servlet.context.tempdir");

	DiskFileItemFactory diskFileItemFactory =
	    DiskFileItemFactorySetup.getDiskFileItemFactory(
		    servletContext, repository);

	try {
	    fileItems = new ServletFileUpload(diskFileItemFactory)
		.parseRequest(request);
	}
	catch (FileUploadException fileEx) {
	    // TODO dispatch to an error handler jsp
	    //fileEx.printStackTrace(out);
	    return;
	}
	finally {
	    //TODO dispatch here
	}

	ResourceSet resourceSet = this.resourceSetProvider.get();
	Resource resource = resourceSet.createResource(URI
		.createURI("dummy:/example.ddsl"));

	/*
	 *  Mechanism to warn the client if no actual file was sent on the
	 *  request.
	 *
	 *  Form fields are non-file items. We begin assuming all elements
	 *  present on the request are form fields.
	 */
	int totalFormFields = fileItems.size();

	for(FileItem fileItem : fileItems) {
	    /*
	     *	We're not interested in simple form fields nor empty file
	     *	submissions (addressed by the empty file name).
	     */
	    if (fileItem.isFormField() || fileItem.getName() =="") {
		// One less form field to process.
		totalFormFields--;
		continue;
	    }

	    // If the current FileItem is not a form field, than it is a file.

	    // Java.io.InputStream;
	    InputStream in = fileItem.getInputStream();

	    resource.load(in, resourceSet.getLoadOptions());

	    List<Issue> issues = this.validator.validate(resource,
		    CheckMode.ALL, CancelIndicator.NullImpl);

	    if (!issues.isEmpty()) {
		for (Issue issue : issues) {
		    messages.put("Line " + issue.getLineNumber().toString(),
			    issue.getMessage());
		}

		request.getRequestDispatcher("/WEB-INF/index.jsp")
		    .forward(request, response);
		return;
	    }

	    // Currently, we process only one file per request.
	    break;
	}

	/*
	 *  If all file items have been processed and the amount of form fields
	 *  has reached zero, then our request had no actual uploaded file.
	 */
	if (totalFormFields == 0) {
	    messages.put("Error", "No file chosen");

	    request.getRequestDispatcher("/WEB-INF/index.jsp")
		.forward(request, response);
	    return;
	}

	String relativeWebPath = "/WEB-INF";
	String absoluteDiskPath = servletContext
	    .getRealPath(relativeWebPath);
	this.fileAccess.setOutputPath(absoluteDiskPath);
	this.generator.doGenerate(resource, fileAccess);

	//messages.put("Success","Code generation finished");

	/*
	 *  After all the work has been done in this servlet, redirect the
	 *  client to the actual download servlet.
	 */
	response.sendRedirect("download");
    }
}
