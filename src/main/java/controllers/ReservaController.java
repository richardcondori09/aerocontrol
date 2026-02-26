package controllers;

import models.Reserva;
import services.ReservaService;
import com.google.gson.Gson;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/reservas")
public class ReservaController {

    private ReservaService reservaService = new ReservaService();
    private Gson gson = new Gson();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReservas() {
        return Response.ok(gson.toJson(reservaService.obtenerReservas())).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReserva(@PathParam("id") int id) {
        Reserva r = reservaService.obtenerReservaPorId(id);
        if (r != null) return Response.ok(gson.toJson(r)).build();
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response crearReserva(String json) {
        Reserva r = gson.fromJson(json, Reserva.class);
        String resultado = reservaService.procesarReserva(r);

        if (resultado.equals("OK")) {
            return Response.status(Response.Status.CREATED).entity("{\"mensaje\":\"Reserva confirmada exitosamente\"}").build();
        } else {
            return Response.status(Response.Status.CONFLICT).entity("{\"error\":\"" + resultado + "\"}").build();
        }
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateReserva(@PathParam("id") int id, String json) {
        Reserva r = gson.fromJson(json, Reserva.class);
        r.setIdReserva(id);
        if (reservaService.actualizarReserva(r)) {
            return Response.ok("{\"mensaje\":\"Reserva actualizada\"}").build();
        }
        return Response.serverError().build();
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteReserva(@PathParam("id") int id) {
        if (reservaService.eliminarReserva(id)) {
            return Response.ok("{\"mensaje\":\"Reserva eliminada\"}").build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }
}