CREATE DATABASE myblogdb;
CREATE USER 'myblog_user'@'localhost' IDENTIFIED WITH mysql_native_password BY '{0}';
GRANT ALL ON myblogdb.* TO 'myblog_user'@'localhost';

CREATE DATABASE myblogdb_test;
CREATE USER 'myblog_test_user'@'localhost' IDENTIFIED WITH mysql_native_password BY 'pleasechangeme';
GRANT ALL ON myblogdb_test.* TO 'myblog_test_user'@'localhost';
