- ever wanted to keep all of your credentials locally so that you can retrieve them when you need locally? this app uses a file locally on your machine and write your credentials there using your password for encrypt/decrypt your credential
- this app uses a passphrase for basic auth
- running jar: 
java -jar vault-0.1.jar --filelocation=<path/to/file> --credentialEncKey=<your encryption key> --basic.auth.basicAuthUsername=<basic auth username> --basic.auth.basisAuthPassword=<encrypted basic auth password> --basic.auth.basicAuthPassphrase=<your basic auth passphrase to decrypt/encrypt basic auth password>
- intellij vm options: 
-Dfilelocation=<path/to/file> -DcredentialEncKey=<your encryption key> -Dbasic.auth.basicAuthUsername=<basic auth username> -Dbasic.auth.basisAuthPassword=<encrypted basic auth password> -Dbasic.auth.basicAuthPassphrase=<your basic auth passphrase to decrypt/encrypt basic auth password>
