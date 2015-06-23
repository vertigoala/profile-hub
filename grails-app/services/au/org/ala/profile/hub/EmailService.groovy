package au.org.ala.profile.hub

class EmailService {

    def grailsApplication

    void sendEmail(String recipient, String sender, String subjectText, String bodyHtml) {
        log.debug("Sending email to ${recipient} with subject '${subjectText}'")

        sendMail {
            to recipient
            from sender
            subject subjectText
            html bodyHtml
        }
    }

}
