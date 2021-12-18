## Share files
Share files (Upload/Download) over HTTP

![Screenshot](https://github.com/alagrede/share-files-http/blob/master/docs/screenshot.png)


## Download
![Stable version](https://img.shields.io/badge/version-1.0.0-blue)

Executable: <a href="https://github.com/alagrede/share-files-http/releases/latest/download/share-upload.jar">[share-upload.jar]</a>

### Build
```
./mvnw clean package
```

### Run
```
java -Xmx6G -DuploadPath=/Users/alagrede/Desktop/upload -jar target/share-upload.jar
```

### Use
`http://localhost:8080`