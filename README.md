# SQL Server Integrated Authentication Setup

This document provides instructions for setting up SQL Server integrated authentication with the ABM application.

## Issue: "This driver is not configured for integrated authentication"

If you encounter the error message "This driver is not configured for integrated authentication" when connecting to SQL Server, follow these steps to resolve the issue:

## Solution

### 1. Download the SQL Server JDBC Authentication DLL

Download the appropriate `sqljdbc_auth.dll` file from Microsoft. The file you need depends on your system architecture (x86 or x64).

You can find these files in the official Microsoft JDBC Driver for SQL Server download:
- [Microsoft JDBC Driver for SQL Server](https://learn.microsoft.com/en-us/sql/connect/jdbc/download-microsoft-jdbc-driver-for-sql-server)

### 2. Place the DLL in the Java Library Path

There are several ways to make the DLL available to your Java application:

#### Option A: Copy to JRE/bin directory
Copy the `sqljdbc_auth.dll` file to your JRE's bin directory:
```
C:\Program Files\Java\jre<version>\bin
```

#### Option B: Add to System PATH
Add the directory containing `sqljdbc_auth.dll` to your system PATH environment variable.

#### Option C: Specify Java Library Path at Runtime
When running the application, specify the directory containing the DLL using the Java system property:
```
java -Djava.library.path=<path_to_dll_directory> -jar ABM-1.0-SNAPSHOT.jar
```

### 3. Verify Connection

After setting up the DLL, restart the application and try connecting again. The integrated authentication should now work properly.

## Additional Information

- The application uses the JDBC URL format with `authentication=ActiveDirectoryIntegrated` for Windows integrated authentication.
- Make sure your Windows user account has the necessary permissions on the SQL Server.
- If you're still experiencing issues, check the SQL Server logs for more detailed error information.