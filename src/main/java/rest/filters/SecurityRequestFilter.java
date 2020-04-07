package rest.filters;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

import daos.UserDAO;
import rest.security.ApplicationSecurityContext;
import rest.security.SecurityBypass;
import rest.security.TestSecurityContext;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class SecurityRequestFilter implements ContainerRequestFilter {

    private static final String BASIC = "BASIC";

    @Inject
    private UserDAO userDao;

    @Override
    public void filter(ContainerRequestContext requestContext)
            throws IOException {
        if (SecurityBypass.isTestRequest(requestContext)) {
            requestContext.setSecurityContext(
                    new TestSecurityContext(requestContext));
            return;
        }

        String authorizationHeader = requestContext.getHeaderString(
                HttpHeaders.AUTHORIZATION);
        if (authorizationHeader == null || authorizationHeader.isEmpty()) {
            requestContext.abortWith(Response.status(Status.UNAUTHORIZED)
                                             .type(MediaType.TEXT_PLAIN)
                                             .build());
            return;
        }

        String username = null;
        if (authorizationHeader.toUpperCase().startsWith(BASIC)) {
            String encodedCredentials = authorizationHeader.substring(
                    BASIC.length()).trim();
            String credentials = new String(
                    Base64.getDecoder().decode(encodedCredentials),
                    StandardCharsets.UTF_8);
            username = credentials.split(":", 2)[0];
            // TODO check passwords
        }

        // TODO: check if username exists, else abort with Forbidden

        requestContext.setSecurityContext(new ApplicationSecurityContext(
                userDao, username, requestContext));
    }

}
