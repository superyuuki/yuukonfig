package xyz.auriium.yuukonfig.core.impl.manipulator;

import xyz.auriium.yuukonfig.core.err.BadValueException;

import xyz.auriium.yuukonfig.core.manipulation.Contextual;
import xyz.auriium.yuukonfig.core.manipulation.Manipulation;
import xyz.auriium.yuukonfig.core.manipulation.Priority;
import xyz.auriium.yuukonfig.core.node.Node;
import xyz.auriium.yuukonfig.core.node.RawNodeFactory;
import xyz.auriium.yuukonfig.core.manipulation.Manipulator;

import java.lang.reflect.Type;

public class DoubleManipulator implements Manipulator {

    final Manipulation manipulation;
    final Class<?> useClass;
    final RawNodeFactory factory;

    public DoubleManipulator(Manipulation manipulation, Class<?> useClass, Contextual<Type> typeContextual, RawNodeFactory factory) {
        this.useClass = useClass;
        this.manipulation = manipulation;
        this.factory = factory;
    }

    @Override
    public int handles() {
        if (useClass.equals(Double.class) || useClass.equals(double.class)) return Priority.PRIORITY_HANDLE;

        return Priority.DONT_HANDLE;
    }

    @Override
    public Object deserialize(Node node, String exceptionalKey) throws BadValueException {
        try {
            return Double.parseDouble(node.asScalar().value());
        } catch (NumberFormatException exception) {
            throw new BadValueException(manipulation.configName(), exceptionalKey, "Value is not a double!");
        }
    }

    @Override
    public Node serializeObject(Object object, String[] comment) {
        return factory.scalarOf(
                object.toString(),
                comment
        );

    }

    @Override
    public Node serializeDefault(String[] comment) {
        return factory.scalarOf(
                "0.0",
                "(default double)",
                comment
        );
    }
}