package net.ontrack.backend;

import net.ontrack.core.model.BranchSummary;
import net.ontrack.core.model.DashboardConfig;
import net.ontrack.core.model.DashboardConfigForm;
import net.ontrack.service.DashboardService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DashboardServiceIntegrationTest extends AbstractBackendTest {

    @Autowired
    private DashboardService dashboardService;

    /**
     * Makes sure all dashboards are deleted before each test.
     */
    @Before
    public void before() throws Exception {
        asAdmin().call(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                for (DashboardConfig dashboardConfig : dashboardService.getDashboardConfigs()) {
                    dashboardService.deleteDashboardConfig(dashboardConfig.getId());
                }
                return null;
            }
        });
    }

    /**
     * Creation of two dashboards
     */
    @Test
    public void creating_two_dashboards() throws Exception {
        asAdmin().call(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                int b1 = doCreateBranch().getId();
                int b2 = doCreateBranch().getId();
                int b3 = doCreateBranch().getId();
                assertTrue("No existing dashboard", dashboardService.getDashboardConfigs().isEmpty());
                dashboardService.createDashboardConfig(new DashboardConfigForm("D1", Arrays.asList(b1, b2)));
                dashboardService.createDashboardConfig(new DashboardConfigForm("D3", Arrays.asList(b3)));
                List<DashboardConfig> configs = dashboardService.getDashboardConfigs();
                assertEquals(2, configs.size());
                return null;
            }
        });
    }

    /**
     * Life-cycle
     */
    @Test
    public void dashboard_lifecycle() throws Exception {
        asAdmin().call(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                BranchSummary b1 = doCreateBranch();
                BranchSummary b2 = doCreateBranch();
                assertTrue("No existing dashboard", dashboardService.getDashboardConfigs().isEmpty());

                DashboardConfig config = dashboardService.createDashboardConfig(new DashboardConfigForm("D1", Arrays.asList(b1.getId(), b2.getId())));
                assertEquals("D1", config.getName());
                assertEquals(Arrays.asList(b1, b2), config.getBranches());

                List<DashboardConfig> configs = dashboardService.getDashboardConfigs();
                assertEquals(Arrays.asList(config), configs);

                assertEquals(config, dashboardService.getDashboardConfig(config.getId()));

                dashboardService.updateDashboardConfig(config.getId(), new DashboardConfigForm("DD1", Arrays.asList(b1.getId(), b2.getId())));
                assertEquals(new DashboardConfig(config.getId(), "DD1", Arrays.asList(b1, b2)), dashboardService.getDashboardConfig(config.getId()));

                BranchSummary b3 = doCreateBranch();
                dashboardService.updateDashboardConfig(config.getId(), new DashboardConfigForm("DD1", Arrays.asList(b1.getId(), b2.getId(), b3.getId())));
                assertEquals(new DashboardConfig(config.getId(), "DD1", Arrays.asList(b1, b2, b3)), dashboardService.getDashboardConfig(config.getId()));

                dashboardService.deleteDashboardConfig(config.getId());
                assertTrue("No existing dashboard", dashboardService.getDashboardConfigs().isEmpty());

                return null;
            }
        });
    }



}
