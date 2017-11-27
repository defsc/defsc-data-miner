package pl.edu.agh.defsc.processor;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface WSResponseProcessor {
    List<Map> process(String WSResponse) throws IOException;
}
