# BigQuery JDBC Regex Reproduction

This repo holds code to reproduce an issue in the BigQuery JDBC driver where
the driver gets "stuck" in regex processing consuming 100% of a single core.

The following query will trigger the issue:

```sql
-- ----------------------------------------------------------------------
SELECT 1
```

It appears that SQL-style comments with lots of hyphens in them take a _very_
time to process.  The time it takes for regex processing increases
superlinearly with the number of hyphens.

The supplied `fetch_driver.sh` script will download version 1.2.19.1023 of 
the JDBC driver from Google and unpack it in the `lib` directory so it can 
be referenced from this project's Maven POM.

The `src/main/java/com/mode/andynguyen/BigQueryJdbcRegexReproductionMain.java` 
file contains the executable reproduction. After filling in a few 
connection configuration bits the `main()` method of that class can be 
run. 
