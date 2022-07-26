package nl.jrdie.beancount.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;
import nl.jrdie.beancount.language.Journal;
import nl.jrdie.beancount.language.JournalDeclaration;
import nl.jrdie.beancount.language.Metadata;
import nl.jrdie.beancount.language.MetadataItem;
import nl.jrdie.beancount.language.MetadataKey;
import nl.jrdie.beancount.language.MetadataLine;
import nl.jrdie.beancount.language.MetadataValue;
import nl.jrdie.beancount.language.StringValue;

public final class BeancountUtil {

  private BeancountUtil() {}

  public static <T extends JournalDeclaration<?, ?>> List<T> findDeclarationsOfType(
      Journal journal, Class<T> type) {
    return journal.declarations().stream().filter(type::isInstance).map(type::cast).toList();
  }

  public static List<JournalDeclaration<?, ?>> findDeclarations(
      Journal journal, Predicate<JournalDeclaration<?, ?>> declarationPredicate) {
    return journalDeclarationStream(journal, declarationPredicate).toList();
  }

  public static <T extends JournalDeclaration<?, ?>> List<T> findDeclarations(
      Journal journal, Class<T> type, Predicate<T> declarationPredicate) {
    return journal.declarations().stream()
        .<T>mapMulti(
            (journalDeclaration, consumer) -> {
              if (type.isInstance(journalDeclaration)) {
                T t = type.cast(journalDeclaration);
                if (declarationPredicate.test(t)) {
                  consumer.accept(t);
                }
              }
            })
        .toList();
  }

  private static Stream<JournalDeclaration<?, ?>> journalDeclarationStream(
      Journal journal, Predicate<JournalDeclaration<?, ?>> declarationPredicate) {
    return journal.declarations().stream().filter(declarationPredicate);
  }

  public static boolean hasMetadataWithKey(Metadata metadata, String key) {
    Objects.requireNonNull(metadata, "metadata");
    Objects.requireNonNull(key, "key");
    return metadata.metadata().stream()
        .anyMatch(ml -> ml instanceof MetadataItem mi && mi.key().key().equals(key));
  }

  public static Metadata addMetadataAtStart(Metadata metadata, MetadataLine... linesToAdd) {
    return metadata.transform(
        builder -> {
          List<MetadataLine> metadataLines = new ArrayList<>(Arrays.asList(linesToAdd));
          metadataLines.addAll(builder.metadata());
          builder.metadata(metadataLines);
        });
  }

  public static Metadata addMetadataAtEnd(Metadata metadata, MetadataLine... linesToAdd) {
    return metadata.transform(
        builder -> {
          List<MetadataLine> metadataLines = builder.metadata();
          metadataLines = new ArrayList<>(metadataLines);
          metadataLines.addAll(Arrays.asList(linesToAdd));
          builder.metadata(metadataLines);
        });
  }

  public static boolean withNewMetadataLine(Metadata metadata, String key) {
    Objects.requireNonNull(metadata, "metadata");
    Objects.requireNonNull(key, "key");
    return metadata.metadata().stream()
        .anyMatch(ml -> ml instanceof MetadataItem mi && mi.key().key().equals(key));
  }

  public static MetadataItem newMetadataItem(String key, String value) {
    return MetadataItem.newMetadataItem()
        .key(MetadataKey.newMetadataKey().key(key).build())
        .value(StringValue.newStringValue().value(value).build())
        .build();
  }

  public static MetadataValue getMetadataValue(Metadata metadata, String key) {
    Objects.requireNonNull(metadata, "metadata");
    Objects.requireNonNull(key, "key");
    return metadata.metadata().stream()
        .filter(ml -> ml instanceof MetadataItem mi && mi.key().key().equals(key))
        .findFirst()
        .map(metadataLine -> ((MetadataItem) metadataLine).value())
        .orElse(null);
  }
}
