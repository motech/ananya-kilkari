package org.motechproject.ananya.kilkari.web;


import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Ignore;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

@Ignore
public class TestUtils {
    public static String toJson(Object objectToSerialize) {
        ObjectMapper mapper = new ObjectMapper();
        StringWriter stringWriter = new StringWriter();
        try {
            mapper.writeValue(stringWriter, objectToSerialize);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringWriter.toString();
    }

    public static <T> T fromJson(String jsonString, Class<T> subscriberResponseClass) {
        ObjectMapper mapper = new ObjectMapper();
        T serializedObject = null;
        try {
            serializedObject = mapper.readValue(jsonString, subscriberResponseClass);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return serializedObject;
    }

    public static <T> T fromXml(String xmlString, Class className) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(className);
        Unmarshaller u = jc.createUnmarshaller();
        return (T) u.unmarshal(new StringReader(xmlString));
    }
}
