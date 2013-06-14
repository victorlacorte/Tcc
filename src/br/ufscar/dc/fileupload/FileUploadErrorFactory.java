package model.commons.fileupload;

public final class FileUploadErrorFactory {
	public static String getFormFieldMessage() {
		return "File upload error: request represents a simple form field";
	}

	public static String getSizeMessage() {
		return "File upload error: size is bigger than 50 kB";
	}

	public static String getFileExtensionMessage() {
		return "File upload error: expected extension 'prova'";
	}
}
