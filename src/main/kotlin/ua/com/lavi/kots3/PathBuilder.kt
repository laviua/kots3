package ua.com.lavi.kots3

import org.codehaus.plexus.util.DirectoryScanner
import java.io.File

object PathBuilder {

    fun pathes(path: String): MutableList<File> {
        val scanner = DirectoryScanner()

        var baseDir: String
        if (path.contains("*")) {
            val split = path.split("/")
            var mask = split[split.size - 1]
            baseDir = path.removeSuffix(mask)
            if (baseDir.isNullOrEmpty() || baseDir.startsWith("*")) {
                baseDir = "."
            }
            if (path.startsWith("*")) {
                mask = path
            }
            scanner.setBasedir(baseDir)
            scanner.setIncludes(arrayOf(mask))
        } else {
            scanner.setBasedir(path)
        }
        scanner.scan()

        val includedFiles = scanner.includedFiles
        val result: MutableList<File> = arrayListOf()

        for (fileName in includedFiles) {
            result.add(File(scanner.basedir.toString() + File.separator + fileName))
        }

        return result
    }
}