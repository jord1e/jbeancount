package nl.jrdie.beancount.cli.commands;

import picocli.CommandLine;

@CommandLine.Command(
    name = "internal",
    description = "Internal commands to debug the utility and library",
    subcommands = {LexCommand.class},
    scope = CommandLine.ScopeType.INHERIT)
public class InternalCommand {}
