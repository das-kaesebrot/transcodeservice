package eu.kaesebrot.dev.transcodeservice.utils;

public final class StringUtils {
    private StringUtils() {}

    public static boolean isNullOrEmpty(String string) {
        return (string == null || string.trim().isEmpty() || string.trim().isBlank());
    }

    public static String getFilenameWithoutExtension(String filename) {
        int pos = filename.lastIndexOf(".");

        if (pos > 0 && pos < (filename.length() - 1)) {
            return filename.substring(0, pos);
        }

        return filename;
    }

    public static String getFileExtension(String filename) {
        int pos = filename.lastIndexOf(".");

        if (pos > 0 && pos < (filename.length() - 1)) {
            return filename.substring(pos + 1);
        }

        return filename;
    }
}
