package model.commons.fileupload;

import org.apache.commons.fileupload.*;
import org.apache.commons.io.FilenameUtils;


// TODO should this class be static?
public class FileUploadManager {

    /*
     *	When obtaining a file upload from a client, the webapp must verify if
     *	the file has the correct file extension, as defined in the MWE2 file
     *	that was constructed along with the DSL. In this case, we expect the
     *	extension "prova".
     */
    public static final String FILE_EXTENSION = "prova";

    public static FileUploadDiagnostic checkFileItem(FileItem fileItem) {

	if (fileItem.isFormField()) {
	    // We are not expecting a simple form field
	    //return new FileUploadDiagnostic(true,
	    //    FileUploadErrorFactory.getFormFieldMessage());
	    return new FileUploadDiagnostic(true, null);
	}

	String fileItemExtension = FilenameUtils.getExtension(fileItem.getName());

	if (!fileItemExtension.equalsIgnoreCase(FILE_EXTENSION)) {
	    return new FileUploadDiagnostic(true,
		    FileUploadErrorFactory.getFileExtensionMessage());
	}

	// We have the expected file
	return new FileUploadDiagnostic(false, null);
    }
}
