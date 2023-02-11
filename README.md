# AuctionBoard

## Build and run locally
- Default database file auctiondb is created automatically in the project's root. You can specify a path and credentials in application.properties


- Build with gradle in project's root
```
./gradlew build
```
- Run output jar from project's root
```
java -jar ./build/libs/AuctionBoard-0.0.1-SNAPSHOT.jar
```

## Project structure
- Sources located at
```
./src/main/java/com/example/AuctionBoard
```
- Tests located at
```
./src/test/java/com/example/AuctionBoard
```
- Api documentation can be found at
```
http://localhost:${PORT:8080}/swagger-ui/
```
