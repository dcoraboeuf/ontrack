package net.ontrack.backend.security;

import net.ontrack.core.security.ProjectFunction;
import net.ontrack.core.security.ProjectGrantIdAlreadyDefinedException;
import net.ontrack.core.security.ProjectGrantIdMissingException;
import net.ontrack.core.security.SecurityUtils;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;

import java.lang.reflect.Method;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class OntrackDecisionManagerTest {

    private OntrackDecisionManager manager;
    private SecurityUtils securityUtils;

    @Before
    public void before() {
        securityUtils = mock(SecurityUtils.class);
        manager = new OntrackDecisionManager(securityUtils);
    }

    @Test
    public void supports_method_invocation() {
        assertTrue(manager.supports(MethodInvocation.class));
    }

    @Test
    public void supports_any_attribute() {
        assertTrue(manager.supports(mock(ConfigAttribute.class)));
    }

    @Test
    public void checkProjectGrant() {
        ProjectFunction fn = ProjectFunction.PROJECT_MODIFY;
        when(securityUtils.isGranted(fn, 1)).thenReturn(true);
        assertTrue(manager.checkProjectGrant(1, fn));
        verify(securityUtils, times(1)).isGranted(fn, 1);
    }

    @Test(expected = AccessDeniedException.class)
    public void decide_no_constraint() throws SecurityException, NoSuchMethodException {
        Method method = SampleAPI.class.getMethod("no_constraint");

        SampleImpl target = new SampleImpl();

        MethodInvocation invocation = mock(MethodInvocation.class);
        when(invocation.getMethod()).thenReturn(method);
        when(invocation.getThis()).thenReturn(target);

        Authentication authentication = mock(Authentication.class);

        manager.decide(authentication, invocation, null);
    }

    @Test(expected = ProjectGrantIdMissingException.class)
    public void decide_id_missing() throws SecurityException, NoSuchMethodException {
        Method method = SampleAPI.class.getMethod("project_call_missing_param", Integer.TYPE);

        SampleImpl target = new SampleImpl();

        MethodInvocation invocation = mock(MethodInvocation.class);
        when(invocation.getMethod()).thenReturn(method);
        when(invocation.getThis()).thenReturn(target);
        when(invocation.getArguments()).thenReturn(new Object[]{1});

        Authentication authentication = mock(Authentication.class);

        manager.decide(authentication, invocation, null);
    }

    @Test(expected = ProjectGrantIdAlreadyDefinedException.class)
    public void decide_too_much() throws SecurityException, NoSuchMethodException {
        Method method = SampleAPI.class.getMethod("project_call_too_much", int.class, int.class);

        SampleImpl target = new SampleImpl();

        MethodInvocation invocation = mock(MethodInvocation.class);
        when(invocation.getMethod()).thenReturn(method);
        when(invocation.getThis()).thenReturn(target);
        when(invocation.getArguments()).thenReturn(new Object[]{1, 10});

        Authentication authentication = mock(Authentication.class);

        manager.decide(authentication, invocation, null);
    }

    @Test
    public void decide_ok() throws SecurityException, NoSuchMethodException {
        Method method = SampleAPI.class.getMethod("project_call_ok", int.class, String.class);

        SampleImpl target = new SampleImpl();

        MethodInvocation invocation = mock(MethodInvocation.class);
        when(invocation.getMethod()).thenReturn(method);
        when(invocation.getThis()).thenReturn(target);
        when(invocation.getArguments()).thenReturn(new Object[]{1, "A"});

        Authentication authentication = mock(Authentication.class);
        when(securityUtils.isGranted(ProjectFunction.PROJECT_MODIFY, 1)).thenReturn(true);

        manager.decide(authentication, invocation, null);
    }


}
