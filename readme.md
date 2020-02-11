# Drop Token Service
This service implements the API necessary for playing games of Drop Token (see PDF) using [Spring Boot](https://spring.io/projects/spring-boot).

## How to run
In order to run this service, you need Java 8 and Gradle 5+ installed. To run, execute `gradle run` from the root directory. To create a distributable jar that only requires Java and not Gradle (or downloading of dependencies), `gradle bootJar` will produce a single file with everything in it in `build/libs/`. You can run the service using that file with `java -jar {path/to/service.jar}`. 

## Contents
The `main` function of this service is in DroptokenApplicaton. Running the `main` function will initialize Spring. While Spring does many things on startup, the important things to know are that it will start an HTTP server (on port 8080 unless overridden in `resources/application.properties`) and load any bean definitions from the main function's package (`com.times6.droptoken`), including `GameInstanceController`.

Requests are handled via the endpoints defined in `GameInstanceController`. You'll note that the class takes two constructor arguments. These are injected by Spring by matching any @Service, @Component, or @Bean classes or functions by their return type. For example, Spring will provide an instance of `IdGeneratorRandomUuid` as the `IdGenerator` argument because it's currently the only implementation of that interface registered with Spring via one of those annotations. In the future we may have multiple implementations, in which case the arguments will be provided via a class annotated with @Configuration or an XML file located in the resources folder.

The game's data model is contained in the `com.times6.droptoken.model` package. The representation of a game instance and the logic for updating the game are contained in `GameInstance` and `GameBoard`.

The `src/test` folder contains a small set of unit tests to verify that the game logic behaves as expected. You can also find some end-to-end testing utilities in `src/resources/static/`. To make use of them, start the service, and then open http://localhost:8080/tests.html in your browser. The functions defined in `tests.js` will be available via the JavaScript console in the global object `api`, which allows for scripting test scenarios. Example:
```javascript
// create a game and store its id
gameId = await api.createGame({ players: ['a', 'b'], columns: 4, rows: 4 }).then(r => r.text());
// make a move
api.fetchApiPost(`${gameId}/a`, { column: 1 }).then(r => r.json())
// list moves
api.fetchApi(`${gameId}/moves`).then(r => r.json())
// get current game state
api.fetchApi(`${gameId}`).then(r => r.json())
// quit the game
fetch(`/drop_token/${gameId}/b`, { method: 'DELETE' })
```
