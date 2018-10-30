A training app for learning Spring Boot with Kotlin

=== Installation

1. Pull this git repository. Make sure you have maven and mysql installed

2. Start MySQL locally. Connect to it with a user who has write acces (ex: 'mysql -u root')

3. Create a new user and database. Give write access to the user on the database

Ex:

create database inventory;
create user 'springuser'@'localhost' identified by 'ThePassword';
grant all on inventory.* to 'springuser'@'localhost';


4. Make sure the application.properties (src/main/resources/application.properties) configuration file has the right access information.
Also verify that ddl-auto=update


It should look something like this

spring.jpa.hibernate.ddl-auto=update
spring.datasource.url=jdbc:mysql://localhost:3306/inventory
spring.datasource.username=springuser
spring.datasource.password=ThePassword

5. Run with maven

mvn spring-boot:run