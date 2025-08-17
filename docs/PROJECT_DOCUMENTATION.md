# ExpenseTracker – Comprehensive Project Documentation

## 1. Overview
ExpenseTracker is a Java Swing + JDBC desktop application for recording and managing personal expenses. It demonstrates a clean layered architecture without using build tools like Maven or Gradle (manual compilation). The app connects to a MySQL database, applies validation rules, and offers a simple GUI for CRUD operations with category filtering and real-time totals.

## 2. Technology Stack
| Layer | Technology |
|-------|-----------|
| Language | Java 8+ (tested on 21 LTS) |
| UI | Swing / AWT |
| Persistence | JDBC (MySQL) |
| Database | MySQL (XAMPP compatible) |
| External JAR | MySQL Connector/J (user supplies) |

## 3. Architecture
```
User → GUI → Service (validation) → DAO (SQL) → DB (MySQL)
                 ↑ Exceptions (Validation/DAO) ↓
```
- **Model**: Data objects (`Expense` extends `BaseEntity`).
- **DAO**: `ExpenseDAO` interface + `ExpenseDAOImpl` (prepared statements, try-with-resources).
- **Service**: Business validation + DAO delegation (`ExpenseService`).
- **GUI**: Windows (`MainFrame`, `ExpenseForm`, `ExpenseTable`) built atop `BaseFrame` helpers.
- **Util**: DB connectivity factory + auto schema creation + diagnostics (`DB`).
- **Exception**: Custom checked exceptions (`ValidationException`, `DAOException`).

## 4. Key Features
- CRUD on `expenses` table.
- Input validation (amount, description, category whitelist, non-future date).
- Custom exception types with user-friendly dialogs.
- Automatic database + table creation if missing.
- Category filter and dynamic total bar in listing.
- Command-line and GUI database connectivity tests.
- Manual build scripts (`compile.bat`, `run.bat`, `package.bat`).

## 5. Directory Structure
```
ExpenseTracker/
├─ lib/                      # Place mysql-connector-j-<ver>.jar
├─ sql/
│  └─ schema.sql             # Provided DDL (optional now; auto-create exists)
├─ src/
│  └─ com/expense/
│      ├─ model/             # BaseEntity.java, Expense.java
│      ├─ dao/               # ExpenseDAO.java, ExpenseDAOImpl.java
│      ├─ service/           # ValidationService.java, ExpenseService.java
│      ├─ gui/               # BaseFrame.java, MainFrame.java, ExpenseForm.java, ExpenseTable.java
│      ├─ util/              # DB.java, DBTest.java
│      ├─ exception/         # ValidationException.java, DAOException.java
│      └─ Main.java          # Entry point
├─ build/                    # Generated classes (after compile)
├─ compile.bat               # Windows build script
├─ run.bat                   # Run GUI
├─ package.bat               # Create runnable JAR
└─ docs/                     # Documentation
```

## 6. Database Schema
Table: `expenses`
```
CREATE TABLE IF NOT EXISTS expenses (
    id INT AUTO_INCREMENT PRIMARY KEY,
    amount DECIMAL(10,2)      NOT NULL CHECK (amount>0),
    description VARCHAR(120)  NOT NULL,
    category    VARCHAR(40)   NOT NULL,
    expense_date DATE         NOT NULL,
    created_at   TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);
```
Auto-creation: `DB.ensureSchema()` will create database `expense_tracker` and this table if absent.

## 7. Validation Rules
| Field | Constraint |
|-------|-----------|
| amount | >0, ≤1,000,000, scale ≤2 |
| description | non-blank, ≤120 chars |
| category | one of Food, Transport, Bills, Entertainment, Others |
| expenseDate | not null, not in future |

Violations raise `ValidationException`; DAO failures wrap as `DAOException`.

## 8. Important Classes
| Class | Responsibility |
|-------|----------------|
| `BaseEntity` | Common id / createdAt fields |
| `Expense` | Domain model for a single expense |
| `ExpenseDAO` | CRUD contract |
| `ExpenseDAOImpl` | JDBC implementation |
| `ValidationService` | Validation contract |
| `ExpenseService` | Business logic + validation + DAO bridging |
| `DB` | Driver loading, schema ensure, connections, diagnostics |
| `DBTest` | CLI-level connectivity tester |
| `MainFrame` | Root navigation & DB test button |
| `ExpenseForm` | Add / Edit dialog |
| `ExpenseTable` | Data grid with filtering, totals, CRUD actions |
| `compile.bat` | Batch script for javac build |

## 9. Build & Run
### 9.1 Prerequisites
Install JDK and MySQL (or XAMPP MySQL). Download MySQL Connector/J and copy its JAR into `lib/`.

### 9.2 Compile
Windows CMD:
```
cd ExpenseTracker
compile.bat
```
Linux/macOS:
```
cd ExpenseTracker
mkdir -p build
javac -d build -cp ".:lib/*" $(find src -name "*.java")
```

### 9.3 Run GUI
```
java -cp "build;lib/*" com.expense.Main   # Windows
java -cp "build:lib/*" com.expense.Main   # Linux/macOS
```

### 9.4 Package JAR
```
package.bat
java -cp "ExpenseTracker.jar;lib/*" com.expense.Main   # Windows
java -cp "ExpenseTracker.jar:lib/*" com.expense.Main   # Linux/macOS
```

## 10. Connectivity Diagnostics
CLI test:
```
java -cp "build;lib/*" com.expense.util.DBTest
```
GUI test: Click "Test DB Connection" in the main window.
Output forms:
- `DB OK (...) latency X ms` – success.
- `DB ERROR: ...` – details of exception, including suppressed attempts.

## 11. Error Handling Strategy
| Layer | Exception | Handling |
|-------|-----------|----------|
| Validation | `ValidationException` | Caught in forms → dialog |
| DAO / SQL | `DAOException` | Propagated to GUI → dialog |
| Connectivity | Raw message via `DB.ping()` | Shown to user |

## 12. Security Notes
- Root credentials are hard-coded (development only). For production: externalize to env vars or config file; use a dedicated limited MySQL user.
- `allowPublicKeyRetrieval=true` is convenient for local dev; remove or secure in production.

## 13. Known Limitations
- No pagination or large dataset optimization.
- No authentication / multi-user separation.
- No unit or integration test suite included.
- Password stored in plain text.
- Minimal logging (printStackTrace).

## 14. Extension Ideas
| Feature | Description |
|---------|-------------|
| Budgets | Track budget vs spend with progress bar |
| Recurring Prediction | Analyze repeating expenses (pattern mining) |
| Auth & Users | Separate user data tables |
| Exports | CSV / PDF (PDFBox) |
| Charts | Category spending visually (e.g., JFreeChart) |
| Reports | Monthly summaries, trends |
| Config | External properties for DB credentials |
| Tests | JUnit tests for validation + DAO |

## 15. Maintenance Tasks
- Externalize credentials.
- Add logging (SLF4J + simple binding or JUL) for DAO ops.
- Implement unit tests before refactors.
- Consider using connection pooling (e.g., HikariCP) if scaling.

## 16. Quick Troubleshooting Matrix
| Symptom | Likely Cause | Fix |
|---------|--------------|-----|
| `No suitable driver` | Driver JAR missing | Place MySQL connector in `lib/` |
| `Communications link failure` | MySQL not running / firewall / wrong port | Start server, verify port 3306 |
| `Access denied` | Wrong credentials | Update `DB.java` or reset password |
| `Unknown database` | Schema not created | Auto-create now, or run `schema.sql` |
| Validation popup | Bad input | Correct form fields |
| Empty table | No records yet | Add expense, refresh |

## 17. Code Quality Notes
- Uses try-with-resources for all JDBC statements.
- Separation of concerns: validation not in DAO, GUI only orchestrates.
- Minimal side effects; DB auto-create isolated in `DB` utility.

## 18. License & Usage
Educational sample—freely adapt for learning or internal demos. Remove hard-coded secrets before sharing.

## 19. Quick Start TL;DR
```
# 1. Put driver in lib/
# 2. compile
cd ExpenseTracker
compile.bat
# 3. test connectivity
java -cp "build;lib/*" com.expense.util.DBTest
# 4. run
java -cp "build;lib/*" com.expense.Main
```

## 20. Support
For enhancements or questions (validation rules, additional features, packaging), extend code within the defined layer boundaries to preserve clarity.

---
End of Documentation.
