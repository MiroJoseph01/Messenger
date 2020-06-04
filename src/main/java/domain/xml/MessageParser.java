package domain.xml;

import domain.Message;
import lombok.extern.slf4j.Slf4j;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class MessageParser extends DefaultHandler {


    private Message message;

    public MessageParser(AtomicInteger id, List<Message> messages) {
        this.id = id;
        this.messages = messages;
    }

    public List<Message> getMessages() {
        return messages;
    }

    private List<Message> messages;
    private AtomicInteger id;
    private String thisElement;

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
        log.debug("Start Document");

    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        thisElement = qName;
        log.debug("Start element");
        log.trace("<" + qName);
        if ("message".equals(qName)) {
            Message.Builder builder = Message.newMessage();
            for (int i = 0; i < attributes.getLength(); i++) {
                String attrName = attributes.getLocalName(i);
                String attrValue = attributes.getValue(i);
                log.trace((attrName + "=" + attrValue));
                switch (attrName) {
                    case "sender":
                        builder.from(attrValue);
                        break;
                    case "receiver":
                        builder.to(attrValue);
                        break;
                    case "id":
                        builder.id(Long.valueOf(attrValue));
                        break;
                    case "moment":
                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
                        try {
                            calendar.setTime(format.parse(attrValue));
                        } catch (ParseException e) {
                            log.error(e.getMessage());
                            e.printStackTrace();
                        }
                        builder.moment(calendar);

                }

            }
            message = builder.build();
        }
        log.trace(">");
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if ("message".equals(qName)) {
            Long newId = Long.valueOf(id.getAndIncrement());

            if (message.getId() == null) {
                message.setId(newId);
            } else
            {
                newId = message.getId();
                id.set(newId.intValue());
            }
            log.debug("id = " + newId);
            messages.add(message);
        }
        thisElement = "";
        log.debug("End element");
        log.trace("</" + qName + ">");
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if ("message".equals(thisElement)){
            String messBody = new String(ch, start, length).trim();
            log.trace(messBody);
            message.setText(messBody);
        }
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
        log.debug("End document");
    }
}
