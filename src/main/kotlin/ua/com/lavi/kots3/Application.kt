package ua.com.lavi.kots3

import com.amazonaws.ClientConfiguration
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.retry.PredefinedRetryPolicies
import com.amazonaws.retry.RetryPolicy
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import java.io.File


fun main(args: Array<String>) {
    Kots3().start(args)
}

class Kots3 {

    fun start(args: Array<String>) {
        if (args.isEmpty() || args.size < 5) {
            println("""Program arguments:
            java -jar kots3.jar DOWNLOAD <key> <secret> <region> <bucket> <s3_source> <realfs_destination>
            java -jar kots3.jar UPLOAD <key> <secret> <region> <bucket> <realfs_source> <s3_destination>""")
            System.exit(1)
        }

        val key = args[1]
        val secret = args[2]
        val bucketRegion = args[3]
        val bucketName = args[4]
        val amazonS3Client = amazonS3(key, secret, bucketRegion)
        val actionType = ActionType.valueOf(args[0])
        val source = args[5]
        val target = args[6]

        when (actionType) {
            ActionType.DOWNLOAD -> {
                download(amazonS3Client, bucketName, source, target)
            }
            ActionType.UPLOAD -> {
                upload(amazonS3Client, bucketName, source, target)
            }
        }
    }

    fun upload(amazonS3Client: AmazonS3, bucketName: String, source: String, target: String) {
        val files = FilePathResolver.resolvePath(source)
        val s3Path = buildS3Path(target)
        for (sourceFile in files) {
            S3MultiPartUploader(amazonS3Client).upload(bucketName, sourceFile, s3Path + sourceFile.name)
        }
    }

    private fun download(amazonS3Client: AmazonS3, bucketName: String, source: String, target: String) {
        File(target).outputStream().use { S3Downloader(amazonS3Client).download(bucketName, source) }
    }

    private fun amazonS3(awsKey: String, awsSecret: String, awsRegion: String): AmazonS3 {
        val maxErrorRetry = 5
        val basicAWSCredentials = BasicAWSCredentials(awsKey, awsSecret)
        val clientConfiguration = ClientConfiguration()
        clientConfiguration.maxErrorRetry = maxErrorRetry
        clientConfiguration.retryPolicy = RetryPolicy(
                PredefinedRetryPolicies.DEFAULT_RETRY_CONDITION,
                PredefinedRetryPolicies.DEFAULT_BACKOFF_STRATEGY,
                maxErrorRetry,
                true)

        return AmazonS3ClientBuilder.standard()
                .withClientConfiguration(clientConfiguration)
                .withCredentials(AWSStaticCredentialsProvider(basicAWSCredentials))
                .withRegion(awsRegion)
                .build()
    }

    enum class ActionType {
        DOWNLOAD, UPLOAD
    }

    private fun buildS3Path(target: String): String {
        if (target == "/") {
            return ""
        }
        if (target.startsWith("/")) {
            return target.removePrefix("/") + "/"
        }
        return target
    }

}


