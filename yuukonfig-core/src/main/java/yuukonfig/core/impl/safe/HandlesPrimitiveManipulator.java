package yuukonfig.core.impl.safe;

import yuukonfig.core.err.BadValueException;
import yuukonfig.core.manipulation.Manipulator;
import yuukonfig.core.manipulation.ManipulatorConstructor;
import yuukonfig.core.manipulation.Priority;
import yuukonfig.core.node.Node;
import xyz.auriium.yuukonstants.GenericPath;

public class HandlesPrimitiveManipulator<T> implements Manipulator {


    public static <T> ManipulatorConstructor ofSpecific(Class<T> targetClass, Class<?> targetPrimitive, ManipulatorConstructorSafe<T> ctor) {
        return (manipulation, useClass, useType, factory) ->  {
            ManipulatorSafe<T> safe = ctor.construct(manipulation, targetClass, useType, factory);
            return new HandlesPrimitiveManipulator<>(useClass, targetClass, targetPrimitive, safe);
        };
    }

    final Class<?> useClass;
    final Class<T> targetClass;
    final Class<?> targetPrimitive;
    final ManipulatorSafe<T> manipulator;

    HandlesPrimitiveManipulator(Class<?> useClass, Class<T> targetClass, Class<?> targetPrimitive, ManipulatorSafe<T> manipulator) {
        this.useClass = useClass;
        this.targetClass = targetClass;
        this.targetPrimitive = targetPrimitive;
        this.manipulator = manipulator;
    }

    @Override
    public int handles() {
        if (useClass.equals(targetPrimitive) || useClass.equals(targetClass)) return Priority.PRIORITY_HANDLE;

        return Priority.DONT_HANDLE;
    }

    @Override
    public Object deserialize(Node node) throws BadValueException {
        return manipulator.deserialize(node);
    }

    @Override
    public Node serializeObject(Object object, GenericPath path) {
        return manipulator.serializeObject(targetClass.cast(object), path);
    }

    @Override
    public Node serializeDefault(GenericPath path) {
        return manipulator.serializeDefault(path);
    }
}
