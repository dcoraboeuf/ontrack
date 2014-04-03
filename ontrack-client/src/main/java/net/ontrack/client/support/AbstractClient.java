package net.ontrack.client.support;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.netbeetle.jackson.ObjectMapperFactory;
import net.ontrack.client.Client;
import net.ontrack.core.model.Ack;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

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

    protected Locale getDefaultLocale() {
        return Locale.getDefault();
    }

    @Override
    public void logout() {
        logger.debug("[logout]");
        // Executes the call
        request(getDefaultLocale(), new HttpGet(getUrl("/logout")), new NOPResponseHandler());
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
        get(getDefaultLocale(), "/ui/login", Ack.class);
    }

    protected <T> T get(Locale locale, String path, Class<T> returnType) {
        return request(locale, new HttpGet(getUrl(path)), returnType);
    }

    protected byte[] getBytes(Locale locale, String path) {
        HttpGet get = new HttpGet(getUrl(path));
        return request(locale, get, new ResponseParser<byte[]>() {
            @Override
            public byte[] parse(String content) throws IOException {
                return Base64.decodeBase64(content);
            }
        });
    }

    protected <T> List<T> list(Locale locale, final String path, final Class<T> elementType) {
        return request(locale, new HttpGet(getUrl(path)), new ResponseParser<List<T>>() {
            @Override
            public List<T> parse(final String content) throws IOException {
                if (StringUtils.isBlank(content)) {
                    return Collections.emptyList();
                } else {
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
            }
        });
    }

    protected <T> T put(Locale locale, String path, Class<T> returnType, Object payload) {
        HttpPut put = new HttpPut(getUrl(path));
        if (payload != null) {
            setBody(payload, put);
        }
        return request(locale, put, returnType);
    }

    private void setBody(Object payload, HttpEntityEnclosingRequestBase put) {
        try {
            String json = ObjectMapperFactory.createObjectMapper().writeValueAsString(payload);
            put.setEntity(new StringEntity(json, ContentType.create("application/json", "UTF-8")));
        } catch (IOException e) {
            throw new ClientGeneralException(put, e);
        }
    }

    protected <T> T post(Locale locale, String path, Class<T> returnType, Map<String, String> parameters) {
        HttpPost post = new HttpPost(getUrl(path));
        if (parameters != null) {
            List<NameValuePair> nvps = new ArrayList<>();
            for (Map.Entry<String, String> param : parameters.entrySet()) {
                nvps.add(new BasicNameValuePair(param.getKey(), param.getValue()));
            }
            try {
                post.setEntity(new UrlEncodedFormEntity(nvps));
            } catch (UnsupportedEncodingException e) {
                throw new ClientGeneralException(post, e);
            }
        }
        return request(locale, post, returnType);
    }

    protected <T> T upload(Locale locale, String path, String fileParameterName, MultipartFile file, Class<T> returnType) {
        HttpPost post = new HttpPost(getUrl(path));
        // Sets the content
        try {
            MultipartEntity multipartEntity = new MultipartEntity();
            ContentBody contentBody = new InputStreamBody(
                    file.getInputStream(),
                    file.getContentType(),
                    file.getName()
            );
            multipartEntity.addPart(fileParameterName, contentBody);
            post.setEntity(
                    multipartEntity
            );
        } catch (IOException e) {
            throw new ClientGeneralException(post, e);
        }
        // OK
        return request(locale, post, returnType);
    }

    protected <T> T post(Locale locale, String path, Class<T> returnType, Object body) {
        HttpPost post = new HttpPost(getUrl(path));
        if (body != null) {
            setBody(body, post);
        }
        return request(locale, post, returnType);
    }

    protected <T> T delete(Locale locale, String path, Class<T> returnType) {
        return request(locale, new HttpDelete(getUrl(path)), returnType);
    }

    protected String getUrl(String path) {
        return url + path;
    }

    protected <T> T request(Locale locale, HttpRequestBase request, Class<T> returnType) {
        return request(locale, request, new SimpleTypeResponseParser<>(returnType));
    }

    protected <T> T request(Locale locale, HttpRequestBase request, final ResponseParser<T> responseParser) {
        return request(locale, request, new BaseResponseHandler<T>() {
            @Override
            protected T handleEntity(HttpEntity entity) throws ParseException, IOException {
                // Gets the content as a JSON string
                String content = entity != null ? EntityUtils.toString(entity, "UTF-8") : null;
                // Parses the response
                return responseParser.parse(content);
            }
        });
    }

    protected <T> T request(Locale locale, HttpRequestBase request, ResponseHandler<T> responseHandler) {
        logger.debug("[request] {}", request);
        request.setHeader("Accept-Language", locale != null ? locale.toString() : "en");
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
