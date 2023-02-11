# AuctionBoard

## Build and run locally
- You need to manually create a database file. Default file should be auctiondb.db in the project's root. You can also specify a different path to it in application.properties:
```
spring.datasource.url=jdbc:h2:file:./auctiondb 
```
- Build with gradle in project's root:
```
./gradlew build
```
- Run output jar from project's root
```
java -jar ./build/libs/AuctionBoard-0.0.1-SNAPSHOT.jar
```

## Project structure
- Sources located in ./src/main/java/com/example/AuctionBoard
- Tests located in ./src/test/java/com/example/AuctionBoard
