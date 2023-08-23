# Cliente XMPP

## Overview

is a Maven project designed to facilitate real-time chat functionalities. It leverages the Ignite Realtime Smack library for XMPP-based communications 


## Basic Functionalities
Real-Time Chat: With the integration of Smack libraries, this project offers XMPP-based real-time chat capabilities.

Terminal-based Input/Output: With the inclusion of the JLine library, it suggests a command-line interface for chat interactions.


# Requirments
Java 8 or above.
Maven for building the project.

# Dependencies 
JUnit 3.8.1: Required for unit testing.

Smack Libraries (4.4.6): A set of libraries for real-time chat functionalities. These include:

smack-tcp: For TCP communication.
smack-im: Instant messaging functionalities.
smack-extensions: Extended functionalities for XMPP.
smack-experimental: Experimental features of Smack.
smack-java8: Java 8 support for Smack.

## Usage
Building the Project
- Navigate to the project directory.
Run the following command:
```
mvn clean install
```
Running the Client (on src>main>java>Main )
After building, follow the relevant instructions provided by the client's main class or entry point.


## Functionalities
### Client Initialization

Instantiates the client with user credentials and server details.
Registers the user if it's new; otherwise, logs them in.
Initializes the chat manager and message listener.

```java
public Client(String username, String password, boolean isNew, String domain, String ip, boolean globalShowLogs);
```
Connection Creation


Establishes the initial connection to the server.

```java
private AbstractXMPPConnection createConnection(String domain, String ip);
```
Account Registration

Registers a new user using provided credentials.

```java
public void registerAccount(boolean showLogs, boolean onSuccessLogin);
```
User Login

Logs in using the user's credentials.

```java
public void login(boolean showLogs);
```
Fetching Contacts

Retrieves and displays all contacts of the current user.

```java
public String printRosterEntries();
```
User Logout


```java
public void logout();
```
Account Deletion

Deletes the user's account.
java
```java
public void deleteAccount();
```
Adding Contacts

Adds a new contact to the user's roster.

```java
public void addContact(String userJID, String nickname, String[] groups);
```
Displaying Contact Details

Shows detailed information for a specific contact.

```java
public String showContactDetails(String userJID);
```
Removing Contacts

Removes a specific contact from the user's roster.

```java
public void removeContact(String userJID);
```