package controllers;

import models.Tripulante;
import services.TripulanteService;
import com.google.gson.Gson;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/tripulacion")
public class TripulanteController {

    private TripulanteService tripulanteService = new TripulanteService();
    private Gson gson = new Gson();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTripulacion() {
        return Response.ok(gson.toJson(tripulanteService.obtenerTripulacion())).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTripulante(@PathParam("id") int id) {
        Tripulante t = tripulanteService.obtenerTripulantePorId(id);
        if (t != null) return Response.ok(gson.toJson(t)).build();
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response crearTripulante(String json) {
        Tripulante t = gson.fromJson(json, Tripulante.class);
        if (tripulanteService.guardarTripulante(t)) {
            return Response.status(Response.Status.CREATED).entity("{\"mensaje\":\"Tripulante registrado\"}").build();
        }
        return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\":\"Error al registrar (Revise licencia)\"}").build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateTripulante(@PathParam("id") int id, String json) {
        Tripulante t = gson.fromJson(json, Tripulante.class);
        t.setIdTripulante(id);
        if (tripulanteService.actualizarTripulante(t)) {
            return Response.ok("{\"mensaje\":\"Tripulante actualizado\"}").build();
        }
        return Response.serverError().build();
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteTripulante(@PathParam("id") int id) {
        if (tripulanteService.eliminarTripulante(id)) {
            return Response.ok("{\"mensaje\":\"Tripulante eliminado\"}").build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }
}