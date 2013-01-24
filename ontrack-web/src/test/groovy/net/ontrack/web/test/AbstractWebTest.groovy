package net.ontrack.web.test

import java.lang.invoke.MethodHandleImpl.BindCaller.T

import net.ontrack.test.AbstractIntegrationTest

import org.codehaus.jackson.map.ObjectMapper
import org.junit.Before
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@WebAppConfiguration
abstract class AbstractWebTest extends AbstractIntegrationTest {
	
	@Autowired
    private WebApplicationContext wac;
	
	@Autowired
	private ObjectMapper objectMapper
	
	protected MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }
	
	protected String json (Object o) {
		return objectMapper.writeValueAsString(o)
	}

}
