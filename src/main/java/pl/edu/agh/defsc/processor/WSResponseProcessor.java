package pl.edu.agh.defsc.processor;

import java.util.List;

public interface WSResponseProcessor {
    List<Object> process(String WSResponse);
}
