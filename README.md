# kots3
Tiny Amazon S3 client for command line work written in Kotlin.
I'm using this tool due deploy artifacts into the S3 storage.

    java -jar kots3.jar DOWNLOAD <key> <secret> <region> <bucket> <s3_source> <realfs_destination>
    java -jar kots3.jar UPLOAD <key> <secret> <region> <bucket> <realfs_source> <s3_destination>
