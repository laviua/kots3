package ua.com.lavi.kots3

import org.codehaus.plexus.util.DirectoryScanner
import java.io.File

object FilePathResolver {

    fun resolvePath(path: String): MutableList<File> {
        val scanner = DirectoryScanner()
        val basedir = parseBaseDir(path)
        val mask = parseMask(path, basedir)
        scanner.setBasedir(basedir)
        scanner.setIncludes(arrayOf(mask))
        scanner.scan()

        val includedFiles = scanner.includedFiles
        val result: MutableList<File> = arrayListOf()

        includedFiles.mapTo(result) { File(scanner.basedir.toString() + File.separator + it) }

        return result
    }

    private fun parseBaseDir(path: String): String {
        if (File(path).isDirectory) {
            return path
        }
        if (path.startsWith("*")) {
            return "./"
        }
        if (path.contains("*")) {
            return path.substring(0, path.indexOfFirst { it == '*' })
        }
        return path
    }

    private fun parseMask(path: String, basedir: String): String {
        val mask = path.removePrefix(basedir)
        if (mask.isEmpty()) {
            return "*.*"
        }
        return mask
    }
}