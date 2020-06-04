package domain.xml;

import domain.Message;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Collection;

@Slf4j
public class MessageBuilder {
    public static String buildDocument(Document document, Collection<Message> messagesList) {
        log.debug("Create document start");

        //Create DOM of XML document
        Element rootElement = document.createElement("messages");
        document.appendChild(rootElement);

        log.trace("Create root element: " + rootElement.toString());

        for (Message message : messagesList) {
            Element messageElement = document.createElement("message");
            rootElement.appendChild(messageElement);
            if (message.getId() != null) {
                messageElement.setAttribute("id", message.getId().toString());
            }
            messageElement.setAttribute("sender", message.getUserNF());
            messageElement.setAttribute("receiver", message.getUserNT());
            messageElement.setAttribute("moment",
                    (new SimpleDateFormat("HH:mm:ss dd-MM-yyyy")
                            .format(message.getMoment().getTime())));
            messageElement.appendChild(document.createTextNode(message.getText()));

            log.trace("Create message element: " + messageElement.toString());
        }
        //так чтоб в нашем методе нельзя будет сохранить не указав UTF-8
        DOMImplementation impl = document.getImplementation();
        DOMImplementationLS implLS = (DOMImplementationLS) impl.getFeature("LS", "3.0");

        LSSerializer ser = implLS.createLSSerializer();
        ser.getDomConfig().setParameter("format-pretty-print", true);

        LSOutput lsOutput = implLS.createLSOutput();
        lsOutput.setEncoding("UTF-8");

        Writer strWriter = new StringWriter(); //поток будет записывать в строку
        lsOutput.setCharacterStream(strWriter);

        ser.write(document, lsOutput);
        String result = strWriter.toString();
        log.debug("Create document end");
        log.trace(result);
        return result;
    }
}
