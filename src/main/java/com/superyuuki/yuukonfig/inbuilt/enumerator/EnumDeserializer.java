package com.superyuuki.yuukonfig.inbuilt.enumerator;

import com.amihaiemil.eoyaml.YamlNode;
import com.superyuuki.yuukonfig.Priority;
import com.superyuuki.yuukonfig.decompose.Deserializer;
import com.superyuuki.yuukonfig.decompose.DeserializerContext;
import com.superyuuki.yuukonfig.impl.decompose.ParsingFailure;
import com.superyuuki.yuukonfig.request.Request;

import java.util.Arrays;

public class EnumDeserializer implements Deserializer {
    @Override
    public int handles(Class<?> clazz) {
        if (clazz.isEnum()) return Priority.HANDLE;

        return Priority.DONT_HANDLE;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object deserialize(YamlNode node, Request rq, DeserializerContext ctx) throws ParsingFailure {

        String raw = node.asScalar().value();
        Class<Enum<?>> enumClass = (Class<Enum<?>>) rq.requestedClass();

        for (Enum<?> enumConstant : enumClass.getEnumConstants()) {
            String name = enumConstant.name();
            boolean equal = name.equalsIgnoreCase(raw);
            if (equal) {
                return enumConstant;
            }
        }
        throw new ParsingFailure(rq, ctx, String.format("%s is not a valid enum! Supported enums are: %s", raw, Arrays.toString(enumClass.getEnumConstants())));

    }


}
