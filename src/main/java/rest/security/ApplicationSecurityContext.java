package rest.security;

import java.security.Principal;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.SecurityContext;

import org.jboss.logging.Logger;

import daos.DAOException;
import daos.UserDAO;
import domain.User;

public class ApplicationSecurityContext implements SecurityContext {

    private Logger logger = Logger.getLogger(this.getClass());

    private UserDAO userDao;
    private String principalUsername;
    private ContainerRequestContext requestContext;

    public ApplicationSecurityContext(UserDAO userDao, String principalUsername,
            ContainerRequestContext requestContext) {
        this.userDao = userDao;
        this.principalUsername = principalUsername;
        this.requestContext = requestContext;
    }

    @Override
    public Principal getUserPrincipal() {
        return () -> this.principalUsername;
    }

    @Override
    public boolean isUserInRole(String role) {
        User principal;
        try {
            principal = userDao.findByUsername(principalUsername);
        } catch (DAOException e) {
            logger.error("Error in finding username", e);
            return false;
        }
        return principal != null && principal.hasRole(role);
    }

    @Override
    public boolean isSecure() {
        return this.requestContext.getSecurityContext().isSecure();
    }

    @Override
    public String getAuthenticationScheme() {
        return SecurityContext.BASIC_AUTH;
    }

    public UserDAO getUserDao() {
        return userDao;
    }

    public void setUserDao(UserDAO userDao) {
        this.userDao = userDao;
    }

    public String getPrincipalUsername() {
        return principalUsername;
    }

    public void setPrincipalUsername(String principalUsername) {
        this.principalUsername = principalUsername;
    }

    public ContainerRequestContext getRequestContext() {
        return requestContext;
    }

    public void setRequestContext(ContainerRequestContext requestContext) {
        this.requestContext = requestContext;
    }

}
