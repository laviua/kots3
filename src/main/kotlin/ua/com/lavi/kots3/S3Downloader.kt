package ua.com.lavi.kots3

import com.amazonaws.services.s3.AmazonS3

/**
 * Created by oleksandrloushkin on 11.02.17.
 */

class S3Downloader(val amazonS3: AmazonS3) {

    fun download(bucketName: String, resourceFullPath: String): ByteArray {
        println("Downloading an object $resourceFullPath from $bucketName")
        val readBytes = amazonS3.getObject(bucketName, resourceFullPath).objectContent.readBytes()
        println("Downloading is completed.")
        return readBytes
    }
}