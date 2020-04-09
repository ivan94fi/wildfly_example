# wildfly_example

## Description
Example JavaEE 8 project realized as training for a future real project. It simulates a simple REST application that handles bookings.

The domain entities are:
* User
* Booking
* Structure

## Technologies
This project employs the following technologies:
* Wildfly server as managed application runtime (JEE8 certified)
* MariaDB database as datasource (mapping with Hibernate ORM and JPA annotations)
* RESTful web service realized with JAX-RS specification (RESTEasy)
* role-based authorization through javax.annotation.security.RolesAllowed
* DAO tests with JUnit, AssertJ and Mockito mocking framework (unit tests)
* endpoint tests with REST-assured (integration tests)

## Installation/Running
This project requires the following services to run:
* a running instance of a Wildfly server, where the application must be deployed
* a running instance of a MariaDB server (can be provided with a docker image, see following sections)

After the application is deployed on the server, it is available at `localhost:8080/swam-example/rest` and it exposes the following endpoints:
* `/users`
* `/bookings`
* `/structures`

The endpoints accept HTTP `GET`, `POST`, `PUT` and `DELETE` methods. See `rest.endpoint.UserEndpointTest` for examples.

## Authorization
The "get all" endpoints, that is `/users`, `/bookings` and `/structures` contacted with a `GET` requests, require a basic authorization in the form of `username` and `password` (the password is actually ignored). All the other endpoints only require the header `Authorization: Basic` to be present in the request.

More specifically, the "get all" endpoints require that the user contacting them is a registered user with `ADMIN` role.

As a shortcut an additional unprotected endpoint is exposed, `/test/populate`, which creates some entities and in particular an admin user with username `test_user1` It is then possible to use this username to access the protected endpoints.

As an alternative, the post user endpoint can be invoked to create an admin:
```bash
curl --location --request POST 'http://localhost:8080/swam-example/rest/users/' \
--header 'Authorization: Basic' \
--header 'Content-Type: application/json' \
--data-raw '{"roles":["BASIC","ADMIN"],"username":"admin"}'
```

Finally, it is also possible to directly create an appropriate admin user with three SQL queries issued to the database server:
```sql
select next value for hibernate_sequence; -- returns the <id> to put in the following insert statements
insert into User (id, username) values (<id>, 'admin');
insert into User_roles (User_id, roles) values (<id>, 'ADMIN'), (<id>, 'BASIC');
```

## Database server
As already stated, the application expects a MariaDB 10.3 server available on `tcp://localhost:3306`. For convenience, the simple bash script `start_container.sh` is provided, which instantiate a docker container with a mariadb image, exposing the correct port. The script requires docker to be installed on the system.

The database name is `MariaDB`.
The database username and password are respectively `root` and `pass`.

To access the server with a command line interface the following command can be issued:
```bash
docker exec -it mariadb mysql MariaDB -u root -p
```

## Executing tests
Two kinds of tests are implemented:
* *unit tests*: the BaseDAO class is tested with unit tests in isolation, by mocking the entity manager injection with Mockito
* *integration tests*: the UserEndpoint class is tested by deploying the application in the container and then verifying the behevior by directly invokating the REST endpoints

The first ones can be executed without the container, while the second ones must be executed while the application endpoints are available.

The command to execute tests is simply:
```bash
mvn clean verify
```
