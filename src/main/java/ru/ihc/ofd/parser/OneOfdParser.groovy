package ru.ihc.ofd.parser

class OneOfdParser {

    ConfigObject config;
    Map<String, FileWriter> outs = [:]
    Map<String, OneOfdTransport> sessions = [:]

    OneOfdParser() {
        config = new ConfigSlurper().parse(new File("config.groovy").text)
    }

    int countRanges() {
        // сколько всего запросов
        def max = 0
        config.kkt.ranges.each { String kkt, Map range ->
            if (range.to == null) {
                def ofd = sessions[range.account]
                range.to = ofd.requestCount(kkt)
                println "${kkt} has ${range.to} tickets"
            }
            max += range.to - range.from + 1
        }
        return max
    }

    void openOut() {
        // подготовка всех файлов
        config.prefixes.each { String prefix ->
            outs[prefix] = new FileWriter("${prefix}.sql", false)
        }
    }

    void auth() {
        // авторизация во все аккаунты
        config.credentials.each { String login, String password ->
            sessions[login] = new OneOfdTransport(login, password)
        }
    }

    void parse() {
        auth()
        openOut()
        def max = countRanges()

        def count = 0
        def percent = 0
        config.kkt.ranges.each { String kkt, Map range ->
            def ofd = sessions[range.account]

            String id = range.id
            int from = range.from
            int to = range.to

            for (int ticketId = from; ticketId <= to; ticketId++) {
                count++
                try {
                    def ticket = ofd.requestTicket(id, "" + ticketId)
                    def name = ticket.ticket.items[0].commodity.name
                    def prefix = config.prefixes.find {
                        name ==~ /^.*${it}(\d+)$/
                    }
                    if (!prefix) {
                        throw new IllegalArgumentException("${kkt} => ${ticketId}: unrecognized name: ${name}")
                    }

                    def txId = (name =~ /^.*${prefix}(\d+)$/)[0][1]
                    def op = ticket.ticket.operationType
                    def price = ticket.ticket.items[0].commodity.price
                    def quantity = ticket.ticket.items[0].commodity.quantity
                    def sum = ticket.ticket.items[0].commodity.sum
                    def transactionId = ticket.ticket.transactionId
                    def date = Date.parse("yyyy-MM-dd'T'HH:mm:ss", ticket.ticket.transactionDate).format("yyyy-MM-dd HH:mm:ss")
                    def url = "https://org.1-ofd.ru/#/kkms/${kkt}/tickets/${transactionId}"

                    def sql = "replace into _ofd_receipts (id, tx_id, dt, tx_type, full_sum, price, quantity, url) values (\"${transactionId}\", ${txId}, \"${date}\", ${op}, ${sum}, ${price}, ${quantity}, \"${url}\");"
                    outs[prefix].write(sql + "\n")

                    // прогресс
                    def nextPercent = Math.round(100d * count / max)
                    if (nextPercent > percent) {
                        percent = nextPercent
                        println "${kkt} => ${ticketId} / ${percent}%"
                    }
                } catch (Exception e) {
                    println "${e}"
                }
            }
        }

        outs.values()*.close()
    }

    static void main(String[] args) {
        new OneOfdParser().parse()
    }
}
