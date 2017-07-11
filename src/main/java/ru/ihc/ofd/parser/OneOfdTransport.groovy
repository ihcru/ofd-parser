package ru.ihc.ofd.parser

import groovy.json.JsonSlurper;

class OneOfdTransport {

    String baseUrl = "https://org.1-ofd.ru/api/"
    String playSession

    OneOfdTransport(String playSession) {
        this.playSession = playSession
    }

    OneOfdTransport(String login, String password) {
        this.playSession = this.login(login, password)
    }

    Map request(String url) {
        URL apiUrl = new URL(url)
        HttpURLConnection urlConn = (HttpURLConnection) apiUrl.openConnection();
        urlConn.setRequestProperty("Cookie", playSession);
        def result = new JsonSlurper().parse(urlConn.inputStream.newReader())
        urlConn.disconnect()
        return result
    }

    String login(String login, String password) {
        def url = ("${baseUrl}user/login")
        def json = "{\"login\":\"${login}\",\"password\":\"${password}\"}"

        URL apiUrl = new URL(url)
        HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
        connection.setRequestProperty("Content-Length", "" + Integer.toString(json.getBytes().length));
        connection.setUseCaches(false);
        connection.setDoInput(true);
        connection.setDoOutput(true);

        DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
        wr.writeBytes(json);
        wr.flush();
        wr.close();

        def cookie = connection.getHeaderField("Set-Cookie");

        connection.disconnect()

        return cookie
    }

    Map requestTicket(String kkt, String ticketId) {
        return request("${baseUrl}ticket/${kkt}_${ticketId}")
    }

    int requestCount(String kkt) {
        return request("${baseUrl}kkms/${kkt}/transactions?page=1&pageSize=1").transactions[0].fiscalDocumentNumber
    }
}
