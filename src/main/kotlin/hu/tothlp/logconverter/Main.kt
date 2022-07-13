package hu.tothlp.logconverter

import picocli.CommandLine
import picocli.CommandLine.*
import picocli.CommandLine.Model.*
import kotlin.system.exitProcess

@Command(name = "log-converter", mixinStandardHelpOptions = true, version = ["log-converter 1.0"],
    description = ["Converts a .txt log file to .csv"],
subcommands = [
    Hello::class
])
class ConverterMain : Runnable {

    @Option(names = ["-c"])
    var config: Boolean = false

    @Spec
    var spec: CommandSpec? = null

    override fun run() {
        when {
            config -> println("config")
            else -> spec?.commandLine()?.usage(System.out)
        }
    }
}

@Command(name = "hello", description = ["Short intro"])
class Hello : Runnable {

    override fun run() {
        println("Hello")
    }
}

fun main(args: Array<String>) : Unit = exitProcess(CommandLine(ConverterMain()).execute(*args))