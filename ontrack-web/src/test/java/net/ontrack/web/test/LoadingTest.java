package net.ontrack.web.test;

import net.ontrack.core.RunProfile;
import net.ontrack.web.config.WebConfig;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * Tests that everything can be loaded.
 * <p/>
 * TODO Activate the tests (does not work with Servlet 3.x
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(
        loader = AnnotationConfigContextLoader.class,
        classes = WebConfig.class)
@ActiveProfiles(profiles = {RunProfile.TEST})
@Ignore
public class LoadingTest {

    @Test
    public void loadOK() throws Exception {
    }

}
