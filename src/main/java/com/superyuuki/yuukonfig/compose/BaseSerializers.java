package com.superyuuki.yuukonfig.compose;

import com.amihaiemil.eoyaml.YamlNode;
import com.superyuuki.yuukonfig.error.NoHandlerFailure;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class BaseSerializers implements Serializers {

    private final List<Serializer> serializers;

    public BaseSerializers(List<Serializer> serializers) {
        this.serializers = serializers;
    }

    @Override
    public YamlNode serialize(Object object) {
        Class<?> clazz = object.getClass();

        Optional<Serializer> ser = serializers.stream().max(Comparator.comparing(v -> v.handles(clazz)));

        if (ser.isPresent()) {
            Serializer serializer = ser.get();
            if (serializer.handles(clazz) > 0) {

                return serializer.serializeObject(clazz, object, this);
            }
        }

        throw new NoHandlerFailure(clazz);
    }

    @Override
    public YamlNode serializeDefault(Class<?> clazz) {
        Optional<Serializer> ser = serializers.stream().max(Comparator.comparing(v -> v.handles(clazz)));

        if (ser.isPresent()) {
            Serializer serializer = ser.get();
            if (serializer.handles(clazz) > 0) {

                return serializer.serializeDefault(clazz, this);
            }
        }

        throw new NoHandlerFailure(clazz);
    }
}
