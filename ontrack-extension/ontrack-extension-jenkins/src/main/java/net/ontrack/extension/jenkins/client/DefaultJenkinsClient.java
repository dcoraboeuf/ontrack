package net.ontrack.extension.jenkins.client;

import com.netbeetle.jackson.ObjectMapperFactory;
import net.ontrack.extension.jenkins.JenkinsJobResult;
import net.ontrack.extension.jenkins.JenkinsJobState;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class DefaultJenkinsClient implements JenkinsClient {

    private ObjectMapper mapper = ObjectMapperFactory.createObjectMapper();

    @Override
    public JenkinsJob getJob(String jenkinsJobUrl, boolean depth) {
        // Gets the job as JSON
        JsonNode tree = getJsonNode(jenkinsJobUrl, depth);

        // Name
        String name = tree.get("name").getTextValue();

        // Gets the 'color' field
        String color = tree.get("color").getTextValue();
        // Gets the state & result
        JenkinsJobState state = getJobState(color);
        JenkinsJobResult result = getJobResult(color);

        // Culprits
        List<JenkinsCulprit> culprits = new ArrayList<>();
        // Gets the list of builds
        JsonNode builds = tree.get("builds");
        if (builds.isArray() && builds.size() > 0) {
            JsonNode build = builds.get(0);
            if (build.has("building") && build.get("building").getBooleanValue()) {
                // Gets the list of culprits
                if (build.has("culprits")) {
                    JsonNode jCulprits = build.get("culprits");
                    if (jCulprits.isArray()) {
                        for (JsonNode jCulprit : jCulprits) {
                            String culpritUrl = jCulprit.get("absoluteUrl").getTextValue();
                            JenkinsUser user = getUser(culpritUrl);
                            if (user != null) {
                                JenkinsCulprit culprit = new JenkinsCulprit(user);
                                // TODO Claim?
                                // OK
                                culprits.add(culprit);
                            }
                        }
                    }
                }
            }
        }

        // OK
        return new JenkinsJob(
                name,
                result,
                state,
                culprits
        );
    }

    protected JenkinsUser getUser(String culpritUrl) {
        // Node
        JsonNode tree = getJsonNode(culpritUrl, false);
        // Basic data
        String id = tree.get("id").getTextValue();
        String fullName = tree.get("fullName").getTextValue();
        // TODO Fetch the image URL
        String imageUrl = null;
        // OK
        return new JenkinsUser(
                id,
                fullName,
                imageUrl
        );
    }

    protected JenkinsJobState getJobState(String color) {
        if ("disabled".equals(color)) {
            return JenkinsJobState.DISABLED;
        } else if (StringUtils.endsWith(color, "_anime")) {
            return JenkinsJobState.RUNNING;
        } else {
            return JenkinsJobState.IDLE;
        }
    }

    protected JenkinsJobResult getJobResult(String color) {
        if ("disabled".equals(color)) {
            return JenkinsJobResult.DISABLED;
        } else if (StringUtils.startsWith(color, "red")) {
            return JenkinsJobResult.FAILED;
        } else if (StringUtils.startsWith(color, "yellow")) {
            return JenkinsJobResult.UNSTABLE;
        } else if (StringUtils.startsWith(color, "blue")) {
            return JenkinsJobResult.SUCCESS;
        } else {
            return JenkinsJobResult.UNKNOWN;
        }
    }

    private JsonNode getJsonNode(String url, boolean depth) {
        // Gets a client
        DefaultHttpClient client = createClient();
        // Gets the JSON API URL for the job
        String apiUrl = getAPIURL(url, depth);
        // Creates the request
        HttpGet get = new HttpGet(apiUrl);
        // Call
        HttpResponse response;
        try {
            response = client.execute(get);
        } catch (IOException e) {
            throw new JenkinsClientCallException(get, e);
        }
        // Checks the status
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != HttpStatus.SC_OK) {
            throw new JenkinsClientNotOKException(get, statusCode);
        }
        // Response entity
        HttpEntity responseEntity = response.getEntity();
        // Gets the content as UTF-8 string
        String content;
        try {
            content = responseEntity != null ? EntityUtils.toString(responseEntity, "UTF-8") : null;
        } catch (IOException e) {
            throw new JenkinsClientCannotGetContentException(get, e);
        }
        // Checks for null
        if (content == null) {
            throw new JenkinsClientNullContentException(get);
        }
        // Parses as a tree
        JsonNode tree;
        try {
            tree = mapper.readTree(content);
        } catch (IOException e) {
            throw new JenkinsClientCannotParseContentException(get, e);
        }
        return tree;
    }

    private String getAPIURL(String url, boolean depth) {
        StringBuilder b = new StringBuilder(url);
        if (!url.endsWith("/")) {
            b.append("/");
        }
        b.append("api/json");
        if (depth) {
            b.append("?depth=1");
        }
        return b.toString();
    }

    private DefaultHttpClient createClient() {
        return new DefaultHttpClient(new PoolingClientConnectionManager());
    }

}
