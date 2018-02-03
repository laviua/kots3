package ua.com.lavi.kots3

import com.amazonaws.ClientConfiguration
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.retry.PredefinedRetryPolicies
import com.amazonaws.retry.RetryPolicy
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import java.io.File
import java.nio.file.FileSystems
import java.nio.file.Files


fun main(args: Array<String>) {
    Kots3().start(args)
}

class Kots3 {

    fun start(args: Array<String>) {
        if (args.isEmpty() || args.size < 5) {
            println("Program arguments: \r\n" +
                    "java -jar kots3.jar DOWNLOAD <key> <secret> <region> <bucket> <s3_source> <realfs_destination>" + "\r\n" +
                    "java -jar kots3.jar UPLOAD <key> <secret> <region> <bucket> <realfs_source> <s3_destination>")

            System.exit(1)
        }
        val key = args[1]
        val secret = args[2]
        val bucketRegion = args[3]
        val bucketName = args[4]
        val amazonS3Client = buildClient(key, secret, bucketRegion)
        val actionType = ActionType.valueOf(args[0])

        when (actionType) {
            ActionType.DOWNLOAD -> {
                val sourceS3Path = args[5]
                val targetFullResourcePath = args[6]
                val content = S3Downloader(amazonS3Client).download(bucketName, sourceS3Path)
                File(targetFullResourcePath).outputStream().use { content }
            }
            ActionType.UPLOAD -> {
                val sourceFullResourcePath = args[5]
                val targetS3Resource = args[6]
                if (sourceFullResourcePath.contains("*")) {
                    val split = sourceFullResourcePath.split("/")
                    val mask = split[split.size - 1]
                    for (path in Files.newDirectoryStream(FileSystems.getDefault().getPath(sourceFullResourcePath.removeSuffix(mask)), mask)) {
                        S3Uploader(amazonS3Client).upload(bucketName, path.toFile(), path.fileName.toString())
                    }
                } else {
                    val sourceFile = File(sourceFullResourcePath)
                    S3Uploader(amazonS3Client).upload(bucketName, sourceFile, targetS3Resource)
                }
            }
            else -> {
                println("Unsupportable action argument: $actionType")
            }
        }
    }

    private fun buildClient(awsKey: String, awsSecret: String, awsRegion: String): AmazonS3 {
        val maxErrorRetry = 5
        val basicAWSCredentials = BasicAWSCredentials(awsKey, awsSecret)
        val clientConfiguration = ClientConfiguration()
        clientConfiguration.maxErrorRetry = maxErrorRetry
        clientConfiguration.retryPolicy = RetryPolicy(
                PredefinedRetryPolicies.DEFAULT_RETRY_CONDITION,
                PredefinedRetryPolicies.DEFAULT_BACKOFF_STRATEGY,
                maxErrorRetry,
                true)

        val amazonS3 = AmazonS3ClientBuilder.standard()
                .withClientConfiguration(clientConfiguration)
                .withCredentials(AWSStaticCredentialsProvider(basicAWSCredentials))
                .withRegion(awsRegion)
                .build()

        return amazonS3
    }

    enum class ActionType {
        DOWNLOAD, UPLOAD, WEBHOOK
    }

}


