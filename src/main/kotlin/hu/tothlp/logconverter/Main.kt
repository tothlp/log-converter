package hu.tothlp.logconverter

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import picocli.CommandLine
import picocli.CommandLine.*
import picocli.CommandLine.Model.CommandSpec
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.system.exitProcess


@Command(
    name = "log-converter", mixinStandardHelpOptions = true, version = ["log-converter 1.0"],
    description = ["Converts a .txt log file to .csv"],
)
class ConverterMain : Runnable {

    @Option(names = ["-p", "--print"], description = ["Print current configuration"])
    var print: Boolean = false

    @Option(names = ["-c", "--config"], description = ["Specify the configuration file"])
    lateinit var config: File

    @ArgGroup(exclusive = false, multiplicity = "0..1")
    var conversionOptions: ConversionOptions? = null

    class ConversionOptions {
        @Option(names = ["-i", "--input"], required = true)
        var input: String = ""
        @Option(names = ["-o", "--output"], required = true)
        var output: String = ""
    }

    @Spec
    var spec: CommandSpec? = null

    override fun run() {
        val configPath = loadConfig("config.yml")
        println(configPath.regex)

        when {
            print -> println("config")
            conversionOptions != null -> conversionOptions?.let { convert(it.input, it.output) }
            else -> spec?.commandLine()?.usage(System.out)
        }
    }
}

fun convert(input: String, output: String) {
    //TODO: file checking
    val inputFile = File(input)
    val outputFile = File(output)
    //TODO: move to config
    val regex = Regex("(\\d{4}-\\d{2}-\\d{2}) (\\d{2}:\\d{2}:\\d{2}) (.*)  - ")
    val replacePattern = "\$1\t$2\t$3\t"
    outputFile.bufferedWriter().use { writer ->
        inputFile.useLines { lines ->
            lines.forEach {
                writer.appendLine(regex.replace(it, replacePattern))
            }
        }
    }

    println("Success!")
}

fun loadConfig(path: String): Config {
    val mapper = ObjectMapper(YAMLFactory()) // Enable YAML parsing
    mapper.registerModule(KotlinModule()) // Enable Kotlin support

    val file = File(ClassLoader.getSystemResource(path).file)
    return file.bufferedReader().use {
        mapper.readValue(it, Config::class.java)
    }
}

fun main(args: Array<String>): Unit = exitProcess(CommandLine(ConverterMain()).execute(*args))