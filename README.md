# myBLOG
> A full-stack web application that shall be my personal blog

**STILL UNDER CONSTRUCTION**
\_√\_ Backend
\_\_\_ Frontend

### Frontend Setup

##### Building

For general questions about building the frontend, [see this file](README_CAR.md).

### Server Setup

The server must be configured. Below are the steps necessary to get it up and running.

Eventually, there will be a configuration script to do all of this.

##### Database Configuration

The values of the below SQL commands should be identical to whatever is in _**server/src/main/resources/database.xml**_

**Repeat this step for the test database, using whatever's in:** _**server/src/test/resources/database.xml**_

```SQL
CREATE DATABASE myblogdb;
CREATE USER 'myblog_user'@'localhost' IDENTIFIED WITH mysql_native_password BY '<PASSWORD_HERE>';
GRANT ALL ON myblogdb.* TO 'myblog_user'@'localhost';
```

##### Keystore Configuration

**Next,** you will need to change the keystore parameters found in _**server/pom.xml**_ and _**server/src/main/resources/jks.properties**_ respectively.

##### Application Root

If you're running a reverse proxy to the server (which you should), you might need to modify _**server/src/main/resources/web.properties**_. If you're confused by any of the terminology within this subsection, you're clearly not using _nginx_. =)

Here's how you determine whether or not you will need to modify that file:
- <u>A reverse proxy directive in /</u> → _No change required._
- <u>A reverse proxy directive in some subdirectory, say /foobar/</u> → _Change required!_

##### CORS Origin

Set the **frontend_origin** variable in _**server/src/main/resources/web.properties**_ to the origin where the frontend will be running from.
