# Sample Company Computer

## Details
This is an application that stores computer details and assign them to employees in a database.

The following data is stored:
* MAC address (required)
* computer name (required)
* IP address (required)
* employee abbreviation (optional)
* description (optional)

The used storage is a relational database. Currently, using H2 for simplicity.
When the application is running in production the database is stored in a file,
so that data can be persisted.
While testing, an in-memory database is configured for testing.

Service is running on localhost:8081, or localhost:8082 while testing.

## Data Storage
The database currently has two tables `computer` and `employee`,
with a many-to-one relation from `computer` to `employee`.
Meaning one employee can have multiple computer assigned.

## Endpoints
This service have the following endpoints:
* `GET /` This endpoint is used to get computer details.
  The endpoint by default returns all computers.
  But it also accepts the following parameters for further filtering of the results:
    * `name` the name of the computer
    * `mac` the MAC Address of the computer
    * `employee` the name abbreviation of the employee whom the computer is assigned to
* `POST /add` This endpoint is used to add computer details. The endpoint will return the added computer.
  The endpoint accepts the following parameters(parameters with `*` are required):
    * `name*` the name of the computer
    * `mac*` the MAC Address of the computer
    * `ip*` the IP Address of the computer
    * `employee` the name abbreviation of the employee whom the computer is assigned to
    * `description` the description of the computer
  
  If a computer with a similar `mac` is already existing, 
  the computer details will be updated.
  If a computer is added with an employee abbreviation that doesn't exist,
  a new employee entry will be created automatically.
* `POST /assign` This endpoint is used to assign a computer to an employee.
  The endpoint returns the updated computer details.
  The endpoint accepts the following parameters(parameters with `*` are required):
  * `mac*` the MAC Address of the computer
  * `employee*` the name abbreviation of the employee whom the computer will be assigned to
  
  If the `mac` doesn't exist an error will be thrown.

  If the `employee` does not exist in the database,
  a new `employee` entry will be automatically created.
* `DELETE /delete` This endpoint is used to remove a computer from an employee.
  The endpoint returns the number of deleted items (expected to be 1 if found and deleted).
  The endpoint accepts the following parameters(parameters with `*` are required):
  * `mac*` the MAC Address of the computer
  * `employee*` the name abbreviation of the employee whom the computer is assigned to

For Adding or Assigning a computer,
a notification will be sent if an employee has 3 or more computers assigned.

## Things that I skipped
* Proper error handling. e.g. returning understandable response codes and messages in case of errors.
* Integration tests. Currently,
  only some unit tests for the data layer behavior are implemented,
  however, some Integration tests at least from the level of the controller should be done.
  Or even better have scenario tests with tools like `cucumber` to test the whole application.
* I would build better data types for the `POST` endpoints and make them accept json data.

## More improvements for a production service
* Use a more scalable database (like a database server), an SQL or NoSQL(in this case the data layer needs to be adjusted) database
* Use actual passwords that are securely stored, depends on the environment, can be stored in Secret Manager in cloud and requested on demand for example,
  or can be ingested on deployment as environment variables.
* Add an auth endpoint before being able to access the services endpoints

## Development and Usage
To develop or build the service the following tools are required:
* Java 11
* Maven 3
* Docker

To run the test, you can use the IDE, or you can run `mvn test`

To build the service, run `mvn package` which will build the service `jar`,
and also build a `docker image` for it, the image will be named and tagged with `sample-company-computer:${version}`,
which is currently `sample-company-computer:0.0.1-SNAPSHOT`

To run the service, you can run it without persisting the database, meaning that the database will be deleted once the container is removed.
to do so run `docker run --rm -it -p 8081:8081 sample-company-computer:0.0.1-SNAPSHOT`.

Or to persist the database attach a docker volume to the container by running
`docker run --rm -it -v sample-company-computer-db:/tmp -p 8081:8081 sample-company-computer:0.0.1-SNAPSHOT`.
The volume will stay even after the container is removed, and will be attached again once you run the command again.

Most of the application is using default configurations for simplicity.