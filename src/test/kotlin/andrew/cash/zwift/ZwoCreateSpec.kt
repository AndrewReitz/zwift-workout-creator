package andrew.cash.zwift

import ch.tutteli.spek.extensions.memoizedTempFolder
import com.google.common.truth.Truth.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.nio.file.Paths
import kotlin.test.assertFailsWith

class ZwoCreateSpec : Spek({
  val tempDir by memoizedTempFolder()
  val classUnderTest by memoized { ZwoCreate() }

  describe("output") {

    it("should return default file when no output provided") {
      val input = tempDir.newFile("empty.zwo.kts")

      classUnderTest.parse(listOf(input.toAbsolutePath().toString()))

      assertThat(classUnderTest.output).isEqualTo(Paths.get("workout.zwo"))
    }

    it("should create file inside directory if provided directory is provided") {
      val input = tempDir.newFile("empty.zwo.kts")
      val output = tempDir.newDirectory("output")

      classUnderTest.parse(
        listOf(
          input.toAbsolutePath().toString(), output.toAbsolutePath().toString()
        )
      )

      assertThat(classUnderTest.output).isEqualTo(output.resolve("workout.zwo"))
    }

    it("should append .zwo to input without it") {
      val input = tempDir.newFile("empty.zwo.kts")
      val output = tempDir.resolve("outputFile")

      classUnderTest.parse(
        listOf(
          input.toAbsolutePath().toString(), output.toAbsolutePath().toString()
        )
      )

      assertThat(classUnderTest.output).isEqualTo(output.parent.resolve("outputFile.zwo"))
    }

    it("should fail with if directory doesn't exist") {
      val input = tempDir.newFile("empty.zwo.kts")
      val output = tempDir.resolve("does/not/exist/outputFile.zwo")

      classUnderTest.parse(
        listOf(
          input.toAbsolutePath().toString(), output.toAbsolutePath().toString()
        )
      )

      val result = assertFailsWith<OutputException> {
        classUnderTest.output
      }

      assertThat(result.message).isEqualTo("Output directory does not exist if \"zwocreate\" should create it re-run with -f flag")
    }

    it("should create directory when -f is passed in") {
      val input = tempDir.newFile("empty.zwo.kts")
      val output = tempDir.resolve("does/not/exist/outputFile.zwo")

      classUnderTest.parse(
        listOf(
          input.toAbsolutePath().toString(), output.toAbsolutePath().toString(), "-f"
        )
      )

      val result = classUnderTest.output
      assertThat(result).isEqualTo(output)
      assertThat(result.toFile().isDirectory).isFalse()
      result.parent.toFile().let {
        assertThat(it.exists()).isTrue()
        assertThat(it.isDirectory).isTrue()
      }
    }

    it("should error when parents don't exist and -f is passed in and parents can't be created") {
      val input = tempDir.newFile("empty.zwo.kts")
      val output = tempDir.resolve("does/not/exist/outputFile.zwo")

      classUnderTest.parse(
        listOf(
          input.toAbsolutePath().toString(), output.toAbsolutePath().toString(), "-f"
        )
      )

      tempDir.newDirectory("does").toFile().setReadOnly()

      val expected = assertFailsWith<OutputException> {
        classUnderTest.output
      }

      assertThat(expected.message).isEqualTo("Could not create ${output.toAbsolutePath()}")
    }
  }
})