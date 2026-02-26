package controllers;

import java.util.List;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import com.google.gson.Gson;
import models.Avion;
import services.AvionService;

@Path("/aviones")
public class AvionController {

    private AvionService avionService = new AvionService();
    private Gson gson = new Gson();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAviones() {
        List<Avion> lista = avionService.obtenerAviones();
        return Response.ok(gson.toJson(lista)).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAvion(@PathParam("id") int id) {
        Avion a = avionService.obtenerAvionPorId(id);
        if (a != null) return Response.ok(gson.toJson(a)).build();
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response crearAvion(String jsonAvion) {
        Avion nuevoAvion = gson.fromJson(jsonAvion, Avion.class);
        if (avionService.guardarAvion(nuevoAvion)) {
            return Response.status(Response.Status.CREATED)
                           .entity("{\"mensaje\":\"Avión creado exitosamente\"}").build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("{\"error\":\"Error al crear el avión\"}").build();
        }
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateAvion(@PathParam("id") int id, String jsonAvion) {
        Avion avion = gson.fromJson(jsonAvion, Avion.class);
        avion.setIdAvion(id);
        if (avionService.actualizarAvion(avion)) {
            return Response.ok("{\"mensaje\":\"Avión actualizado\"}").build();
        }
        return Response.serverError().build();
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteAvion(@PathParam("id") int id) {
        if (avionService.eliminarAvion(id)) {
            return Response.ok("{\"mensaje\":\"Avión eliminado\"}").build();
        }
        return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\":\"No se puede eliminar\"}").build();
    }
}