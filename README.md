# BigQuery JDBC NullPointerException Reproduction

Note: this has been reported via the BigQuery support channels and is now part of ticket [155223254](https://issuetracker.google.com/issues/155223254).

This repo holds code to reproduce a NullPointerException in the 
BigQuery JDBC driver.

The NullPointerException is caused by a badly written regular 
expression in com.simba.googlebigquery.googlebigquery.dataengine.BQSQLExecutor, 
which causes it to misinterpret DQL statements as DML statements. This 
causes it to follow a code path that ultimately triggers a NullPointerException.

For example, the following query will trigger the bug mentioned above:

```sql
select id,
updated_at FROM example.bq_npe_repro
```

Note the newline before `updated_at`. The newline is important. Without the 
newline the query will succeed.

The supplied `fetch_driver.sh` script will download version 1.2.2.1004 of 
the JDBC driver from Google and unpack it in the `lib` directory so it can 
be referenced from this project's Maven POM.

The `src/main/java/com/mode/ryankennedy/BigQueryJdbcNpeReproductionMain.java` 
file contains the executable reproduction. After filling in a few 
connection configuration bits the `main()` method of that class can be 
run to trigger the NullPointerException. 
