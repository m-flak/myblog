# myBLOG
> A full-stack web application that shall be my personal blog

### Configuration

##### Build
Before building the backend & frontend, initial configuration must be performed. This can be accomplished via:
```
git branch deploy
git checkout deploy
python configure.py
```
Follow the instructions to configure both the frontend & backend.

Afterwards, apply the generated **config/database_users.sql** file to the database on your server. This can be done like this:
```
mysql -u root -p < database_users.sql
```
You'll populate the database with tables and a user account using the server executable. We'll get to that later in [Backend Setup](#backend-setup).

##### Web Server & Host Configuration

Both an example systemd unit file and nginx configuration can be found in the **srvconfig** directory.

### Frontend Setup

##### Building

For general questions about building the frontend, [see this file](README_CAR.md).

##### Deployment

Refer to the sample nginx configuration for any questions. The frontend is deployed like any React application.

### Backend Setup

##### Building

Building the backend requires **maven**. _You'll probably need to remove this from **pom.xml**_:
```xml
        <plugin>
          <artifactId>maven-surefire-plugin</artifactId>
          <version>3.0.0-M3</version>
          <configuration>
              <argLine>--enable-preview</argLine>
          </configuration>
        </plugin>
 ```
_Remove that in order to disable the tests_, because the tests require a functional MySQL database & account. The testing database and account is included in the **database_users.sql** file.

Run the ``mvn package`` command from within the **server** directory.

##### Post-Build

Before the server can get to work, the database must be filled with tables, and a user account needs to be created.

**For the tables:** ``java --enable-preview -jar server-1.0-shaded.jar --mode migrate``

**For the user:** ``java --enable-preview -jar server-1.0-shaded.jar --mode shell``
