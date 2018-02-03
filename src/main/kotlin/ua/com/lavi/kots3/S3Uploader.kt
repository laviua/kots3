package ua.com.lavi.kots3

import com.amazonaws.AmazonClientException
import com.amazonaws.AmazonServiceException
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.PutObjectRequest
import java.io.File

/**
 * Created by Oleksandr Loushkin on 11.02.17.
 */

class S3Uploader(val amazonS3: AmazonS3) {

    fun upload(bucketName: String, sourceFile: File, s3FullPath: String) {
        println("Uploading object from $sourceFile into $bucketName...")
        try {
            amazonS3.putObject(PutObjectRequest(bucketName, s3FullPath, sourceFile))
            println("Uploading is completed.")
        }
        catch (ase: AmazonServiceException) {
            println("Error Message:    ${ase.message}")
            println("HTTP Status Code: ${ase.statusCode}")
            println("AWS Error Code:   ${ase.errorCode}")
            println("Error Type:       ${ase.errorType}")
            println("Request ID:       ${ase.requestId}")
            throw ase
        } catch (ace: AmazonClientException) {
            println("Caught an AmazonClientException, which means the client encountered an internal error while trying to communicate with S3, such as not being able to access the network.")
            println("Error Message: " + ace.message)
            throw ace
        } catch (e: Exception) {
            print(e)
            throw e
        }
    }
}