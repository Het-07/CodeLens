package codelens.backend.util;

public final class EmailTemplates {
    // Private constructor to prevent instantiation
    private EmailTemplates() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
    private static final String OTP_EMAIL_TEMPLATE = """
        <p>Hello,</p>
        <p>Your OTP for email verification is:</p>
        <h2>%s</h2>
        <p>This OTP is valid for 5 minutes.</p>
    """;

    // Public methods to get email templates
    public static String getOtpEmailTemplate() {
        return OTP_EMAIL_TEMPLATE;
    }

}
