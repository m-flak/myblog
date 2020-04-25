#!/usr/bin/env python3
from getpass import getpass
import json
import os
import re
import shutil
import sys

class ConfigFile:
    def __init__(self, *args, **kwargs):
        filename = kwargs.get('filename')

        with open(filename, 'r') as f:
            self.contents = f.read()

    def configure_file(self, variables):
        pass

class DotEnv(ConfigFile):
    def __init__(self):
        super().__init__(filename='./config/templates/.env')
        self.title = ''
        self.blurb = ''
        self.backend_url = ''

    def __str__(self):
        return self.contents.format(self.title, self.blurb, self.backend_url)

    def configure_file(self, variables):
        self.title = variables.get('title')
        self.blurb = variables.get('blurb')
        self.backend_url = variables.get('backend_url')

        return str(self)

class DatabaseXML(ConfigFile):
    def __init__(self):
        super().__init__(filename='./config/templates/database.xml')
        self.db_password = ''

    def __str__(self):
        return self.contents.format(self.db_password)

    def configure_file(self, variables):
        self.db_password = variables.get('db_password')

        return str(self)

class JKSProperties(ConfigFile):
    def __init__(self):
        super().__init__(filename='./config/templates/jks.properties')
        self.store_pass = ''
        self.key_pass = ''

    def __str__(self):
        return self.contents.format(self.store_pass, self.key_pass)

    def configure_file(self, variables):
        self.store_pass = variables.get('store_pass')
        self.key_pass = variables.get('key_pass')

        return str(self)

class PomXML(ConfigFile):
    def __init__(self):
        super().__init__(filename='./config/templates/pom.xml')
        self.store_pass = ''
        self.key_pass = ''

    def __str__(self):
        output_contents = re.sub(r"STOREPASSHERE", self.store_pass, self.contents)
        output_contents = re.sub(r"KEYPASSHERE", self.key_pass, output_contents)

        return output_contents

    def configure_file(self, variables):
        self.store_pass = variables.get('store_pass')
        self.key_pass = variables.get('key_pass')

        return str(self)

class WebProperties(ConfigFile):
    def __init__(self):
        super().__init__(filename='./config/templates/web.properties')
        self.app_root = ''
        self.frontend_origin = ''

    def __str__(self):
        return self.contents.format(self.app_root, self.frontend_origin)

    def configure_file(self, variables):
        self.app_root = variables.get('app_root')
        self.frontend_origin = variables.get('frontend_origin')

        return str(self)

class PackageJSON(ConfigFile):
    def __init__(self):
        super().__init__(filename='./package.json')
        self.json = json.loads(self.contents)

    def __str__(self):
        return json.dumps(self.json, indent=2)

    def configure_file(self, variables):
        homepage = variables.get('homepage')
        self.json['homepage'] = homepage

        return str(self)

class DatabaseUserSQL(ConfigFile):
    def __init__(self):
        super().__init__(filename='./config/templates/database_users.sql')
        self.db_password = ''

    def __str__(self):
        return self.contents.format(self.db_password)

    def configure_file(self, variables):
        self.db_password = variables.get('db_password')

        return str(self)

def is_branch_master():
    head_contents = None
    with open('./.git/HEAD', 'r') as f:
        head_contents = f.read()

    if re.search(r"master$", head_contents) is None:
        return False

    return True

def successful_exit():
    print("""Apply the ./config/database_users.sql file to your MySQL server.

    TO CREATE THE DATABASE SCHEMA:
    Run the backend server in migration mode via: --mode migrate

    TO CREATE BLOG USERS:
    Create initial blog users with: --mode shell
    """)

    return sys.exit(0)

if __name__ == '__main__':
    if is_branch_master():
        print("Please checkout to a new branch before running configure.")
        sys.exit(1)

    # .ENV VARIABLES
    blog_title = input('Please enter the name of the blog: ')
    blog_blurb = input('Please enter the blog\'s blurb: ')
    blog_backend_url = input('Please enter where the backend is located (either full URL or relative path): ')

    # DATABASE.XML, POM.XML, & JKS.PROPERTIES VARIABLES
    database_pw = getpass('Enter database user\'s password: ')
    keystore_pw = getpass('Enter keystore password: ')
    key_pw = getpass('Enter the key itself\'s password: ')

    # WEB.PROPERTIES VARIABLES
    backend_root = input('Please enter the backend\'s root (leave blank if \'/\'): ')
    backend_frontend_origin = input('Please enter the CORS origin for the frontend: ')

    # PACKAGE.JSON VARIABLES
    frontend_homepage = input('Please enter the subdirectory the frontend will be deployed in (leave blank if none): ')

    if not backend_root:
        backend_root = '/'

    env_file = DotEnv()
    database_xml = DatabaseXML()
    db_user_sql = DatabaseUserSQL()
    jks_props = JKSProperties()
    pom_xml = PomXML()
    web_props = WebProperties()
    package_json = PackageJSON() if frontend_homepage else None

    # WRITE GENERATED FILES

    with open('./config/.env', 'w') as f:
        f.write(env_file.configure_file({
            'title': blog_title,
            'blurb': blog_blurb,
            'backend_url': blog_backend_url,
        }))

    with open('./config/database.xml', 'w') as f:
        f.write(database_xml.configure_file({
            'db_password': database_pw,
        }))

    with open('./config/database_users.sql', 'w') as f:
        f.write(db_user_sql.configure_file({
            'db_password': database_pw,
        }))

    with open('./config/jks.properties', 'w') as f:
        f.write(jks_props.configure_file({
            'store_pass': keystore_pw,
            'key_pass': key_pw,
        }))

    with open('./config/pom.xml', 'w') as f:
        f.write(pom_xml.configure_file({
            'store_pass': keystore_pw,
            'key_pass': key_pw,
        }))

    with open('./config/web.properties', 'w') as f:
        f.write(web_props.configure_file({
            'app_root': backend_root,
            'frontend_origin': backend_frontend_origin,
        }))

    if package_json is not None:
        with open('./config/package.json', 'w') as f:
            f.write(package_json.configure_file({
                'homepage': frontend_homepage,
            }))

    print('\n\nConfiguration has been applied to configurable files in ./config/')

    # CONFIRM WHETHER TO PROCEED WITH COPY
    do_copy = None
    while True:
        do_copy = input('Do you wish to automatically copy these files now? [Y/N]: ')
        if 'Y' in do_copy.upper():
            break
        elif 'N' in do_copy.upper():
            successful_exit()

    # BACKUP NON-TRACKED FILES
    if os.path.exists('./.env'):
        shutil.move('./.env', './.env_backup')

    if os.path.exists('./server/src/main/resources/keystore.jks'):
        shutil.move(
            './server/src/main/resources/keystore.jks',
            './server/src/main/resources/keystore.jks.bak'
        )

    # MOVE CONFIGURED FILES TO WHERE THEY GO
    shutil.move(
        './config/database.xml',
        './server/src/main/resources/database.xml'
    )
    shutil.move(
        './config/jks.properties',
        './server/src/main/resources/jks.properties'
    )
    shutil.move(
        './config/web.properties',
        './server/src/main/resources/web.properties'
    )
    shutil.move(
        './config/pom.xml',
        './server/pom.xml'
    )

    if os.path.exists('./config/package.json'):
        shutil.move(
            './config/package.json',
            './package.json'
        )

    successful_exit()
