package rpost.anxb

import com.sun.tools.xjc.Driver
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.io.File

class AnnotatePluginTest {

    companion object {

        const val OUTPUT_DIR = "tmp/"

        @JvmStatic
        @BeforeAll
        fun cleanOutputDir() {
            val outputDirFile = File(OUTPUT_DIR)
            outputDirFile.mkdirs()
            for (file in outputDirFile.list() ?: arrayOf()) {
                File(file).deleteRecursively()
            }
        }
    }

    @Test
    fun testAnnotate() {
        Driver.run(
            arrayOf(
                "-extension",
                "-Xanxb-annotate",
                "-npa",
                "-no-header",
                "-b",
                "example/jaxb-bindings.xml",
                "-d",
                OUTPUT_DIR,
                "example/schema.xsd"
            ),
            System.out,
            System.out
        )

        assertFilesHaveSameContent(OUTPUT_DIR + "com/example/Address.java", "example/com/example/Address.java")
        assertFilesHaveSameContent(OUTPUT_DIR + "com/example/Status.java", "example/com/example/Status.java")
    }

    private fun assertFilesHaveSameContent(actual: String, expected: String) {
        assertThat(File(actual)).hasSameContentAs(File(expected))
    }
}