package xyz.auriium.yuukonfig.toml;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import xyz.auriium.yuukonfig.core.node.Mapping;
import xyz.auriium.yuukonfig.core.node.Node;
import xyz.auriium.yuukonfig.core.node.RawNodeFactory;
import xyz.auriium.yuukonfig.core.node.Sequence;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TomlNodeFactory implements RawNodeFactory {
    @Override
    public SequenceBuilder makeSequenceBuilder() {
        return new SequenceBuilder() {

            final List<Node> internal = new ArrayList<>();

            @Override
            public void add(Node node) {
                internal.add(node);
            }

            @Override
            public Sequence build(String... aboveComment) {
                return new TomlSequence(internal);
            }
        };
    }

    @Override
    public MappingBuilder makeMappingBuilder() {
        return new MappingBuilder() {

            final Map<String, Node> internal = new HashMap<>();

            @Override
            public void add(String key, Node node) {
                internal.put(key, node);
            }

            @Override
            public Mapping build(String... aboveComment) {
                return new TomlMapping(internal);
            }
        };
    }

    @Override
    public Node scalarOf(String data, String inlineComment, String... aboveComment) {
        return new TomlScalar(data);
    }

    @Override
    public Node scalarOf(String data, String... aboveComment) { //comments disappear :(
        return new TomlScalar(data);
    }

    @Override
    public Mapping loadString(String simulatedConfigInStringForm) {
        return loadFromToml(new Toml().read(simulatedConfigInStringForm));
    }

    @Override
    public Mapping loadFromFile(Path path) {
        return loadFromToml(new Toml().read(path.toFile()));
    }


    Mapping loadFromToml(Toml populatedToml) {
        var rootMap = populatedToml.toMap();
        return iterateMap(rootMap);
    }



    Sequence iterateList(List<Object> list) {

        List<Node> toNode = new ArrayList<>();

        for (Object ob : list) {
            if (ob instanceof List<?>) {
                toNode.add(iterateList((List<Object>) ob));
                continue;
            }

            if (ob instanceof Map<?,?>) {
                toNode.add(iterateMap((Map<String, Object>) ob));
                continue;
            }

            toNode.add(new TomlScalar(ob));
            continue;
        }

        return new TomlSequence(toNode);
    }

    Mapping iterateMap(Map<String, Object> map) {
        Map<String, Node> toNode = new HashMap<>();

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();

            if (entry.getValue() instanceof List<?>) { //seqeuence
                List<Object> sublist = (List<Object>) entry.getValue();
                Sequence subnode = iterateList(sublist);
                toNode.put(key, subnode);
                continue;
            }
            if (entry.getValue() instanceof Map<?,?>) { //table
                Map<String, Object> subtable = (Map<String, Object>) entry.getValue();
                Mapping subnode = iterateMap(subtable);
                toNode.put(key, subnode);
                continue;
            }
            //primitive
            toNode.put(key, new TomlScalar( entry.getValue() )); //TODO this needs to be fixed
        }

        return new TomlMapping(toNode);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Node> iterateMerge(Map<String, Node> original, Map<String, Node> newMap) {
        for (String key : newMap.keySet()) {
            Node newNode = newMap.get(key);
            Node oldNode = original.get(key);


            if (newNode.type() == Node.Type.MAPPING && oldNode.type() == Node.Type.MAPPING) {
                Map<String, Node> originalChild = (Map<String, Node>) oldNode.rawAccess(Map.class);
                Map<String, Node> newChild = (Map<String, Node>) newNode.rawAccess(Map.class);
                original.put(key, new TomlMapping(iterateMerge(originalChild, newChild)));
            } else if (newNode.type() == Node.Type.SEQUENCE && oldNode.type() == Node.Type.SEQUENCE) {
                List<Node> originalChild = (List<Node>) oldNode.rawAccess(List.class);
                List<Node> newChild = (List<Node>) newNode.rawAccess(List.class);
                for (Node each : newChild) {
                    if (!originalChild.contains(each)) {
                        originalChild.add(each);
                    }
                }
            } else {
                original.put(key, newMap.get(key));
            }
        }
        return original;
    }

    @Override
    public Mapping mergeMappings(Mapping one, Mapping two) {
        Map<String, Node> original = (Map<String,Node>) one.rawAccess(Map.class);
        Map<String, Node> secondary = (Map<String, Node>) two.rawAccess(Map.class);

        Map<String, Node> map = iterateMerge(original, secondary);
        return new TomlMapping(map);
    }

    @Override
    public void writeToFile(Mapping toWrite, Path location) {
        try {
            new TomlWriter.Builder().build().write(toWrite.rawAccess(Map.class), location.toFile());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

    }
}