package br.ufscar.dc.web;

import com.google.inject.Singleton;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

@Singleton
public class FileDownloadServlet extends HttpServlet {

    /*
     *	The file that will be available for download is known beforehand.
     *	If this is not enough, maybe some logic with the extension should be
     *	enough.
     */
    private static final String FILE_NAME = "greetings.txt";
    private static final String FILE_DIR = "/WEB-INF";
    private static final int BUF_SIZE = 4096;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
	// Preprocess request: we actually don't need to do any business
	// stuff, so just display the JSP.
	request.getRequestDispatcher("/WEB-INF/download.jsp")
		.forward(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {

	/*
	 *  Verify the file is in the actual directory.
	 *  Send the response to the client.
	 *
	 *  For now, assume the user did not manually type the url.
	 *
	 *  If the file name becomes an issue, a filter could specify the user
	 *  id and the previous servlet could set an attribute to warn about
	 *  which user actually uploaded the file.
	 */

	/*
	 *  This is most likely going to change, since we need
	 *  to make a download file unique across multiple
	 *  simultaneous requisitions.
	 */

	ServletContext servletContext = this.getServletConfig()
	    .getServletContext();

	String absoluteDiskPath = servletContext
	    .getRealPath(FILE_DIR);

	String filePath = absoluteDiskPath + "/" + FILE_NAME;
	File file = new File(filePath);

	ServletOutputStream outStream = response
	    .getOutputStream();

	String mimeType = servletContext.getMimeType(filePath);

	/*
	 *  Force a generic MIME type instead.
	 */
	if (mimeType == null) {
	    mimeType = "application/octet-stream";
	}

	/*
	 *  Set the response content type.
	 */
	response.setContentType(mimeType);
	response.setContentLength((int)file.length());

	/*
	 *  Set the HTTP header.
	 */
	response.setHeader("Content-Disposition", "attachment; filename=\""
		+ FILE_NAME + "\"");

	byte[] byteBuffer = new byte[BUF_SIZE];
	DataInputStream in = new DataInputStream(new FileInputStream(file));

	/*
	 * Read the file's bytes and write them to the response stream.
	 */
	int length = 0;
	while ((in != null) && (length = in.read(byteBuffer)) != -1) {
	    outStream.write(byteBuffer, 0, length);
	}

	in.close();

	// Closing is not actually necessary.
	outStream.close();

    }
}
