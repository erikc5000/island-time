import org.gradle.api.internal.AbstractTask
import org.gradle.api.tasks.*
import org.gradle.language.base.plugins.LifecycleBasePlugin
import java.io.File

abstract class RunIosTestsTask : AbstractTask() {
    init {
        group = LifecycleBasePlugin.VERIFICATION_GROUP
        description = "Run tests for target 'ios' on an iOS simulator"
    }

    @Input
    var device = project.findProperty("iosDevice")?.toString() ?: "iPhone Xʀ"

    @InputFile
    @SkipWhenEmpty
    open var binary: File? = null

    @TaskAction
    fun run() {
        binary?.let {
            project.exec {
                commandLine("xcrun", "simctl", "spawn", "--standalone", device, it.absolutePath)
            }
        }
    }
}