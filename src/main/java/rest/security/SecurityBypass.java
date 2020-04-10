package rest.security;

import javax.ws.rs.container.ContainerRequestContext;

import org.jasypt.util.password.BasicPasswordEncryptor;

public class SecurityBypass {

    private SecurityBypass() {
        throw new IllegalStateException(
                "Utility class, not to be instantiated.");
    }

    public static final String PASSWORD_ENV_VAR = "WILDFLY_TEST_REQUEST_PASSWORD";
    public static final String TEST_HEADER = "Test-Request-Secret";

    private static BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();

    public static boolean isTestRequest(
            ContainerRequestContext requestContext) {
        String testHeader = requestContext.getHeaderString(TEST_HEADER);
        String hostHeader = requestContext.getHeaderString("Host");
        if (testHeader != null) {
            return hostHeader.startsWith("localhost")
                    && SecurityBypass.passwordEncryptor.checkPassword(
                            System.getenv(PASSWORD_ENV_VAR), testHeader);
        }
        return false;
    }

}
