package net.ontrack.backend.security;

import net.ontrack.core.security.*;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;

@Component
public class OntrackDecisionManager implements AccessDecisionManager {

    private final Logger logger = LoggerFactory.getLogger(OntrackDecisionManager.class);
    private final SecurityUtils securityUtils;

    @Autowired
    public OntrackDecisionManager(SecurityUtils securityUtils) {
        this.securityUtils = securityUtils;
    }

    @Override
    public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes) throws AccessDeniedException, InsufficientAuthenticationException {
        logger.debug("[grant] Authorizing {}...", object);
        // Method to authenticate
        MethodInvocation invocation = (MethodInvocation) object;
        // Checks the grants
        if (globalGranted(invocation) || projectGranted(invocation)) {
            logger.debug("[grant] Granted after authorization.");
        }
        // No control - anomaly
        else {
            String accessDeniedMessage = String.format("%s is under control but no access could be granted.", invocation.getMethod());
            throw new AccessDeniedException(accessDeniedMessage);
        }
    }

    protected boolean globalGranted(MethodInvocation invocation) {
        GlobalGrant grant = getAnnotation(invocation, GlobalGrant.class);
        if (grant != null) {
            return checkAdminGrant(grant.value());
        } else {
            return false;
        }
    }

    protected boolean projectGranted(MethodInvocation invocation) {
        ProjectGrant grant = getAnnotation(invocation, ProjectGrant.class);
        if (grant != null) {
            int project = getProjectId(invocation);
            return checkProjectGrant(project, grant.value());
        } else {
            return false;
        }
    }

    protected boolean checkAdminGrant(GlobalFunction fn) {
        return securityUtils.isGranted(fn);
    }

    protected boolean checkProjectGrant(int project, ProjectFunction fn) {
        return securityUtils.isGranted(fn, project);
    }

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return (MethodInvocation.class.isAssignableFrom(clazz));
    }

    protected int getProjectId(MethodInvocation invocation) {
        Integer project = getParamDesignedByAnnotation(invocation, ProjectGrantId.class, int.class);
        if (project == null) {
            throw new ProjectGrantIdMissingException(invocation.getMethod().getName());
        }
        return project;
    }

    private <T> T getParamDesignedByAnnotation(MethodInvocation invocation, Class<?> annotationClass, Class<T> paramClass) {
        Method method = getTargetMethod(invocation);
        T parameterValue = null;
        Annotation[][] allParamAnnotations = method.getParameterAnnotations();
        Object[] arguments = invocation.getArguments();
        for (int i = 0; i < arguments.length; i++) {
            Class<?> paramType = method.getParameterTypes()[i];
            if (paramClass.isAssignableFrom(paramType)) {
                Annotation[] paramAnnotations = allParamAnnotations[i];
                if (paramAnnotations != null) {
                    for (Annotation paramAnnotation : paramAnnotations) {
                        if (annotationClass.isInstance(paramAnnotation)) {
                            if (parameterValue != null) {
                                throw new ProjectGrantIdAlreadyDefinedException(method.getName(), annotationClass);
                            }
                            parameterValue = (T) arguments[i];
                        }
                    }
                }
            }
        }
        return parameterValue;
    }

    protected <A extends Annotation> A getAnnotation(MethodInvocation invocation, Class<A> type) {
        Method method = invocation.getMethod();
        A a = method.getAnnotation(type);
        if (a == null) {
            Method targetMethod = getTargetMethod(invocation);
            return targetMethod.getAnnotation(type);
        } else {
            return a;
        }
    }

    protected Method getTargetMethod(MethodInvocation invocation) {
        Object target = invocation.getThis();
        Method method = invocation.getMethod();
        Method targetMethod;
        try {
            targetMethod = target.getClass().getMethod(method.getName(), method.getParameterTypes());
        } catch (Exception e) {
            throw new IllegalStateException("Cannot find target method", e);
        }
        return targetMethod;
    }
}
