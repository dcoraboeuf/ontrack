package net.ontrack.web.test;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import net.ontrack.test.AbstractIntegrationTest;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebAppConfiguration
public abstract class AbstractWebTest extends AbstractIntegrationTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    protected ObjectMapper objectMapper;

    protected MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    protected String json(Object o) throws IOException {
        return objectMapper.writeValueAsString(o);
    }

    protected <T> T parse(String json, Class<T> type) throws IOException {
        return objectMapper.readValue(json, type);
    }

    protected <T> Iterable<T> parseList(String json, final Class<T> type) throws IOException {
        return Iterables.transform(
                objectMapper.readTree(json),
                new Function<JsonNode, T>() {
                    @Override
                    public T apply(JsonNode it) {
                        try {
                            return objectMapper.readValue(it, type);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
        );
    }

    protected <T> T getCall(String url, Class<T> type) throws Exception {
        return parse(
                this.mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andDo(MockMvcResultHandlers.print())
                        .andExpect(content().contentType("application/json;charset=UTF-8"))
                        .andReturn().getResponse().getContentAsString(),
                type);
    }

}
