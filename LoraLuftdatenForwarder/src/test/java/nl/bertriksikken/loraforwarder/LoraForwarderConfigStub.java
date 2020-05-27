package nl.bertriksikken.loraforwarder;

import java.io.File;
import java.time.Duration;

public final class LoraForwarderConfigStub implements ILoraForwarderConfig {

    private final String encoding;

    public LoraForwarderConfigStub(String encoding) {
        this.encoding = encoding;
    }

    @Override
    public String getTtnMqttUrl() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getTtnAppId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getTtnAppKey() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getNodeEncoding() {
        return encoding;
    }

    @Override
    public String getLuftdatenUrl() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Duration getLuftdatenTimeout() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getOpenSenseUrl() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Duration getOpenSenseTimeout() {
        throw new UnsupportedOperationException();
    }

    @Override
    public File getOpenSenseConfigFile() {
        throw new UnsupportedOperationException();
    }

}
