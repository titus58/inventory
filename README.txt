A training app for learning Spring Boot with Kotlin


Create a database

create database inventory;
create user 'springuser'@'localhost' identified by 'ThePassword';
grant all on inventory.* to 'springuser'@'localhost';

Paste the following into "src/main/resources/application.properties"

spring.jpa.hibernate.ddl-auto=create
spring.datasource.url=jdbc:mysql://localhost:3306/inventory
spring.datasource.username=springuser
spring.datasource.password=ThePassword