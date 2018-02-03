package ua.com.lavi.kots3

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class PathBuilderTest {

    @Test
    fun shouldGetOneFile() {
        val pathes = PathBuilder.pathes("./*.properties")
        Assertions.assertTrue(pathes.size == 1, "Files is not 1")
        Assertions.assertTrue(pathes[0].path == "./gradle.properties", "File name is not gradle.properties")
    }

    @Test
    fun shouldGetTwoFiles() {
        val pathes = PathBuilder.pathes("./*.gradle")
        Assertions.assertTrue(pathes.size == 2, "Files is not 2")
        Assertions.assertTrue(pathes[0].path == "./build.gradle", "File name is not build.gradle")
        Assertions.assertTrue(pathes[1].path == "./settings.gradle", "File name is not settings.gradle")
    }

    @Test
    fun shouldGetAllPropertiesByMaskRecursive() {
        val pathes = PathBuilder.pathes("**/*.properties")
        Assertions.assertTrue(pathes.size == 3, "Files is not 3")
    }

    @Test
    fun shouldGetAllFilesInDirectory() {
        val pathes = PathBuilder.pathes("gradle")
        Assertions.assertTrue(pathes.size == 1, "Files is not 1")
    }
}