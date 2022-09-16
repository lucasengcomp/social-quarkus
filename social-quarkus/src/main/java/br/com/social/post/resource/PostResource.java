package br.com.social.post.resource;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static br.com.social.core.consts.ConstsStatusCode.CREATED_STATUS;

@Path("/users/{userId}/posts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PostResource {

    @POST
    public Response savePost() {
        return Response.status(CREATED_STATUS).build();
    }

    @GET
    public Response listPosts() {
        return Response.ok().build();
    }
}
