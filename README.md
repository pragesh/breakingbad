# Breaking Bad
A Java application to provide suggestions for stable code base revision for developers
by checking the stable revision in QA landscapes and incorporating feedback received from other developers.

# Running Instructions
1. Import this project in eclipse as a maven project
2. Select RunAs > Maven build
3. Select RunAs > Java Application

# Configurations Required
Before running this application, define constants in Constants.java
1. Username: Username for svn repository
2. Password: Password for svn repository
3. FTP_SERVER_URL_FOR_USER_HISTORY_READ: FTP server URL for the file where user feedback for revisions are to be read from.
4. FTP_SERVER_URL_FOR_USER_HISTORY_WRITE: FTP server URL for the file where user feedback for revisions are written
5. QA_URL_REVISIONS: QA URL for revision.properties file to fetch qa revisions.

# Requirements:
 1. Java version: 1.8
 2. Rest all dependencies are defined in pom.xml
 
 Note: Uncomment the code in pom.xml to download the revision of swt according to your operating system.
