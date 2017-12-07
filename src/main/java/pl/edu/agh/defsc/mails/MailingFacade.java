package pl.edu.agh.defsc.mails;

import com.mongodb.DBCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import jdk.incubator.http.HttpResponse;

@Component
public class MailingFacade {

    @Autowired
    private MailingAggregator aggregator;

    @Autowired
    private MailingSender sender;

    @Autowired
    private MailsFormatter formatter;

    public void handleNewResponse(HttpResponse<byte []> httpResponse, DBCollection collection) {
        aggregator.aggregateResponseForCollection(httpResponse, collection);
    }

    public void handleNewRequest(DBCollection collection) {
        aggregator.registerNewRequest(collection);
    }

    public void sendDailyMails() {
        String requestCounter = formatter.format(aggregator.getRequestCounter(), "Request Counter");
        String responseAggregator = formatter.format2(aggregator.getResponseAggregation(), "Response Aggregator");
        sender.sendMails(requestCounter, responseAggregator);
        aggregator.clearAggregations();
    }
}
