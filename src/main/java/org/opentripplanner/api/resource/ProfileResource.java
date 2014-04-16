package org.opentripplanner.api.resource;

import java.util.List;

import javax.inject.Singleton;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.opentripplanner.api.param.HourMinuteSecond;
import org.opentripplanner.api.param.LatLon;
import org.opentripplanner.api.param.YearMonthDay;
import org.opentripplanner.profile.Option;
import org.opentripplanner.profile.ProfileResponse;
import org.opentripplanner.profile.ProfileRouter;
import org.opentripplanner.routing.graph.Graph;
import org.opentripplanner.routing.services.GraphService;
import org.opentripplanner.standalone.OTPServer;

/**
 * A Jersey resource class which exposes OTP profile routing functionality
 * as a web service.
 */
@Path("routers/{routerId}/profile")
public class ProfileResource {

    private ProfileRouter router;

    public ProfileResource (@Context OTPServer otpServer, @PathParam("routerId") String routerId) {
        Graph graph = otpServer.graphService.getGraph(routerId);
        router = new ProfileRouter(graph.index);
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    public Response profileRoute(
            @QueryParam("from") LatLon from, 
            @QueryParam("to")   LatLon to,
            @QueryParam("date")      @DefaultValue("today") YearMonthDay date,
            @QueryParam("startTime") @DefaultValue("07:00") HourMinuteSecond fromTime, 
            @QueryParam("endTime")   @DefaultValue("09:00") HourMinuteSecond toTime,
            @QueryParam("orderBy")   @DefaultValue("MIN")   Option.SortOrder orderBy,
            @QueryParam("limit")     @DefaultValue("10")    Integer limit) {

        List<Option> options = router.route(from, to, fromTime.toSeconds(), toTime.toSeconds(), date.toJoda());
        ProfileResponse pr = new ProfileResponse(options, orderBy, limit);
        return Response.status(Status.OK).entity(pr).build();
    
    }
    
}