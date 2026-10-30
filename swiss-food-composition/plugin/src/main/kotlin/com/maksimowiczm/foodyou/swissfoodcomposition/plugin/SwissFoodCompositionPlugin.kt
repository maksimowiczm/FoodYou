package com.maksimowiczm.foodyou.swissfoodcomposition.plugin

import javax.inject.Inject
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import org.jetbrains.compose.ComposeExtension
import org.jetbrains.compose.resources.ResourcesExtension

private const val EXTENSION_NAME = "swissFoodComposition"
private const val COMPILER_CONFIGURATION_NAME = "swissFoodCompositionCompiler"
private const val COMPILER_MAIN_CLASS =
    "com.maksimowiczm.foodyou.swissfoodcomposition.compiler.MainKt"
private const val TASK_NAME = "compileSwissFoodCompositionDatabase"
private const val GENERATED_RESOURCE_DIR = "generated/swissFoodComposition"

abstract class SwissFoodCompositionExtension {
    abstract val dataFile: RegularFileProperty
    abstract val i18nDir: DirectoryProperty
}

@CacheableTask
abstract class CompileSwissFoodCompositionDatabase : DefaultTask() {

    @get:InputFile
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val dataFile: RegularFileProperty

    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val i18nDir: DirectoryProperty

    @get:Classpath abstract val compilerClasspath: ConfigurableFileCollection

    @get:OutputDirectory abstract val outputDirectory: DirectoryProperty

    @get:Inject abstract val execOperations: ExecOperations

    @TaskAction
    fun compile() {
        check(!compilerClasspath.isEmpty) {
            "The Food You's Swiss Food Composition plugin was applied but no compiler was found. " +
                "Use `dependencies { swissFoodCompositionCompiler(...) }` to add a compiler."
        }

        val resourceRoot = outputDirectory.get().asFile
        val output = resourceRoot.resolve("files/SwissFoodCompositionDatabase.db")

        check(output.parentFile.mkdirs() || output.parentFile.isDirectory) {
            "Could not create output directory: ${output.parentFile}"
        }

        execOperations.javaexec {
            classpath = compilerClasspath
            mainClass.set(COMPILER_MAIN_CLASS)
            args(
                "--data",
                dataFile.get().asFile.absolutePath,
                "--i18n",
                i18nDir.get().asFile.absolutePath,
                "--out",
                output.absolutePath,
            )
        }
    }
}

class SwissFoodCompositionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension =
            project.extensions.create(EXTENSION_NAME, SwissFoodCompositionExtension::class.java)

        val compilerConfiguration =
            project.configurations.create(COMPILER_CONFIGURATION_NAME) {
                isCanBeConsumed = false
                isCanBeResolved = true
                description = "Classpath of the Swiss Food Composition database compiler."
            }

        val compileTask =
            project.tasks.register(TASK_NAME, CompileSwissFoodCompositionDatabase::class.java) {
                group = "build"
                description = "Compiles the Swiss Food Composition database."

                dataFile.set(extension.dataFile)
                i18nDir.set(extension.i18nDir)
                compilerClasspath.from(compilerConfiguration)

                outputDirectory.convention(
                    project.layout.buildDirectory.dir(GENERATED_RESOURCE_DIR)
                )
            }

        project.pluginManager.withPlugin("org.jetbrains.compose") {
            project.extensions.configure<ComposeExtension>("compose") {
                (this as ExtensionAware).extensions.configure<ResourcesExtension>("resources") {
                    customDirectory(
                        sourceSetName = "commonMain",
                        directoryProvider = compileTask.flatMap { it.outputDirectory },
                    )
                }
            }
        }
    }
}
