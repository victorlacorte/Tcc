package model.commons.fileupload;

public class FileUploadDiagnostic {

    private final boolean hasErrors;
    private final String errorMessage;

    public FileUploadDiagnostic(boolean hasErrors, String errorMessage) {
	this.hasErrors = hasErrors;
	this.errorMessage = errorMessage;
    }

    public boolean hasErrors() {
	return this.hasErrors;
    }

    public String getErrorMessage() {
	return this.errorMessage;
    }
}
