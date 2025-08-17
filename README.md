# ExpenseTracker (Java Swing + JDBC, No Maven)

A minimal desktop application demonstrating CRUD operations for personal expenses using plain Java 8+, Swing UI, JDBC, and MySQL. Built **without** Maven/Gradle – manual compilation only.

## Features
- Layered architecture (model, DAO, service, GUI, util)
- JDBC with prepared statements
- Validation rules & custom checked exceptions (`ValidationException`, `DAOException`)
- Simple Swing UI (main menu, add/edit dialog, table view)
- Category filter & running total bar in expense table
- Inheritance (`BaseEntity` -> `Expense`) & interfaces (`ExpenseDAO`, `ValidationService`)

## Directory Layout
```
ExpenseTracker/
├─ lib/                    # Put mysql-connector-j-<version>.jar here
├─ sql/
│  └─ schema.sql           # DB & table DDL
├─ src/
│  └─ com/expense/...
└─ README.md
```

## Prerequisites
| Tool | Version |
|------|---------|
| JDK | 8+ |
| MySQL Server | 5.7 – 8.x |
| MySQL Connector/J | 8.x JAR |

Download the MySQL connector and place the JAR inside `lib/` (do not rename it).

## Database Setup
Run the SQL script:
```
mysql -u root -p < sql/schema.sql
```
Or open it in MySQL Workbench and execute.

The credentials are configured in `src/com/expense/util/DB.java`:
```
USER = root
PWD  = S1h2w3e@gowda
URL  = jdbc:mysql://localhost:3306/expense_tracker
```
Adjust if needed.

## Compile
From project root (ExpenseTracker):

Linux/macOS:
```bash
mkdir -p build
# Include current directory in classpath in case lib is empty
javac -d build -cp ".:lib/*" $(find src -name "*.java")
```

Windows (PowerShell):
```powershell
md build
# Use FullName so javac receives proper paths
javac -d build -cp ".;lib/*" (Get-ChildItem -Recurse -Path src -Filter *.java | ForEach-Object { $_.FullName })

# If the above gives issues (long command lines), use an argument file:
Get-ChildItem -Recurse -Path src -Filter *.java | ForEach-Object { $_.FullName } | Out-File sources.txt -Encoding ASCII
javac -d build -cp ".;lib/*" @sources.txt
```

## Run
Linux/macOS:
```bash
java -cp "build:lib/*" com.expense.Main
```
Windows:
```powershell
java -cp "build;lib/*" com.expense.Main
```

### Quick DB Connectivity Test (CLI)
After compiling you can test the database without opening the GUI:
```powershell
java -cp "build;lib/*" com.expense.util.DBTest
```
Or inside the GUI click "Test DB Connection".

## Using the App
1. Launch main window.
2. Click **Add Expense** to create a record.
3. Click **View Expenses** to open the table window.
   - Refresh reloads from DB.
   - Filter choose a category or "All Categories".
   - Total bar (bottom) shows sum of currently displayed rows.
   - Add New opens the add dialog.
   - Edit Selected modifies the chosen row.
   - Delete Selected removes it after confirmation.
4. Close windows to exit (main window closes app).

## Validation Rules
| Field | Rule |
|-------|------|
| Amount | >0, <=1,000,000, scale <=2 |
| Description | non-blank, <=120 chars |
| Category | one of Food, Transport, Bills, Entertainment, Others |
| Date | not null, not future |

Errors pop up as dialog messages.

## Exception Strategy
| Exception | Purpose |
|-----------|---------|
| ValidationException | Bad user input (service layer) |
| DAOException | SQL / JDBC issues (DAO layer) |

## Troubleshooting
- `ClassNotFoundException: com.mysql.cj.jdbc.Driver`: Ensure connector JAR is in `lib/` and classpath uses `lib/*`.
- `Access denied for user`: Verify username/password in `DB.java` and MySQL user permissions.
- Time zone warning: Append `?serverTimezone=UTC` to the JDBC URL if needed.
- Empty table: Add expenses first; refresh.

## Create Runnable JAR (Optional)
After compiling, you can package classes:
```bash
jar cfe ExpenseTracker.jar com.expense.Main -C build .
```
Run it (adjust separator on Windows):
```bash
java -cp "ExpenseTracker.jar:lib/*" com.expense.Main  # Linux/macOS
java -cp "ExpenseTracker.jar;lib/*" com.expense.Main  # Windows
```

## Next Steps / Enhancements
1. Budgets table & progress bar.
2. Recurring transaction predictor.
3. User authentication & multi-user data.
4. Export reports (CSV/PDF via Apache PDFBox).
5. Charts (e.g., JFreeChart) – place JAR in `lib/` and integrate.

## License
Educational sample – adapt freely.
