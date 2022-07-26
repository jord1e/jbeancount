package nl.jrdie.beancount.cli.commands;

import nl.jrdie.beancount.cli.commands.jordie.JordieCommand;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(
    name = "internal",
    description = "Internal commands to debug the utility and library",
    subcommands = {LexCommand.class, JordieCommand.class},
    scope = CommandLine.ScopeType.INHERIT)
public class InternalCommand {}
