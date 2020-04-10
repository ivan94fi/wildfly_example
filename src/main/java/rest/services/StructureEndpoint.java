package rest.services;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.logging.Logger;

import daos.StructureDAO;
import domain.Structure;

@Path("structures")
public class StructureEndpoint {

    @Inject
    private StructureDAO structureDao;

    @Inject
    private Logger logger;

    @GET
    @RolesAllowed("ADMIN")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {
        List<Structure> stuctures = structureDao.findAll();
        return Response.ok().entity(stuctures).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postStructure(Structure structure) {
        if (structure == null) {
            return Response.status(Status.BAD_REQUEST).build();
        }
        boolean success = structureDao.save(structure);
        if (!success) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
        URI uri;
        try {
            uri = new URI(structure.getId().toString());
        } catch (URISyntaxException e) {
            logger.error("Error in building URI", e);
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.created(uri).entity(structure).build();

    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSingleStructure(@PathParam("id") Long id) {
        Structure structure = structureDao.findById(id);
        if (structure == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.ok(structure).build();
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeUser(@PathParam("id") Long id) {
        Structure structure = structureDao.findById(id);
        if (structure == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        boolean success = structureDao.delete(structure);
        if (!success) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.ok(structure).build();
    }

    @PUT
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUser(@PathParam("id") Long id,
            Structure modifiedStructure) {
        Structure existingStructure = structureDao.findById(id);
        if (existingStructure == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        if (modifiedStructure == null) {
            return Response.status(Status.BAD_REQUEST).build();
        }
        existingStructure.setName(modifiedStructure.getName());
        existingStructure.setAddress(modifiedStructure.getAddress());
        boolean success = structureDao.merge(existingStructure);
        if (!success) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.ok(existingStructure).build();
    }
}
