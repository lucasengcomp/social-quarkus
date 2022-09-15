package br.com.social.user.resource;

import br.com.social.user.dto.CreateUserRequest;

import javax.print.attribute.standard.Media;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    @POST
    public Response createUser(CreateUserRequest userRequest) {
        return Response.ok().build();
    }

    @GET
    public Response listAllUsers() {
        return Response.ok().build();
    }
}
