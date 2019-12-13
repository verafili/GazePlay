package net.gazeplay.commons.configuration.observableproperties;

import javafx.beans.property.DoublePropertyBase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

@RequiredArgsConstructor
@Slf4j
public abstract class PropertiesDoubleProperty extends DoublePropertyBase {

    private final String propertyName;

    private final double defaultValue;

    private final PropertyChangeListener propertyChangeListener;

    @Override
    public Object getBean() {
        return null;
    }

    @Override
    public String getName() {
        return propertyName;
    }

    protected abstract void setProperty(String propertyName, String propertyValue);

    protected abstract String getProperty(String propertyName);

    @Override
    public double get() {
        String propertyValue = getProperty(propertyName);
        if (propertyValue == null) {
            return defaultValue;
        }
        return Double.parseDouble(propertyValue);
    }

    @Override
    public void set(double value) {
        log.info("Set property {} to {}", propertyName, value);
        setProperty(propertyName, Double.toString(value));
        propertyChangeListener.propertyChange(new PropertyChangeEvent(this, propertyName, null, value));
    }

}