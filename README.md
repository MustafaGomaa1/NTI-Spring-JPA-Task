# JPA Shop Application

## Run

Install Java 17+, Maven, and MySQL, then create the `shop` database. Configure the database using environment variables:

```powershell
$env:DB_URL = "jdbc:mysql://localhost:3306/shop?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "your-password"
mvn exec:java
```

The app starts an interactive console menu for registering customers, managing products, and placing and processing orders. Enter `0` to exit.

The default Hibernate schema mode is `update`, which preserves existing data. Override it only when you intentionally want a different schema policy:

```powershell
$env:HIBERNATE_DDL_AUTO = "validate"
```

Java system properties (`-Ddb.url`, `-Ddb.username`, `-Ddb.password`, and `-Dhibernate.ddl-auto`) can be used instead of the corresponding environment variables.
