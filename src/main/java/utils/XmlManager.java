package utils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;

public class XmlManager {
    public static String convertirAXml(Object objeto, Class<?> clase) throws Exception {
        JAXBContext context = JAXBContext.newInstance(clase);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        
        StringWriter sw = new StringWriter();
        marshaller.marshal(objeto, sw);
        return sw.toString();
    }
}