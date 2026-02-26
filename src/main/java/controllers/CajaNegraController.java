package controllers;

import models.Coordenada;
import telemetria.CajaNegraManager;
import utils.JsonManager;

import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/cajanegra")
public class CajaNegraController {

    @GET
    @Path("/{idVuelo}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response recuperarUltimaPosicion(@PathParam("idVuelo") int idVuelo) {
        Coordenada coord = CajaNegraManager.recuperarUltimaPosicion(idVuelo);
        
        if (coord != null) {
            return Response.ok(JsonManager.getInstance().toJson(coord)).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                           .entity("{\"error\":\"No hay registros de caja negra para este vuelo.\"}").build();
        }
    }
    
    @GET
    @Path("/{idVuelo}/historial")
    @Produces(MediaType.APPLICATION_JSON)
    public Response recuperarHistorialCompleto(@PathParam("idVuelo") int idVuelo) {
        List<Coordenada> historial = CajaNegraManager.recuperarHistorialCompleto(idVuelo);
        
        if (!historial.isEmpty()) {
            return Response.ok(JsonManager.getInstance().toJson(historial)).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                           .entity("{\"error\":\"No hay historial de caja negra para este vuelo.\"}").build();
        }
    }
}