# kots3
Tiny Amazon S3 client for command line work written in Kotlin.
It can deploy artifacts into the S3 storage and no needs to install python or something else withing java container.

    java -jar kots3.jar DOWNLOAD <key> <secret> <region> <bucket> <s3_source> <realfs_destination>
    java -jar kots3.jar UPLOAD <key> <secret> <region> <bucket> <realfs_source> <s3_destination>

Gitlab CI example:

    image: java:8-jdk
    
    stages:
      - build
    
    before_script:
      - export GRADLE_USER_HOME=`pwd`/.gradle
    build:
      stage: build
      script:
        - ./gradlew clean build
        - curl -O -L https://github.com/laviua/kots3/releases/download/0.2/kots3-0.2.jar
        - java -jar kots3-0.2.jar UPLOAD $AWS_ACCESS_KEY_ID $AWS_SECRET_ACCESS_KEY $AWS_REGION $AWS_S3_BUCKET_NAME "$CI_PROJECT_DIR/**/*.jar" /
      only:
        - master