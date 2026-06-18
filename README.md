# Time Servlet

A simple Java web application built on Apache Tomcat that displays the current time in a specified timezone.

## Features

- Displays current time in UTC by default
- Supports `timezone` query parameter (e.g. `Europe/Berlin`, `UTC+2`)
- Validates timezone via a servlet filter — returns HTTP 400 for invalid values
- Remembers the last used timezone via cookies
- Uses Thymeleaf as a template engine

## Tech Stack

- Java 21
- Apache Tomcat 9
- Servlet API 4.0
- Thymeleaf 3.1
- Maven

## Usage

| URL | Result |
|-----|--------|
| `/time` | Current time in UTC (or last used timezone from cookie) |
| `/time?timezone=Europe/Berlin` | Current time in Berlin |
| `/time?timezone=UTC+2` | Current time in UTC+2 |
| `/time?timezone=INVALID` | 400 Bad Request — Invalid timezone |

## Project Structure

```
src/main/
├── java/org/example/
│   ├── TimeServlet.java
│   └── TimezoneValidateFilter.java
└── webapp/
    └── WEB-INF/
        └── templates/
            └── time.html
```
