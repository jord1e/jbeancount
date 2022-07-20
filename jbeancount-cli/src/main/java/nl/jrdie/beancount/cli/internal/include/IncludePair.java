package nl.jrdie.beancount.cli.internal.include;

import java.nio.file.Path;

public record IncludePair(Path fromJournal, Path toJournal) {}
