package controllers;

import models.ManifiestoVuelo;
import models.Pasajero;
import utils.XmlManager;
import dao.PasajeroImpl;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/exportar")
public class ExportadorController {

    @GET
    @Path("/manifiesto/{idVuelo}")
    @Produces(MediaType.APPLICATION_XML)
    public Response exportarManifiestoXML(@PathParam("idVuelo") int idVuelo) {
        try {
            // Simulamos obtener los pasajeros de ese vuelo
            List<Pasajero> pasajeros = new PasajeroImpl().listarTodos(); 

            ManifiestoVuelo manifiesto = new ManifiestoVuelo();
            manifiesto.setIdVuelo(idVuelo);
            manifiesto.setPasajeros(pasajeros);

            return Response.ok(XmlManager.convertirAXml(manifiesto, ManifiestoVuelo.class)).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity("<error>No se pudo generar el XML</error>").build();
        }
    }
}