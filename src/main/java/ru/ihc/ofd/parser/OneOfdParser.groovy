package ru.ihc.ofd.parser

class OneOfdParser {

    static void main(String[] args) {
        ConfigObject config = new ConfigSlurper().parse(new File("config.groovy").text)

        def ofd = new OneOfdTransport(config.play.session)

        config.kkt.ranges.each { String kkt, Map range ->
            String id = range.id
            int from = range.from
            int to = range.to

            for (int ticketId = from; ticketId <= to; ticketId++) {
                try {
                    def ticket = ofd.loadTicket(id, "" + ticketId)
                    def name = ticket.ticket.items[0].commodity.name
                    def txId = (name =~ /^.*ihc(\d+)$/)[0][1]
                    def op = ticket.ticket.operationType
                    def price = ticket.ticket.items[0].commodity.price
                    def quantity = ticket.ticket.items[0].commodity.quantity
                    def sum = ticket.ticket.items[0].commodity.sum
                    def transactionId = ticket.ticket.transactionId
                    def date = Date.parse("yyyy-MM-dd'T'HH:mm:ss", ticket.ticket.insertedAt).format("yyyy-MM-dd HH:mm:ss")
                    def url = "https://org.1-ofd.ru/#/kkms/${kkt}/tickets/${transactionId}"

                    println "replace into _ofd_receipts (id, tx_id, dt, tx_type, full_sum, price, quantity, url) values (\"${transactionId}\", ${txId}, \"${date}\", ${op}, ${sum}, ${price}, ${quantity}, \"${url}\");"
                } catch (Exception e) {
                    println "-- ${e}"
                }
            }
        }
    }

}
