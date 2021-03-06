package com.superyuuki.yuukonfig.serializer.bad;


import com.amihaiemil.eoyaml.YamlNode;
import com.superyuuki.yuukonfig.YuuKonfig;
import com.superyuuki.yuukonfig.inbuilt.section.ImpossibleAccessException;
import com.superyuuki.yuukonfig.user.Section;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ReflectiveSerializerTest {

    @Test
    public void testInlinePrivateConfigShouldFail() {

        Assertions.assertThrows(ImpossibleAccessException.class, () -> {
            YamlNode node = YuuKonfig.instance().test().serializeTest(InlinePrivateConfig.class);
        });

    }

    interface InlinePrivateConfig extends Section {

        default Integer someValue() {
            return 5;
        }

    }

    @Test
    public void testPrivateConfigShouldFail() {

        Assertions.assertThrows(ImpossibleAccessException.class, () -> {
            YamlNode node = YuuKonfig.instance().test().serializeTest(PackagePrivateConfig.class);
        });
    }

    public interface PublicInlineConfig extends Section {

        default Integer test() {
            return 0;
        }

    }

    @Test
    public void testPublicInlineConfigShouldPass() {

        Assertions.assertDoesNotThrow(() -> {
            YamlNode node = YuuKonfig.instance().test().serializeTest(PublicInlineConfig.class);
        });
    }

    public interface ConfigWithParams extends Section {
        default Integer badMethod(Integer unwantedParam) {
            return 4;
        }
    }

    @Test
    public void testConfigWithParamsShouldFail() {
        Assertions.assertThrows(IllegalStateException.class, () -> {
            YamlNode node = YuuKonfig.instance().test().serializeTest(ConfigWithParams.class);
        });
    }

}
