package com.superyuuki.yuukonfig.inbuilt;

import com.amihaiemil.eoyaml.Node;
import com.amihaiemil.eoyaml.YamlNode;
import com.superyuuki.yuukonfig.YuuKonfig;
import com.superyuuki.yuukonfig.serializer.bad.InvalidSerializerTest;
import com.superyuuki.yuukonfig.user.Section;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ListTest {

    static final String COMPLEX_CONFIG = """
            
            subsections:
              -
                hello: 0
                goodbye: bye
              -
                hello: 5
                goodbye: getOutOfMyHouse
            
            
            """;
    static final String DEFAULT_CONFIG = """
            
            stringsList:
              - hi
              - bye
            integerList:
              - 4
              - 3
            
            """;


    @Test
    public void testSerializingDefaultList() {
        YamlNode node = YuuKonfig.instance().test().serializeTest(DefaultListConfig.class);

        Assertions.assertEquals(Node.SEQUENCE, node.asMapping().value("stringsList").type());
        Assertions.assertEquals(3, node.asMapping().value("integerList").asSequence().integer(1));

    }


    public interface DefaultListConfig extends Section {

        default List<String> stringsList() {
            List<String> list = new ArrayList<>();

            list.add("hi");
            list.add("bye");

            return list;
        }

        default List<Integer> integerList() {
            List<Integer> list = new ArrayList<>();

            list.add(4);
            list.add(3);

            return list;
        }

    }

    @Test
    public void testSerializingComplexList() {
        YamlNode node = YuuKonfig.instance().test().serializeTest(ComplexListConfig.class);

        System.out.println("oo");

        Assertions.assertEquals(Node.SEQUENCE, node.asMapping().value("subsections").type());
        Assertions.assertEquals("getOutOfMyHouse", node.asMapping().value("subsections").asSequence().yamlMapping(1).string("goodbye"));
    }

    public interface ComplexListConfig extends Section {
        interface SubSection extends Section {
            Integer hello();
            String goodbye();
        }

        record SubSectionImpl(Integer hello, String goodbye) implements SubSection {}

        default List<SubSection> subsections() {

            List<SubSection> saneTestNames = new ArrayList<>();

            saneTestNames.add(new SubSectionImpl(0, "bye"));
            saneTestNames.add(new SubSectionImpl(5, "getOutOfMyHouse"));

            return saneTestNames;
        }
    }


    @Test
    public void testDeserializingDefaultList() throws IOException {
        DefaultListConfig config = YuuKonfig.instance().test().deserializeTest(DEFAULT_CONFIG, DefaultListConfig.class);

        Assertions.assertEquals(4, config.integerList().get(0));
    }


    @Test
    public void testDeserializingComplexList() throws IOException {
        ComplexListConfig config = YuuKonfig.instance().test().deserializeTest(COMPLEX_CONFIG, ComplexListConfig.class);

        Assertions.assertEquals(0, config.subsections().get(0).hello());
    }

}
