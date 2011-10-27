/*
 * RHQ Management Platform
 * Copyright (C) 2005-2011 Red Hat, Inc.
 * All rights reserved.
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
package org.rhq.enterprise.server.rest;

import java.io.InputStream;
import java.util.List;

import javax.ejb.Local;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.PUT;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

import org.rhq.enterprise.server.rest.domain.MetricAggregate;
import org.rhq.enterprise.server.rest.domain.MetricSchedule;

/**
 * Deal with metrics
 * @author Heiko W. Rupp
 */
@Produces({"application/json","application/xml", "text/html"})
@Path("/metric")
@Local
public interface MetricHandlerLocal {


    @GET
    @Path("data/{scheduleId}")
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML,MediaType.TEXT_HTML})
    Response getMetricData(@PathParam("scheduleId") int scheduleId,
                                  @QueryParam("startTime")  long startTime,
                                  @QueryParam("endTime") long endTime,
                                  @QueryParam("dataPoints") int dataPoints,
                                  @QueryParam("hideEmpty") boolean hideEmpty,
                                  @Context Request request,
                                  @Context HttpHeaders headers);


    @GET
    @Path("data/resource/{resourceId}")
    List<MetricAggregate> getAggregatesForResource(@PathParam("resourceId") int resourceId);

    @GET
    @Path("/schedule/{id}")
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML,MediaType.TEXT_HTML})
    Response getSchedule(@PathParam("id") int scheduleId, @Context Request request, @Context HttpHeaders headers);

    @PUT
    @Path("/schedule/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    MetricSchedule updateSchedule(@PathParam("id") int scheduleId,  MetricSchedule in);

}
