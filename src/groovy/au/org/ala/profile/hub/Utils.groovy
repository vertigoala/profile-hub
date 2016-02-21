package au.org.ala.profile.hub

class Utils {
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
}
