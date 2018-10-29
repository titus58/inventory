Create a database

create database inventory;
create user 'springuser'@'localhost' identified by 'ThePassword';
grant all on inventory.* to 'springuser'@'localhost';