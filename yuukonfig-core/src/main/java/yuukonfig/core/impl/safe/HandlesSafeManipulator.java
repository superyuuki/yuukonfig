package yuukonfig.core.impl.safe;

import yuukonfig.core.err.BadValueException;
import yuukonfig.core.manipulation.Manipulator;
import yuukonfig.core.manipulation.ManipulatorConstructor;
import yuukonfig.core.manipulation.Priority;
import yuukonfig.core.node.Node;
import xyz.auriium.yuukonstants.GenericPath;

public class HandlesSafeManipulator<T> implements Manipulator {


    public static <T> ManipulatorConstructor ofSpecific(Class<T> clazz, ManipulatorConstructorSafe<T> ctor) {
        return (manipulation, useClass, useType, factory) ->  {
            ManipulatorSafe<T> safe = ctor.construct(manipulation, clazz, useType, factory);
            return new HandlesSafeManipulator<>(useClass, clazz, safe, true);
        };
    }

    public static <T> ManipulatorConstructor ofSubclass(Class<T> clazz, ManipulatorConstructorSafe<T> ctor) {
        return (manipulation, useClass, useType, factory) ->  {
            ManipulatorSafe<T> safe = ctor.construct(manipulation, clazz, useType, factory);
            return new HandlesSafeManipulator<>(useClass, clazz, safe, false);
        };
    }

    final Class<?> useClass;
    final Class<T> targetClass;
    final ManipulatorSafe<T> manipulator;
    final boolean onlySpecificClass;

    HandlesSafeManipulator(Class<?> useClass, Class<T> targetClass, ManipulatorSafe<T> manipulator, boolean onlySpecificClass) {
        this.useClass = useClass;
        this.targetClass = targetClass;
        this.manipulator = manipulator;
        this.onlySpecificClass = onlySpecificClass;
    }


    @Override
    public int handles() {
        if (onlySpecificClass) {
            return useClass == targetClass ? Priority.HANDLE : Priority.DONT_HANDLE;
        }

        //baseClass#isAssignableFrom
        return targetClass.isAssignableFrom(useClass) ? Priority.HANDLE : Priority.DONT_HANDLE;
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
