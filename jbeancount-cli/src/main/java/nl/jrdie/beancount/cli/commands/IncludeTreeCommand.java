package nl.jrdie.beancount.cli.commands;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.Callable;
import nl.jrdie.beancount.Beancount;
import nl.jrdie.beancount.cli.internal.include.IncludePair;
import nl.jrdie.beancount.language.IncludePragma;
import nl.jrdie.beancount.language.Journal;
import nl.jrdie.beancount.parser.BeancountUtil;
import nl.jrdie.beancount.util.Assert;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "includes", description = "This command dumps the include pragma mappings")
public class IncludeTreeCommand implements Callable<Integer> {

  @Parameters(index = "0", description = "The Beancount file")
  private Path file;

  @Option(
      names = "--experimental-dot",
      description = "Output the inclusion mappings as a Graphviz DOT string",
      defaultValue = "false")
  private boolean useDot;

  @Option(
      names = "--experimental-relative",
      description = "Outputs relative paths",
      defaultValue = "false")
  private boolean relative;

  @Override
  public Integer call() throws Exception {
    Beancount beancount = Beancount.newBeancount().build();
    Journal journal = beancount.createJournalSync(file);
    Set<IncludePair> includes = new LinkedHashSet<>();
    recurseThroughIncludes(file, journal, includes);
    StringBuilder sb = new StringBuilder();
    Set<String> dotDirs = new HashSet<>();
    for (IncludePair includeMapping : includes) {
      if (useDot) {
        dotDirs.addAll(dotLane(includeMapping.fromJournal(), includeMapping.toJournal(), sb));
      } else {
        sb.append(maybeRelativize(includeMapping.fromJournal()).toString());
        sb.append(" -> ");
        sb.append(maybeRelativize(includeMapping.toJournal()).toString());
        sb.append('\n');
      }
    }
    if (useDot) {
      StringBuilder dotBuilder = new StringBuilder();
      dotBuilder.append("strict digraph {\n");
      dotBuilder.append(" node [shape=plaintext]\n \"");
      dotBuilder.append(file.getFileName().toString());
      dotBuilder.append("\" [shape=septagon]\n");
      if (!dotDirs.isEmpty()) {
        for (String dotDir : dotDirs) {
          dotBuilder.append("  \"");
          dotBuilder.append(dotDir);
          dotBuilder.append("\" [shape=box3d]\n");
        }
      }
      dotBuilder.append(sb);
      dotBuilder.append('}');
      sb = dotBuilder;
    }
    System.out.println(sb);
    return 0;
  }

  private Set<String> dotLane(Path from, Path to, StringBuilder sb) {
    sb.append("  ");
    Set<String> directories = new HashSet<>();
    StringJoiner joiner = new StringJoiner(" -> ", "", "");
    joiner.setEmptyValue("");
    for (Iterator<Path> iterator = maybeRelativize(from).iterator(); iterator.hasNext(); ) {
      Path next = iterator.next();
      if (iterator.hasNext()) {
        directories.add(next.getFileName().toString());
      }
      joiner.add("\"" + next + "\"");
    }
    for (Iterator<Path> iterator = maybeRelativize(to).iterator(); iterator.hasNext(); ) {
      Path next = iterator.next();
      if (iterator.hasNext()) {
        directories.add(next.getFileName().toString());
      }
      joiner.add("\"" + next + "\"");
    }
    sb.append(joiner);
    sb.append("\n");
    return directories;
  }

  private Path maybeRelativize(Path includedPath) {
    if (relative) {
      return file.getParent().relativize(includedPath);
    } else {
      return includedPath;
    }
  }

  private void recurseThroughIncludes(
      Path currentPath, Journal currentJournal, Set<IncludePair> accumulator) {
    Path journalFolder = currentPath.getParent();
    if (!Files.isDirectory(journalFolder)) {
      Assert.shouldNeverHappen();
      return;
    }
    List<IncludePragma> includePragmas =
        BeancountUtil.findDeclarationsOfType(currentJournal, IncludePragma.class);
    for (IncludePragma includePragma : includePragmas) {
      if (includePragma.journal() == null) {
        // TODO Figure out what to do
        Assert.shouldNeverHappen();
        return;
      }
      Path includedFilePath = journalFolder.resolve(includePragma.filename());
      accumulator.add(new IncludePair(currentPath, includedFilePath));
      recurseThroughIncludes(includedFilePath, includePragma.journal(), accumulator);
    }
  }
}
