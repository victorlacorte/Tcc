package br.ufscar.dc.web.filters;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.*;

import org.apache.commons.fileupload.servlet.ServletUpload;

// Nice try but not used for now
@Singleton
public class FileExtensionFilter implements Filter {

    private final FilterConfig filterConfig;
    private final DiskFileItemFactory diskFileItemFactory;

    @Inject
    public FileExtensionFilter(DiskFileItemFactory diskFileItemFactory) {
	this.diskFileItemFactory = diskFileItemFactory;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
	this.filterConfig = filterConfig;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
	    FilterChain chain)
	throws IOException, ServletException {

	HttpRequest httpRequest = (HttpRequest) request;

	boolean isMultipart = ServletFileUpload
	    .isMultipartContent(httpRequest);

	// If we are not dealing with a file upload, continue with the
	//  filter chain.
	if (isMultipart) {
	    List<FileUploadException> fileUploadExceptions =
		new List<FileUploadException>();
	    httpRequest.setAttribute("fileUploadErrorMessages",
		    fileUploadExceptions);

	    List<FileItem> fileItems = new List<String>();
	    httpRequest.setAttribute("fileItems", fileItems);

	    List<FileItem> allFileItems;

	    // Configure a repository to ensure a secure temp location is used.
	    ServletContext servletContext = this.filterConfig.getServletContext();
	    File repository = (File) servletContext
		.getAttribute("javax.servlet.context.tempdir");

	    // Will Guice Injector work here?
	    //DiskFileItemFactory diskFileItemFactory = DiskFileItemFactorySetup
	    //	.getDiskFileItemFactory(servletContext, repository);

	    try {
		allFileItems = new ServletFileUpload(this.diskFileItemFactory)
		    .parseRequest(httpRequest);
	    }
	    catch(FileUploadException fileEx) {
		// Log the exception and exit.
		fileUploadException.add(fileEx);
	    }
	    finally {
		// Continue with the filter chain and exit.
		chain.doFilter(request, response);
		return;
	    }

	    // We want to discard all fileItems that correspond to
	    // formFields.
	    for (FileItem fileItem : allFileItems) {
		if (fileItem.isFormField()) {
		    continue;
		}

		// Register all (actual) files.
		fileItems.add(fileItem);
	    }
	}

	chain.doFilter(request, response);
    }
}
