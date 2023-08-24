package xyz.auriium.yuukonfig.yaml;

import com.amihaiemil.eoyaml.*;
import com.amihaiemil.eoyaml.extensions.MergedYamlMapping;
import xyz.auriium.yuukonfig.core.err.BadConfigException;
import xyz.auriium.yuukonfig.core.node.Mapping;
import xyz.auriium.yuukonfig.core.node.Node;
import xyz.auriium.yuukonfig.core.node.RawNodeFactory;
import xyz.auriium.yuukonfig.core.node.Sequence;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;

public class YamlRawNodeFactory implements RawNodeFactory {
    @Override
    public SequenceBuilder makeSequenceBuilder() {
        return new SequenceBuilder() {

            YamlSequenceBuilder builder = Yaml.createYamlSequenceBuilder();

            @Override
            public void add(Node node) {
                builder = builder.add(node.rawAccess(YamlNode.class)); //TODO this is horrible
            }

            @Override
            public Sequence build(String... aboveComment) {
                return new YamlSequenceShiv(builder.build(Arrays.asList(aboveComment)));
            }
        };
    }

    @Override
    public MappingBuilder makeMappingBuilder() {
        return new MappingBuilder() {

            YamlMappingBuilder builder = Yaml.createYamlMappingBuilder();

            @Override
            public void add(String key, Node node) {
                builder = builder.add(key, node.rawAccess(YamlNode.class));
            }

            @Override
            public Mapping build(String... aboveComment) {
                return new YamlMappingShiv(builder.build());
            }
        };
    }

    @Override
    public Node scalarOf(String data, String inlineComment, String... aboveComment) {
        if (aboveComment.length == 0) {
            return new YamlScalarShiv(Yaml.createYamlScalarBuilder().addLine(data).buildPlainScalar(inlineComment));
        }
        return new YamlScalarShiv(Yaml.createYamlScalarBuilder().addLine(data).buildPlainScalar(Arrays.asList(aboveComment), inlineComment));

    }

    @Override
    public Node scalarOf(String data, String... aboveComment) {
        if (aboveComment.length == 0) {
            return new YamlScalarShiv(Yaml.createYamlScalarBuilder().addLine(data).buildPlainScalar());

        }

        return new YamlScalarShiv(Yaml.createYamlScalarBuilder().addLine(data).buildPlainScalar(Arrays.asList(aboveComment), ""));
    }

    @Override
    public Mapping loadString(String simulatedConfigInStringForm) {
        try {
            return new YamlMappingShiv(Yaml.createYamlInput(simulatedConfigInStringForm).readYamlMapping());
        }catch (IOException e) {
            throw new BadConfigException("bad config: " + e.getMessage());
        }

    }

    @Override
    public Mapping loadFromFile(Path path) {
        try {
            return new YamlMappingShiv(Yaml.createYamlInput(path.toFile()).readYamlMapping()); //read map
        } catch (FileNotFoundException e) {
            return new YamlMappingShiv(Yaml.createYamlMappingBuilder().build()); //empty map
        } catch (IOException e) {
            throw new BadConfigException(
                    String.format("Something went really wrong loading a config titled: %s, an IO exception: %s: was thrown.", path, e)
            );
        }
    }

    @Override
    public Mapping mergeMappings(Mapping one, Mapping two) {
        YamlMapping oneYaml = one.rawAccess(YamlNode.class).asMapping();
        YamlMapping twoYaml = two.rawAccess(YamlNode.class).asMapping();

        MergedYamlMapping merged = new MergedYamlMapping(oneYaml, twoYaml, false);
        return new YamlMappingShiv(merged);
    }

    @Override
    public void writeToFile(Mapping toWrite, Path location) {
        try {

            YamlPrinter printer = Yaml.createYamlPrinter(new FileWriter(location.toFile()));
            printer.print(toWrite.rawAccess(YamlNode.class));


        } catch (IOException e) {
            throw new BadConfigException(
                    String.format("Something went really wrong loading a config titled: %s, an IO exception: %s: was thrown.", location, e)
            );
        }


    }
}
