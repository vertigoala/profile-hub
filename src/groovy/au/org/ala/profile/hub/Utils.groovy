package au.org.ala.profile.hub

import org.apache.tika.config.TikaConfig
import org.apache.tika.mime.MimeTypeException

import java.nio.file.Files
import java.util.regex.Matcher
import java.util.regex.Pattern

class Utils {
    static final String UUID_REGEX_PATTERN = "[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}"
    /** Regex used to parse content-disposition headers */
    private static final Pattern CONTENT_DISPOSITION_PATTERN = Pattern.compile(/filename\s*=\s*"([^"]*)"/)

    static String getCCLicenceIcon(String ccLicence) {
        String icon

        switch (ccLicence) {
            case "Creative Commons Attribution":
                icon = "https://licensebuttons.net/l/by/4.0/80x15.png"
                break
            case "Creative Commons Attribution-Noncommercial":
                icon = "https://licensebuttons.net/l/by-nc/3.0/80x15.png"
                break
            case "Creative Commons Attribution-Share Alike":
                icon = "https://licensebuttons.net/l/by-sa/3.0/80x15.png"
                break
            case "Creative Commons Attribution-Noncommercial-Share Alike":
                icon = "https://licensebuttons.net/l/by-nc-sa/3.0/80x15.png"
                break
            default:
                icon = "https://licensebuttons.net/l/by/4.0/80x15.png"
        }

        icon
    }

    static String getContentType(File file) {
        return Files.probeContentType(file.toPath()) ?: 'application/octet-stream'
    }

    static String getExtension(String fileName) {
        if (!fileName) {
            return ''
        }
        final idx = fileName.lastIndexOf(".")
        return idx == -1 ? '' : fileName.substring(idx)
    }

    static String getExtensionFromContentType(String contentType) {
        try {
            TikaConfig.defaultConfig.mimeRepository.forName(contentType).extension
        } catch (MimeTypeException e) {
            ''
        }
    }

    static String enc(String value) {
        value ? URLEncoder.encode(value, "UTF-8") : ""
    }

    static String extractFilenameFromContentDisposition(String contentDisposition) {
        try {
            Matcher m = CONTENT_DISPOSITION_PATTERN.matcher(contentDisposition)
            if (m.find()) {
                return m.group(1)
            }
        } catch (IllegalStateException e) {
        }
        return ''
    }
}
