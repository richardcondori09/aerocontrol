package controllers;

import models.Pasajero;
import services.PasajeroService;
import com.google.gson.Gson;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/pasajeros")
public class PasajeroController {

    private PasajeroService pasajeroService = new PasajeroService();
    private Gson gson = new Gson();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPasajeros() {
        return Response.ok(gson.toJson(pasajeroService.obtenerPasajeros())).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPasajero(@PathParam("id") int id) {
        Pasajero p = pasajeroService.obtenerPasajeroPorId(id);
        if (p != null) return Response.ok(gson.toJson(p)).build();
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response crearPasajero(String json) {
        Pasajero p = gson.fromJson(json, Pasajero.class);
        if (pasajeroService.guardarPasajero(p)) {
            return Response.status(Response.Status.CREATED).entity("{\"mensaje\":\"Pasajero creado\"}").build();
        }
        return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\":\"Error al crear pasajero (Verifique pasaporte único)\"}").build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updatePasajero(@PathParam("id") int id, String json) {
        Pasajero p = gson.fromJson(json, Pasajero.class);
        p.setIdPasajero(id);
        if (pasajeroService.actualizarPasajero(p)) {
            return Response.ok("{\"mensaje\":\"Pasajero actualizado\"}").build();
        }
        return Response.serverError().build();
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deletePasajero(@PathParam("id") int id) {
        if (pasajeroService.eliminarPasajero(id)) {
            return Response.ok("{\"mensaje\":\"Pasajero eliminado\"}").build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }
}