package nl.jrdie.beancount.cli.commands.jordie;

import picocli.CommandLine.Command;
import picocli.CommandLine.ScopeType;

@Command(
    name = "jordie",
    description = "Some special tools for myself",
    subcommands = {FixStuffCommand.class, JordieMoveToDesc.class},
    scope = ScopeType.INHERIT)
public class JordieCommand {}
