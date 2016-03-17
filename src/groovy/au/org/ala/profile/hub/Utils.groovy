package au.org.ala.profile.hub

class Utils {
    static final String UUID_REGEX_PATTERN = "[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}"

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
        String extension = file.getName().substring(file.getName().lastIndexOf("."))

        List images = ["jpg", "jpeg", "gif", "tiff", "png", "bmp"]
        if (images.contains(extension)) {
            "image/*"
        } else if (extension == "pdf") {
            "application/pdf"
        } else {
            ""
        }
    }

    static String getExtension(String fileName) {
        fileName.substring(fileName.lastIndexOf("."))
    }
}
