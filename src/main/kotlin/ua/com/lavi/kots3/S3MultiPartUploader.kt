package ua.com.lavi.kots3

import com.amazonaws.AmazonClientException
import com.amazonaws.AmazonServiceException
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.CompleteMultipartUploadRequest
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest
import com.amazonaws.services.s3.model.PartETag
import com.amazonaws.services.s3.model.UploadPartRequest
import java.io.File
import java.util.*

/**
 * Created by Oleksandr Loushkin on 04.08.18.
 */

class S3MultiPartUploader(private val amazonS3: AmazonS3) {

    //100 Mbyte part
    private val PART_SIZE : Long = 100 * 1024 * 1024.toLong()

    fun upload(bucketName: String, sourceFile: File, s3FullPath: String) {
        println("Uploading object from $sourceFile into ${amazonS3.region}:$bucketName:$s3FullPath...")
        try {
            val partETags = ArrayList<PartETag>()
            val filename = sourceFile.name
            val initRequest = InitiateMultipartUploadRequest(bucketName, filename)
            val initResponse = amazonS3.initiateMultipartUpload(initRequest)

            var filePosition: Long = 0
            var i = 1
            val contentLength = sourceFile.length()
            var partSize = (PART_SIZE)
            while (filePosition < contentLength) {
                partSize = Math.min(partSize, contentLength - filePosition)

                // Create the request to upload a part.
                val uploadRequest = UploadPartRequest()
                        .withBucketName(bucketName)
                        .withKey(filename)
                        .withUploadId(initResponse.uploadId)
                        .withPartNumber(i)
                        .withFileOffset(filePosition)
                        .withFile(sourceFile)
                        .withPartSize(partSize)

                // Upload the part and add the response's ETag to our list.
                val uploadResult = amazonS3.uploadPart(uploadRequest)
                partETags.add(uploadResult.partETag)
                filePosition += partSize
                i++
            }

            // Complete the multipart upload.
            val compRequest = CompleteMultipartUploadRequest(bucketName, filename, initResponse.uploadId, partETags)
            amazonS3.completeMultipartUpload(compRequest)
            println("Uploading $sourceFile is completed.")
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