package controllers;

import models.Vuelo;
import services.VueloService;
import com.google.gson.Gson;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/vuelos")
public class VueloController {

    private VueloService vueloService = new VueloService();
    private Gson gson = new Gson();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getVuelos() {
        List<Vuelo> lista = vueloService.obtenerVuelos();
        return Response.ok(gson.toJson(lista)).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getVuelo(@PathParam("id") int id) {
        Vuelo v = vueloService.obtenerVueloPorId(id);
        if (v != null) return Response.ok(gson.toJson(v)).build();
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response crearVuelo(String jsonVuelo) {
        Vuelo nuevoVuelo = gson.fromJson(jsonVuelo, Vuelo.class);
        if (vueloService.programarVuelo(nuevoVuelo)) {
            return Response.status(Response.Status.CREATED)
                           .entity("{\"mensaje\":\"Vuelo programado exitosamente\"}").build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("{\"error\":\"Error al programar el vuelo. Verifique que el id_avion exista.\"}").build();
        }
    }
    
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateVuelo(@PathParam("id") int id, String json) {
        Vuelo v = gson.fromJson(json, Vuelo.class);
        v.setIdVuelo(id);
        if (vueloService.actualizarVuelo(v)) {
            return Response.ok("{\"mensaje\":\"Vuelo actualizado\"}").build();
        }
        return Response.serverError().build();
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteVuelo(@PathParam("id") int id) {
        if (vueloService.eliminarVuelo(id)) {
            return Response.ok("{\"mensaje\":\"Vuelo eliminado\"}").build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    @PUT
    @Path("/{id}/despegar")
    @Produces(MediaType.APPLICATION_JSON)
    public Response iniciarVuelo(@PathParam("id") int idVuelo) {
        if (vueloService.despegarVuelo(idVuelo)) {
            return Response.ok("{\"mensaje\":\"El vuelo ha despegado\"}").build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                           .entity("{\"error\":\"Vuelo no encontrado\"}").build();
        }
    }
}