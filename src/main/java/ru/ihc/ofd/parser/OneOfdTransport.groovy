package ru.ihc.ofd.parser

import groovy.json.JsonSlurper;

class OneOfdTransport {

    String baseUrl = "https://org.1-ofd.ru/api/"
    String playSession

    OneOfdTransport(String playSession) {
        this.playSession = playSession
    }

    Map loadTicket(String kkt, String ticketId) {
        String url = "${baseUrl}ticket/${kkt}_${ticketId}"
        URL apiUrl = new URL(url)
        URLConnection urlConn = apiUrl.openConnection();
        urlConn.setRequestProperty("Cookie", "PLAY_SESSION=${playSession}");
        def result = new JsonSlurper().parse(urlConn.inputStream.newReader())
        return result
    }

}
