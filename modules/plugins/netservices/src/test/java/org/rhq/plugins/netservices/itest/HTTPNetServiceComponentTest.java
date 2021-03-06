/*
 * RHQ Management Platform
 * Copyright 2013, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

package org.rhq.plugins.netservices.itest;

import static org.rhq.plugins.netservices.HTTPNetServiceComponent.ConfigKeys.METHOD;
import static org.rhq.plugins.netservices.HTTPNetServiceComponent.ConfigKeys.URL;
import static org.rhq.plugins.netservices.HTTPNetServiceComponent.ConfigKeys.VALIDATE_RESPONSE_CODE;
import static org.rhq.plugins.netservices.HTTPNetServiceComponent.ConfigKeys.VALIDATE_RESPONSE_PATTERN;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import org.rhq.core.domain.configuration.Configuration;
import org.rhq.core.domain.discovery.MergeResourceResponse;
import org.rhq.core.domain.measurement.AvailabilityType;
import org.rhq.core.domain.measurement.DataType;
import org.rhq.core.domain.measurement.MeasurementData;
import org.rhq.core.domain.measurement.MeasurementReport;
import org.rhq.core.domain.measurement.MeasurementScheduleRequest;
import org.rhq.core.pluginapi.inventory.ResourceComponent;
import org.rhq.plugins.netservices.HTTPNetServiceComponent;

/**
 * @author Thomas Segismont
 */
@Test(singleThreaded = true)
public class HTTPNetServiceComponentTest extends NetServiceComponentTest {

    private static final String SERVICE_NAME = "HTTPService";

    private static final String HTTP_HOST = "localhost";

    private static final int MIN_DYNAMIC_PORT = 49152;

    private static final int MAX_PORT_NUMBER = 65535;

    private static final int SERVLET_SLEEP = 1000;

    private int httpPort;

    private Server jettyServer;

    private HTTPNetServiceComponent httpNetServiceComponent;

    @BeforeClass
    public void startJetty() throws Exception {
        // Loop until Jetty binds to an available port
        for (httpPort = MIN_DYNAMIC_PORT; httpPort <= MAX_PORT_NUMBER; httpPort++) {
            ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
            context.setContextPath("/");
            HttpServlet testServlet = new TestServlet();
            context.addServlet(new ServletHolder(testServlet), "/*");
            jettyServer = new Server(new InetSocketAddress(HTTP_HOST, httpPort));
            jettyServer.setHandler(context);
            try {
                jettyServer.start();
                return;
            } catch (BindException e) {
                // Port already in use
            }
        }
        throw new RuntimeException("Could not find an available port");
    }

    @AfterClass
    public void stopJetty() {
        try {
            if (jettyServer != null) {
                jettyServer.stop();
            }
        } catch (Exception ignore) {
        }
    }

    @Test(dependsOnMethods = "testPluginLoad")
    public void testManualAdd() throws Exception {
        Configuration configuration = new Configuration();
        configuration.setSimpleValue(URL, "http://" + HTTP_HOST + ":" + httpPort + "/pipo/molo");
        configuration.setSimpleValue(METHOD, "GET");
        configuration.setSimpleValue(VALIDATE_RESPONSE_CODE, "true");
        configuration.setSimpleValue(VALIDATE_RESPONSE_PATTERN, "success");
        MergeResourceResponse response = getInventoryManager().manuallyAddResource(
            getPluginManager().getMetadataManager().getType(SERVICE_NAME, PLUGIN_NAME), getPlatform().getId(),
            configuration, -1);
        assertNotNull(response, "Manual add response is null");
        @SuppressWarnings("rawtypes")
        ResourceComponent resourceComponent = getInventoryManager().getResourceContainer(response.getResourceId())
            .getResourceComponent();
        assertEquals(resourceComponent.getClass(), HTTPNetServiceComponent.class);
        httpNetServiceComponent = (HTTPNetServiceComponent) resourceComponent;
    }

    @Test(dependsOnMethods = "testManualAdd")
    public void testAvailability() throws Exception {
        assertEquals(httpNetServiceComponent.getAvailability(), AvailabilityType.UP);
    }

    @Test(dependsOnMethods = "testAvailability")
    public void testMeasurement() throws Exception {
        MeasurementReport report = new MeasurementReport();
        Set<MeasurementScheduleRequest> metrics = new HashSet<MeasurementScheduleRequest>();
        int scheduleId = 1;
        metrics.add(new MeasurementScheduleRequest(scheduleId++, "connectTime", 1000, true, DataType.MEASUREMENT));
        metrics.add(new MeasurementScheduleRequest(scheduleId++, "readTime", 1000, true, DataType.MEASUREMENT));
        metrics.add(new MeasurementScheduleRequest(scheduleId++, "contentLength", 1000, true, DataType.MEASUREMENT));
        httpNetServiceComponent.getValues(report, metrics);
        Map<String, Object> datas = new HashMap<String, Object>();
        for (MeasurementData data : report.getNumericData()) {
            datas.put(data.getName(), data.getValue());
        }
        Double value = getMetric(datas, "connectTime");
        assertTrue(value > SERVLET_SLEEP);
        value = getMetric(datas, "readTime");
        assertTrue(value > SERVLET_SLEEP);
        value = getMetric(datas, "contentLength");
        assertTrue(value > 0);
    }

    private static class TestServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            resp.getWriter().println("Test servlet request: success view");
            long start = System.currentTimeMillis();
            do {
                try {
                    Thread.sleep(SERVLET_SLEEP);
                } catch (InterruptedException e) {
                }
            } while (System.currentTimeMillis() - start < SERVLET_SLEEP);
        }
    }
}
