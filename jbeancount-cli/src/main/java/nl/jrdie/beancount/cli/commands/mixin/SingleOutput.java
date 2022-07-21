package nl.jrdie.beancount.cli.commands.mixin;

import java.nio.file.Path;
import picocli.CommandLine.Option;

public class SingleOutput {

  @Option(
      names = {"-o", "--output"},
      description = "The output file. When this is not specified stdout will be used")
  private Path output;

  public Path outputFile() {
    return output;
  }

  public boolean hasOutput() {
    return output != null;
  }
}
