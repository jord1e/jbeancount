package nl.jrdie.beancount.cli.picocli;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import picocli.CommandLine;

public class PathConverter implements CommandLine.ITypeConverter<Path> {
  @Override
  public Path convert(String value) throws Exception {
    String sep = FileSystems.getDefault().getSeparator();
    if (value != null && !value.contains(sep) && !value.isEmpty() && value.charAt(0) != '.') {
      value = "." + sep + value;
    }
    return Paths.get(value);
  }
}
