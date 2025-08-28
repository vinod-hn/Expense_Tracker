# How to Run Expense Tracker on Another System

Follow these steps to set up and run the Expense Tracker project on any Windows laptop or desktop:

---
## 1. Prerequisites
- **Java JDK 8 or newer** (add to PATH)
- **MySQL Server** (running)
- **MySQL Connector/J** JAR (download from https://dev.mysql.com/downloads/connector/j/)

---
## 2. Copy Project Files
- Copy the entire `ExpenseTracker` folder (including `src/`, `lib/`, `sql/`, `build/`, `compile.bat`, `run.bat`, etc.) to the new system.

---
## 3. Place MySQL Connector/J JAR
- Download the JAR (e.g., `mysql-connector-j-8.3.0.jar`).
- Place it in the `lib/` folder (do not rename).

---
## 4. Set Up the Database
- Start MySQL server.
- Open a terminal or MySQL Workbench.
- Run the schema script:
  ```bash
  mysql -u root -p < sql/schema.sql
  ```
- Or just run the app once; it will auto-create the database and table if missing.

---
## 5. Update Credentials (if needed)
- If MySQL username/password is different, edit `src/com/expense/util/DB.java`:
  ```java
  private static final String USER = "your_mysql_user";
  private static final String PWD = "your_mysql_password";
  ```
- Save and recompile.

---
## 6. Compile the Project
Open a terminal in the `ExpenseTracker` folder and run:
```cmd
compile.bat
```

---
## 7. Run the Application
```cmd
run.bat
```

---
## 8. (Optional) Package as JAR
```cmd
package.bat
```
Then run:
```cmd
java -cp "ExpenseTracker.jar;lib/*" com.expense.Main
```

---
## 9. Troubleshooting
- **Driver not found:** Ensure the connector JAR is in `lib/` and classpath uses `lib/*`.
- **DB connection errors:** Check MySQL is running and credentials are correct.
- **Test DB:** Use the "Test DB Connection" button in the app or run:
  ```cmd
  java -cp "build;lib/*" com.expense.util.DBTest
  ```

---
## 10. Quick Checklist
- [ ] Java installed
- [ ] MySQL running
- [ ] Connector JAR in `lib/`
- [ ] Database created (or let app auto-create)
- [ ] Credentials updated if needed
- [ ] Compiled successfully
- [ ] App runs and connects to DB

---
**You’re ready!**
If you need a Linux/Mac version or have issues, ask for a platform-specific guide.
