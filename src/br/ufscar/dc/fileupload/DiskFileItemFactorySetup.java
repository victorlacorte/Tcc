package br.ufscar.dc.fileupload;

/*
 *  http://commons.apache.org/proper/commons-fileupload/using.html
 */

import javax.servlet.*;

import java.io.*;

import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.disk.*;
import org.apache.commons.fileupload.servlet.*;
import org.apache.commons.io.*;

public final class DiskFileItemFactorySetup {
    public static DiskFileItemFactory getDiskFileItemFactory(ServletContext context,
							     File repository) {
	FileCleaningTracker fileCleaningTracker =
	    FileCleanerCleanup.getFileCleaningTracker(context);

	DiskFileItemFactory factory =
	    new DiskFileItemFactory(DiskFileItemFactory.DEFAULT_SIZE_THRESHOLD,
				                                  repository);

	factory.setFileCleaningTracker(fileCleaningTracker);
	return factory;
    }
}
