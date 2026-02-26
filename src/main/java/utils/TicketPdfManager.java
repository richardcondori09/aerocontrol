package utils;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import models.Pasajero;
import models.Reserva;
import models.Vuelo;

import java.io.File;
import java.io.FileOutputStream;

public class TicketPdfManager {

	public static void generarTicket(Reserva reserva, Pasajero pasajero, Vuelo vuelo) {
		// Nombre del archivo
		String nombreArchivo = "Ticket_" + pasajero.getPasaporte() + "_Vuelo" + vuelo.getIdVuelo() + ".pdf";
		// Definimos la ruta
		String rutaCompleta = FileManager.getRutaTicket(nombreArchivo);
		Document documento = new Document();

		try {
			File archivoFisico = new File(rutaCompleta);
			PdfWriter.getInstance(documento, new FileOutputStream(archivoFisico));
			documento.open();

			// Estilos de fuente
			Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, BaseColor.BLUE);
			Font fontSubtitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.DARK_GRAY);
			Font fontNormal = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);

			// Título
			Paragraph titulo = new Paragraph("AeroControl - Boarding Pass", fontTitulo);
			titulo.setAlignment(Element.ALIGN_CENTER);
			titulo.setSpacingAfter(20);
			documento.add(titulo);

			// Datos del Pasajero
			documento.add(new Paragraph("DATOS DEL PASAJERO", fontSubtitulo));
			documento.add(new Paragraph("Nombre: " + pasajero.getNombre(), fontNormal));
			documento.add(new Paragraph("Pasaporte: " + pasajero.getPasaporte(), fontNormal));
			documento.add(new Paragraph(" "));

			// Datos del Vuelo
			documento.add(new Paragraph("INFORMACIÓN DEL VUELO", fontSubtitulo));
			documento.add(new Paragraph("Vuelo N°: " + vuelo.getIdVuelo(), fontNormal));
			documento.add(new Paragraph("Ruta: " + vuelo.getOrigen() + " ➔ " + vuelo.getDestino(), fontNormal));
			documento.add(new Paragraph("Fecha y Hora: " + vuelo.getFechaSalida(), fontNormal));
			documento.add(new Paragraph("Asiento: " + reserva.getAsiento(),
					FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.RED)));

			documento.close();
			System.out.println("PDF Generado con éxito en: " + archivoFisico.getAbsolutePath());

		} catch (Exception e) {
			System.err.println("Error al generar el PDF: " + e.getMessage());
		}
	}
}