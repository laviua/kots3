package ua.com.lavi.kots3

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class FilePathResolverTest {

    @Test
    fun shouldGetTwoFiles() {
        val pathes = FilePathResolver.resolvePath("./*.gradle")
        Assertions.assertTrue(pathes.size == 2, "Files is not 2")
        Assertions.assertTrue(pathes[0].path == "./build.gradle", "File name is not build.gradle")
        Assertions.assertTrue(pathes[1].path == "./settings.gradle", "File name is not settings.gradle")
    }

    @Test
    fun shouldGetAllPropertiesByMaskRecursive() {
        val pathes = FilePathResolver.resolvePath("**/*.properties")
        Assertions.assertTrue(pathes.size == 3, "Files is not 3")
    }

    @Test
    fun shouldGetAllFilesInDirectory() {
        val pathes = FilePathResolver.resolvePath("gradle/**")
        Assertions.assertTrue(pathes.size == 1, "Files is not 1")
    }

    @Test
    fun shouldGetAllKotlinFiles() {
        val pathes = FilePathResolver.resolvePath("./**/*.kt")
        Assertions.assertTrue(pathes.size == 6, "Files is not 6")
    }
}