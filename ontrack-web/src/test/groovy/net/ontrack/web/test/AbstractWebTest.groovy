package net.ontrack.web.test

import java.lang.invoke.MethodHandleImpl.BindCaller.T

import net.ontrack.test.AbstractIntegrationTest

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.web.context.WebApplicationContext

@WebAppConfiguration
abstract class AbstractWebTest extends AbstractIntegrationTest {
	
	@Autowired
    private WebApplicationContext wac;

}
