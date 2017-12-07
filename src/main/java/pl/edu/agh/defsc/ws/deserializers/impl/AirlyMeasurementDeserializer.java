package pl.edu.agh.defsc.ws.deserializers.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import jdk.incubator.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.edu.agh.defsc.ws.deserializers.WSResponseDeserializer;

import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

@Component
public class AirlyMeasurementDeserializer implements WSResponseDeserializer {
    private final static Logger log = LoggerFactory.getLogger(AirlyMeasurementDeserializer.class);

    @Autowired
    private ObjectMapper mapper;

    @Override
    public List<Map> deserialize(HttpResponse<byte []> WSResponse) {
        byte [] body = WSResponse.body();
        String bodyString = new String(body);

        if (isGzipEncoded(WSResponse))
        {
            bodyString = decompress(body);
            log.info("Deserializing gziped body");
        }


        List<Map> historyOfMeasurements = tryToDeserialize(bodyString);

        return historyOfMeasurements;
    }

    private boolean isGzipEncoded(HttpResponse<byte []> wsResponse) {
        return wsResponse.headers().map().getOrDefault("content-encoding", Collections.EMPTY_LIST).contains("gzip");
    }

    private String decompress(byte [] ressponseBody) {
        byte [] responseBodyBytesArray = ressponseBody;

        final StringBuilder outStr = new StringBuilder();
        if ((responseBodyBytesArray == null) || (responseBodyBytesArray.length == 0)) {
            return "";
        }
        if (isValidGzip(responseBodyBytesArray)) {
            GZIPInputStream gis = null;
            try {
                gis = new GZIPInputStream(new ByteArrayInputStream(responseBodyBytesArray));
            } catch (IOException e) {
                e.printStackTrace();
            }
            BufferedReader bufferedReader = null;
            try {
                bufferedReader = new BufferedReader(new InputStreamReader(gis, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            String line;
            try {
                while ((line = bufferedReader.readLine()) != null) {
                    outStr.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            outStr.append(responseBodyBytesArray);
        }
        return outStr.toString();
    }

    private static boolean isValidGzip(final byte[] compressed) {
        return (compressed[0] == (byte) (GZIPInputStream.GZIP_MAGIC)) && (compressed[1] == (byte) (GZIPInputStream.GZIP_MAGIC >> 8));
    }

    public List<Map> tryToDeserialize(String responseBody) {
        try {
            Map map = mapper.readValue(responseBody, Map.class);
            return (List) map.getOrDefault("history", Collections.emptyList());
        } catch (IOException e) {
            log.warn("Unable to deserialize following response {}", responseBody, e);
            return Collections.emptyList();
        }
    }
}
