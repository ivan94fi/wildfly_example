package rest.security;

import java.security.Principal;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.SecurityContext;

public class TestSecurityContext implements SecurityContext {

    private ContainerRequestContext requestContext;

    public TestSecurityContext(ContainerRequestContext requestContext) {
        this.requestContext = requestContext;
    }

    @Override
    public boolean isUserInRole(String role) {
        return true;
    }

    @Override
    public boolean isSecure() {
        return requestContext.getSecurityContext().isSecure();
    }

    @Override
    public Principal getUserPrincipal() {
        return null;
    }

    @Override
    public String getAuthenticationScheme() {
        return null;
    }
}
