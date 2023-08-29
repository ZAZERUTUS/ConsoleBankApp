#. /etc/environment && sdk use java 17.0.7-amzn && gradle build && java -classpath /tests/libs/postgresql-42.6.0.jar:/tests/build/libs/consoleApp-1.0-SNAPSHOT.jar org.example.Main
. /etc/environment && sdk use java 17.0.7-amzn && ./gradlew shadowJar && java -classpath /tests/libs/postgresql-42.6.0.jar:/tests/build/libs/consoleApp-1.0-SNAPSHOT-all.jar org.example.Main
