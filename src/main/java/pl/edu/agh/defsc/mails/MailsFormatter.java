package pl.edu.agh.defsc.mails;

import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class MailsFormatter {
    public String format(Map<String, Integer> requestCounter, String name) {
        StringBuilder builder = new StringBuilder();
        builder.append("======" + name + "======\n");
        for (Map.Entry<String, Integer> entry : requestCounter.entrySet()) {
            builder.append(entry.getKey());
            builder.append(" --> ");
            builder.append(entry.getValue());
            builder.append("\n");
        }
        return builder.toString();
    }

    public String format2(Map<String, Map<Integer, Integer>> responseAggregation, String name) {
        StringBuilder builder = new StringBuilder();
        builder.append("======" + name + "======\n");
        for (String entryKey : responseAggregation.keySet()) {
            Map<Integer, Integer> aggregations = responseAggregation.get(entryKey);
            for (Integer entryCode : aggregations.keySet()) {
                builder.append(entryKey);
                builder.append(" ---> ");
                builder.append(entryCode);
                builder.append(" -- ");
                builder.append(aggregations.get(entryCode));
                builder.append("\n");
            }
        }

        return builder.toString();
    }
}
