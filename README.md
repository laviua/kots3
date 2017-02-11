# kots3
Tiny Amazon S3 client for command line work written in Kotlin.
I'm using this tool due deploy artifacts into the S3 storage.

    java -jar kots3.jar DOWNLOAD <key> <secret> <bucket> <region> s3Folder/file.zip /tmp/file.zip
    java -jar kots3.jar UPLOAD <key> <secret> <bucket> <region> /tmp/file.zip s3Folder/file.zip
