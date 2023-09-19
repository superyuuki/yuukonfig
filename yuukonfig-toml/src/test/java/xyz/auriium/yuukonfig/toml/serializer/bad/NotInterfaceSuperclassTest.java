package xyz.auriium.yuukonfig.toml.serializer.bad;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import xyz.auriium.yuukonfig.core.YuuKonfig;
import xyz.auriium.yuukonfig.core.annotate.Section;

public class NotInterfaceSuperclassTest {

    public interface NonInterfaceSubsectionTest extends Section {

        MyClass myClass();

        int num();


        class MyClass implements Section {
            public int configMethod() {
                return 0;
            }

            public String configMethod2() {
                return "unused";
            }
        }


    }

    public interface NonInterfaceRecordTest extends Section {

        interface Subsection extends Section{
            int x();
            String y();
        }

        record SubsectionImpl(int x, String y) implements Subsection {}

        default Subsection subsection() {
            return new SubsectionImpl(2, "no");
        }

        int num();

    }

    @Test
    public void testShouldThrowIfClassIsUsed() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            YuuKonfig.instance().test().serializeTest(NonInterfaceSubsectionTest.class);
        });
    }

    @Test
    public void testShouldNotThrowIfInterfaceUsedWithRecord() {
        YuuKonfig.instance().test().serializeTest(NonInterfaceRecordTest.class);
    }


}
