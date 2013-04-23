package net.ontrack.client.support;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.netbeetle.jackson.ObjectMapperFactory;
import net.ontrack.client.Client;
import net.ontrack.core.model.Ack;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class AbstractClient implements Client {

    private final Logger logger = LoggerFactory.getLogger(Client.class);

    private final String url;
    private final DefaultHttpClient client;

    public AbstractClient(String url) {
        this(url, new DefaultHttpClient(new PoolingClientConnectionManager()));
    }

    public AbstractClient(String url, DefaultHttpClient client) {
        this.client = client;
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public void logout() {
        logger.debug("[logout]");
        // Executes the call
        request(new HttpGet(getUrl("/logout")), new NOPResponseHandler());
    }

    @Override
    public void login(String name, String password) {
        // Forces the logout
        logout();
        // Configures the client for the credentials
        logger.debug("[login]");
        client.getCredentialsProvider().setCredentials(
                new AuthScope(null, -1),
                new UsernamePasswordCredentials(name, password));
        // Gets the server to send a challenge back
        get("/ui/login", Ack.class);
    }

    protected <T> T get(String path, Class<T> returnType) {
        return request(new HttpGet(getUrl(path)), returnType);
    }

    protected <T> List<T> list(final String path, final Class<T> elementType) {
        return request(new HttpGet(getUrl(path)), new ResponseParser<List<T>>() {
            @Override
            public List<T> parse(final String content) throws IOException {
                final ObjectMapper mapper = ObjectMapperFactory.createObjectMapper();
                JsonNode node = mapper.readTree(content);
                if (node.isArray()) {
                    return Lists.newArrayList(
                            Iterables.transform(node, new Function<JsonNode, T>() {
                                @Override
                                public T apply(JsonNode input) {
                                    try {
                                        return mapper.readValue(input, elementType);
                                    } catch (IOException e) {
                                        throw new ClientGeneralException(path, e);
                                    }
                                }
                            })
                    );
                } else {
                    throw new IOException("Did not receive a JSON array");
                }
            }
        });
    }

    protected <T> T put(String path, Class<T> returnType, Object payload) {
        HttpPut put = new HttpPut(getUrl(path));
        if (put != null) {
            setBody(payload, put);
        }
        return request(put, returnType);
    }

    private void setBody(Object payload, HttpEntityEnclosingRequestBase put) {
        try {
            String json = ObjectMapperFactory.createObjectMapper().writeValueAsString(payload);
            put.setEntity(new StringEntity(json, ContentType.create("application/json", "UTF-8")));
        } catch (IOException e) {
            throw new ClientGeneralException(put, e);
        }
    }

    protected <T> T post(String path, Class<T> returnType, Map<String, String> parameters) {
        HttpPost post = new HttpPost(getUrl(path));
        if (parameters != null) {
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            for (Map.Entry<String, String> param : parameters.entrySet()) {
                nvps.add(new BasicNameValuePair(param.getKey(), param.getValue()));
            }
            try {
                post.setEntity(new UrlEncodedFormEntity(nvps));
            } catch (UnsupportedEncodingException e) {
                throw new ClientGeneralException(post, e);
            }
        }
        return request(post, returnType);
    }

    protected <T> T post(String path, Class<T> returnType, Object body) {
        HttpPost post = new HttpPost(getUrl(path));
        if (body != null) {
            setBody(body, post);
        }
        return request(post, returnType);
    }

    protected <T> T delete(String path, Class<T> returnType) {
        return request(new HttpDelete(getUrl(path)), returnType);
    }

    protected String getUrl(String path) {
        return url + path;
    }

    protected <T> T request(HttpRequestBase request, Class<T> returnType) {
        return request(request, new SimpleTypeResponseParser<T>(returnType));
    }

    protected <T> T request(HttpRequestBase request, final ResponseParser<T> responseParser) {
        return request(request, new BaseResponseHandler<T>() {
            @Override
            protected T handleEntity(HttpEntity entity) throws ParseException, IOException {
                // Gets the content as a JSON string
                String content = entity != null ? EntityUtils.toString(entity, "UTF-8") : null;
                // Parses the response
                return responseParser.parse(content);
            }
        });
    }

    protected <T> T request(HttpRequestBase request, ResponseHandler<T> responseHandler) {
        logger.debug("[request] {}", request);
        // Executes the call
        try {
            HttpResponse response = client.execute(request);
            logger.debug("[response] {}", response);
            // Entity response
            HttpEntity entity = response.getEntity();
            try {
                return responseHandler.handleResponse(request, response, entity);
            } finally {
                EntityUtils.consume(entity);
            }
        } catch (IOException e) {
            throw new ClientGeneralException(request, e);
        } finally {
            request.releaseConnection();
        }
    }

    protected static interface ResponseParser<T> {

        T parse(String content) throws IOException;

    }

    protected static interface ResponseHandler<T> {

        T handleResponse(HttpRequestBase request, HttpResponse response, HttpEntity entity) throws ParseException, IOException;

    }

    protected static class NullResponseParser<T> implements ResponseParser<Object> {

        public static final NullResponseParser<Object> INSTANCE = new NullResponseParser<Object>();

        @Override
        public Object parse(String content) throws IOException {
            return null;
        }
    }

    protected static class StringResponseParser<T> implements ResponseParser<String> {

        public static final StringResponseParser INSTANCE = new StringResponseParser();

        @Override
        public String parse(String content) throws IOException {
            return content;
        }
    }

    protected static class SimpleTypeResponseParser<T> implements ResponseParser<T> {

        private final Class<T> type;


        public SimpleTypeResponseParser(Class<T> type) {
            this.type = type;
        }

        @Override
        public T parse(String content) throws IOException {
            if (content != null) {
                ObjectMapper mapper = ObjectMapperFactory.createObjectMapper();
                return mapper.readValue(content, type);
            } else {
                return null;
            }
        }
    }

    protected static class NOPResponseHandler implements ResponseHandler<Void> {

        @Override
        public Void handleResponse(HttpRequestBase request, HttpResponse response, HttpEntity entity) throws ParseException, IOException {
            return null;
        }

    }

    protected static abstract class BaseResponseHandler<T> implements ResponseHandler<T> {

        @Override
        public T handleResponse(HttpRequestBase request, HttpResponse response, HttpEntity entity) throws ParseException, IOException {
            // Parses the response
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                return handleEntity(entity);
            } else if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
                throw new ClientCannotLoginException(request);
            } else if (statusCode == HttpStatus.SC_FORBIDDEN) {
                throw new ClientForbiddenException(request);
            } else if (statusCode == HttpStatus.SC_NOT_FOUND) {
                return handleEntity(null);
            } else if (statusCode == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
                String content = EntityUtils.toString(response.getEntity(), "UTF-8");
                if (StringUtils.isNotBlank(content)) {
                    throw new ClientMessageException(content);
                } else {
                    // Generic error
                    throw new ClientServerException(
                            request,
                            statusCode,
                            response.getStatusLine().getReasonPhrase());
                }
            } else {
                // Generic error
                throw new ClientServerException(
                        request,
                        statusCode,
                        response.getStatusLine().getReasonPhrase());
            }
        }

        protected abstract T handleEntity(HttpEntity entity) throws ParseException, IOException;

    }

}
