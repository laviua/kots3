package ua.com.lavi.kots3

import com.amazonaws.AmazonClientException
import com.amazonaws.AmazonServiceException
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import java.io.File


fun main(args: Array<String>) {
    Kots3().start(args)
}

class Kots3 {

    fun start(args: Array<String>) {
        if (args.isEmpty() || args.size != 7) {
            println("Program arguments should be like this example: \r\n" +
                    "java -jar kots3.jar DOWNLOAD <key> <secret> <bucket> <region> s3Folder/file.zip /tmp/file.zip" + "\r\n" +
                    "java -jar kots3.jar UPLOAD <key> <secret> <bucket> <region> /tmp/file.zip s3Folder/file.zip")

            System.exit(1)
        }
        val actionType = args[0]
        val awsKey = args[1]
        val awsSecret = args[2]
        val bucketName = args[3]
        val region = args[4]

        val basicAWSCredentials = BasicAWSCredentials(awsKey, awsSecret)

        val amazonS3 = AmazonS3ClientBuilder.standard()
                .withCredentials(AWSStaticCredentialsProvider(basicAWSCredentials))
                .withRegion(region)
                .build()

        try {
            if (ActionType.valueOf(actionType) == ActionType.DOWNLOAD) {
                val sourceS3Path = args[5]
                val targetFullResourcePath = args[6]

                val content = S3Downloader(amazonS3).download(bucketName, sourceS3Path)
                File(targetFullResourcePath).outputStream().use { content }
            }

            if (ActionType.valueOf(actionType) == ActionType.UPLOAD) {

                val sourceFullResourcePath = args[5]
                val targetS3Resource = args[6]

                S3Uploader(amazonS3).upload(bucketName, File(sourceFullResourcePath), targetS3Resource)

            }
        } catch (ase: AmazonServiceException) {
            println("Error Message:    ${ase.message}")
            println("HTTP Status Code: ${ase.statusCode}")
            println("AWS Error Code:   ${ase.errorCode}")
            println("Error Type:       ${ase.errorType}")
            println("Request ID:       ${ase.requestId}")
            System.exit(1)
        } catch (ace: AmazonClientException) {
            println("Caught an AmazonClientException, which means the client encountered an internal error while trying to communicate with S3, such as not being able to access the network.")
            println("Error Message: " + ace.message)
            System.exit(1)
        } catch (e: Exception) {
            print(e)
            System.exit(1)
        }
    }

    enum class ActionType {
        DOWNLOAD, UPLOAD
    }

}
