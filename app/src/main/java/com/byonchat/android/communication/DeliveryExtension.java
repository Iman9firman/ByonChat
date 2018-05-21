package com.byonchat.android.communication;

import org.jivesoftware.smack.packet.PacketExtension;

public class DeliveryExtension {

    public static final String REQUEST_ELEMENT_NAME = "request";
    public static final String RESPONSE_ELEMENT_NAME = "received";
    public static final String NAMESPACE = "urn:xmpp:receipts";
    public static final String REQUEST_XML_STRING = "<" + REQUEST_ELEMENT_NAME
            + " xmlns=\"" + NAMESPACE + "\"/>";

    public static class Request implements PacketExtension {
        @Override
        public String getElementName() {
            return REQUEST_ELEMENT_NAME;
        }

        @Override
        public String getNamespace() {
            return NAMESPACE;
        }

        @Override
        public String toXML() {
            return REQUEST_XML_STRING;
        }
    }

    public static class Response implements PacketExtension {
        private String packetId;

        public Response(String packetId) {
            this.packetId = packetId;
        }

        @Override
        public String getElementName() {
            return RESPONSE_ELEMENT_NAME;
        }

        @Override
        public String getNamespace() {
            return NAMESPACE;
        }

        public String getPacketId() {
            return packetId;
        }

        @Override
        public String toXML() {
            return "<" + RESPONSE_ELEMENT_NAME + " xmlns=\"" + NAMESPACE
                    + "\" id=\"" + packetId + "\" />";
        }
    }


}
