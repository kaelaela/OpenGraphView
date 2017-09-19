package me.kaelaela.opengraphview;

import java.io.IOException;
import java.io.InputStream;
import me.kaelaela.opengraphview.network.model.OGData;

public interface Parser {
    OGData parse(InputStream inputStream) throws IOException;
}
