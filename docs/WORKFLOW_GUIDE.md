# Expense Tracker – End‑to‑End Workflow Guide (Easy Mode)

> Goal: Help you (or a teammate) understand how the whole app works – from launch to database – using plain language, diagrams, image placeholders, and an FAQ. Replace the image placeholders with real screenshots when ready.

---
## 1. Big Picture (Mental Model)
```
User Clicks UI  →  Swing Windows  →  Service (Validation)  →  DAO (SQL)  →  JDBC Connection  →  MySQL
                                ↑                                   ↓
                           ValidationException                DAOException
```
- **GUI**: Collects input & shows results.
- **Service**: Checks data rules; rejects bad input early.
- **DAO**: Runs SQL safely (PreparedStatement).
- **DB util (`DB`)**: Gives connections + auto-creates schema if missing.

---
## 2. Main Screens (Add your screenshots)
| Purpose | Placeholder (add file in `docs/images/`) | Notes |
|---------|-------------------------------------------|-------|
| Main Menu | `![Main Menu](images/main_menu.png)` | Buttons to start actions. |
| Add Expense Dialog | `![Add Expense](images/add_expense.png)` | Form fields + Save/Cancel. |
| Expenses Table | `![Expense Table](images/expense_table.png)` | Shows rows + total + filter. |
| DB Test Success Dialog | `![DB Test](images/db_test.png)` | Confirms connectivity & timing. |

Create folder `docs/images/` and save PNG screenshots using those names. Git will track them.

---
## 3. Typical User Flow
| Step | What User Does | What Code Does | Result |
|------|----------------|----------------|--------|
| 1 | Starts app | `Main` → `MainFrame` | Main menu visible |
| 2 | Clicks *Add Expense* | Opens `ExpenseForm` | Dialog shown |
| 3 | Fills fields & Save | Build `Expense` object | Validation step |
| 4 | Validation OK | Service calls DAO `insert` | Row persisted |
| 5 | Views *View Expenses* | `ExpenseTable` loads list | Table filled |
| 6 | Filters category | In‑memory filter of loaded list | Table + total update |
| 7 | Edits row | Loads selected → `ExpenseForm` | Update SQL |
| 8 | Deletes row | DAO `delete` | Row removed |
| 9 | Tests DB | Calls `DB.ping()` | Dialog shows OK / error |

---
## 4. Data Validation (Inside `ExpenseService`)
Rule checklist before any DB write:
- Amount: not null, > 0, ≤ 1,000,000, ≤ 2 decimals.
- Description: not blank, ≤ 120 chars.
- Category: in allowed set (Food, Transport, Bills, Entertainment, Others).
- Date: not null & not in future.

If any rule fails → `ValidationException` → GUI shows error dialog (user can correct).

---
## 5. Database Auto‑Creation Logic
1. App tries connection to schema URL.
2. If MySQL says: `Unknown database 'expense_tracker'` → connect to base URL (no schema).
3. Run: `CREATE DATABASE IF NOT EXISTS expense_tracker;`
4. Connect to new DB; run `CREATE TABLE IF NOT EXISTS expenses (...);`
5. Retry original operation (transparent to GUI layer).

This means **first run can succeed even if you forgot to manually create the schema**.

---
## 6. Class Role Cheat Sheet
| Class | Role | Key Methods |
|-------|------|-------------|
| `Main` | Entry point | `main()` launches GUI |
| `MainFrame` | Hub window | Opens other windows, DB test |
| `ExpenseForm` | Create / edit dialog | Builds `Expense`, calls service |
| `ExpenseTable` | Lists expenses | Refresh, filter, compute total |
| `ExpenseService` | Validation + orchestration | `addExpense`, `listExpenses` |
| `ExpenseDAO` | Interface | CRUD contract |
| `ExpenseDAOImpl` | JDBC logic | `insert`, `findAll`, `update`, `delete` |
| `DB` | Connection + schema ensure | `get()`, `ping()` |
| `Expense` | Model entity | Fields + getters/setters |
| `ValidationException` | Input problems | Thrown by service |
| `DAOException` | SQL issues | Thrown by DAO |

---
## 7. Build & Run Timeline (Manual)
```
compile.bat  →  build/ (class files)
run.bat      →  Launch GUI using classpath build;lib/*
package.bat  →  ExpenseTracker.jar (runnable jar)
```
At runtime MySQL driver must be in `lib/`.

---
## 8. Inside a Single SAVE Operation
```
User presses Save
  ↓
ExpenseForm collects text fields
  ↓
Creates Expense object
  ↓
ExpenseService.validate(expense)
  ├─ if fail → ValidationException → dialog
  └─ if pass → dao.insert(expense)
                ↓
            DB.get() (may auto-create schema)
                ↓
            PreparedStatement executes INSERT
                ↓
            Generated key (id) set on object
                ↓
Return to form → dialog closes → table can refresh
```

---
## 9. Filtering + Total Logic
1. Table loads full list from DB once (on refresh).
2. Category filter combo chooses subset (or all).
3. Total label recomputes sum of shown rows each filter or data change.
4. No extra SQL for each filter → faster for small dataset.

---
## 10. Error Handling Strategy
| Layer | Error Type | What Happens |
|-------|------------|--------------|
| Service | ValidationException | User sees friendly message |
| DAO | SQLException wrapped in DAOException | User sees error dialog (technical, but short) |
| DB util | Connection failure | `ping()` string explains cause |

---
## 11. Adding Your Screenshots (Step‑by‑Step)
1. Run app, open each screen.
2. Take screenshot → save as PNG.
3. Place files: `docs/images/main_menu.png`, `add_expense.png`, `expense_table.png`, `db_test.png`.
4. Confirm links render in markdown preview.
5. Git add & commit.

---
## 12. Extending the App (Quick Ideas)
| Feature | Minimal Start |
|---------|---------------|
| CSV Export | Iterate `listExpenses()`, write lines `id,amount,...` via `FileWriter`. |
| Budget Alerts | Add `budgets` table, compare monthly sum vs limit. |
| Charts | Add JFreeChart JAR; query sums per category; pie chart panel. |
| Auth | Add `users` table; wrap queries with user_id. |
| Config File | Load DB creds from `db.properties` using `Properties`.

---
## 13. Converting This Guide to PDF
If you have **Pandoc** installed:
```bash
pandoc docs/WORKFLOW_GUIDE.md -o docs/WORKFLOW_GUIDE.pdf
```
(Or use any online Markdown → PDF converter.)

---
## 14. FAQ (Questions & Answers)
**Q1. App starts but adding expense fails with DB error.**  
Check MySQL service is running and connector JAR is in `lib/`.

**Q2. Where do I change categories?**  
Edit the static `CATEGORIES` set in `ExpenseService`.

**Q3. Why is there no framework (like Spring)?**  
Educational clarity—teaches raw JDBC & Swing fundamentals.

**Q4. Can I move to Maven later?**  
Yes: create `pom.xml`, list dependencies (MySQL driver), move `src` into standard layout, then delete batch scripts.

**Q5. How to hide password?**  
Create `db.properties` (not tracked) and load via `Properties` before first connection.

**Q6. Does it support multiple users now?**  
No. You’d add a `user_id` column and filter queries per login context.

**Q7. How do I reset the table data?**  
Run `TRUNCATE TABLE expenses;` in MySQL or delete DB entirely; auto-create will rebuild.

**Q8. Why not filter with SQL each time?**  
For small datasets in memory is simpler & snappier. For large data add `WHERE category=?` queries.

**Q9. Getting timezone warnings.**  
Append `&serverTimezone=UTC` to the JDBC URL parameters.

**Q10. Insert is slow on large imports.**  
Switch to batch mode: reuse PreparedStatement and call `addBatch()`.

**Q11. How to add search by description?**  
Add method in DAO: `SELECT ... WHERE description LIKE ?` and UI text field to trigger it.

**Q12. Why we use PreparedStatement?**  
Prevents SQL injection and handles proper type conversion.

**Q13. Can I log SQL errors somewhere?**  
Add a simple logging wrapper or use `java.util.logging` before showing dialogs.

**Q14. Future date accidentally accepted?**  
Check system clock; validation rejects `expenseDate.isAfter(today)`.

**Q15. How to unit test service layer?**  
Mock DAO (create stub implementing `ExpenseDAO`) and call `validate` / `addExpense` with edge cases.

---
## 15. One‑Page Recap (Print This)
1. Build: `compile.bat`
2. Run: `run.bat`
3. Add expense → validation → insert.
4. View expenses → filter & total.
5. Auto DB creation on first run.
6. Errors: Validation vs DAO.
7. Extend: CSV / charts / auth.

---
**End of Guide.** Replace image placeholders when you capture screenshots.
