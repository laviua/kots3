package ua.com.lavi.kots3

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.PutObjectRequest
import java.io.File


/**
 * Created by oleksandrloushkin on 11.02.17.
 */

class S3Uploader(val amazonS3: AmazonS3) {

    fun upload(bucketName: String, sourceFile: File, resourceFullPath: String) {
        println("Uploading an object from $resourceFullPath into $bucketName...")
        amazonS3.putObject(PutObjectRequest(bucketName, resourceFullPath, sourceFile))
        println("Uploading is completed.")
    }
}