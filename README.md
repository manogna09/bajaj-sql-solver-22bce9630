# bajaj-sql-solver-22bce9630

# Bajaj Finserv Health | JAVA

This Spring Boot app:
- Registers with the Bajaj API on startup (using name, regNo, email).
- Receives a webhook URL and JWT access token.
- Submits the SQL query for **Question 2** (since regNo ends with 30 - even).
- Uses `RestTemplate` and sends the solution automatically (no controllers).

## Note
The app worked for me on the **first run** with a fresh token.  
On subsequent runs with the same token/webhook, the server responded with `401 Unauthorized`, which indicates the token has expired or is single-use.  
The logic and SQL query are correct — re-run the app fresh to generate a new token.
