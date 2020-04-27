package nl.bertriksikken.loraforwarder;

import java.time.Duration;

public final class LoraForwarderConfigStub implements ILoraForwarderConfig {

    private final String encoding;

    public LoraForwarderConfigStub(String encoding) {
        this.encoding = encoding;
    }

    @Override
    public String getMqttUrl() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getMqttAppId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getMqttAppKey() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getEncoding() {
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

}
