# Expense Tracker – Detailed Integration & Exam Preparation Guide

> Use this document to quickly demonstrate understanding of every mandated concept (JDBC, Swing UI, Packages, Interfaces, Inheritance, Exception Handling, Validation) and to prepare for viva / exam questions. It supplements `PROJECT_DOCUMENTATION.md` and `WORKFLOW_GUIDE.md`.

---
## 1. Assignment Requirements → Exact Implementations
| Requirement | Where Implemented | Evidence / Notes |
|-------------|-------------------|------------------|
| JDBC Application | `com.expense.dao.*`, `com.expense.util.DB` | Uses MySQL Connector/J, prepared statements, auto schema creation. |
| Swing User Interface | `com.expense.gui.*` (`MainFrame`, `ExpenseForm`, `ExpenseTable`, `BaseFrame`) | Multiple windows/dialogs, actions triggered via listeners. |
| Validations (all must) | `ExpenseService.validate()` | Enforces amount, description, category, date constraints before DAO call. |
| Packages | `model`, `dao`, `service`, `gui`, `util`, `exception` | Clean separation of concerns. |
| Interfaces | `ExpenseDAO`, `ValidationService` | Abstractions for DAO operations & validation contract. |
| Inheritance | `BaseEntity` → `Expense`; `BaseFrame` → concrete GUI frames | Shared fields & helper methods. |
| Exception Handling Concepts | Custom checked: `ValidationException`, `DAOException`; try-with-resources in DAO | Layer-specific error propagation & user-friendly dialogs. |
| Team Collaboration Friendly | Modular structure, small focused classes, scripts | Easy parallel work: GUI vs DAO vs Service. |
| Additional Enhancements | Auto DB creation, category filter, totals, DB ping | Value-add for reliability & diagnostics. |

---
## 2. High-Level Architecture
```
[User]
  ↓ (events)
[ GUI Layer ]  Swing components (frames, dialogs, table)
  ↓ builds domain objects / gathers inputs
[ Service Layer ]  Validation + orchestration
  ↓ only if valid
[ DAO Layer ]  CRUD with prepared statements
  ↓
[ DB Utility ]  Connection factory, auto schema, diagnostics
  ↓
[ MySQL Database ]
```
Error Flow:
- Validation failure → `ValidationException` → GUI shows message.
- SQL failure → `DAOException` → GUI shows error dialog (technical snippet).
- Unknown DB → auto create then retry.

---
## 3. Detailed Component Roles
### 3.1 GUI Layer
| Class | Responsibility | Key Methods / Actions |
|-------|----------------|-----------------------|
| `MainFrame` | Entry menu & navigation | `openAddForm()`, `openTable()`, `testDb()` |
| `ExpenseForm` | Add/Edit expense dialog | Builds `Expense`, calls service `addExpense` / `updateExpense` |
| `ExpenseTable` | Lists expenses, filtering, totals | `applyFilter()`, `updateTotal()`, CRUD toolbar actions |
| `BaseFrame` | Shared UI helpers | `showError()`, `showInfo()`, `centerOnScreen()` |

### 3.2 Service Layer
`ExpenseService` centralizes validation + delegates persistence to `ExpenseDAO`.

### 3.3 DAO Layer
`ExpenseDAOImpl` uses parameterized SQL for `INSERT`, `SELECT`, `UPDATE`, `DELETE` with try-with-resources for safe cleanup.

### 3.4 Utility
`DB` loads JDBC driver, sets timeout parameters, auto-creates database & table if missing, offers `ping()` for quick health check.

### 3.5 Model & Inheritance
`BaseEntity` provides `id`, `createdAt` with helper to convert from `Timestamp`. `Expense` extends it adding business fields.

### 3.6 Exceptions
- `ValidationException`: user input faults.
- `DAOException`: wraps low-level `SQLException`. GUI never directly manipulates these layers' internal error details.

---
## 4. Validation Logic in Depth
| Field | Checks | Rationale |
|-------|--------|-----------|
| amount | not null, > 0, scale ≤ 2, ≤ 1,000,000 | Prevent invalid or unrealistic monetary values & precision drift |
| description | not blank, length ≤ 120 | UI readability + database column bound |
| category | not blank, must be in fixed set | Controlled vocabulary for filtering & reporting |
| expenseDate | not null, not future | Aligns with "recorded actual spending" semantics |

Order of validation short-circuits on first failure → faster feedback.

---
## 5. Auto Schema Creation Sequence
1. Attempt connection to schema URL.
2. Catch SQLException: if error code 1049 (unknown DB) mark schema missing.
3. Connect to base URL (no DB) and execute `CREATE DATABASE IF NOT EXISTS expense_tracker`.
4. Connect to new DB; execute `CREATE TABLE IF NOT EXISTS expenses (...columns...)`.
5. Retry original connection/operation.

Advantages: reduces onboarding friction; Disadvantage: could conceal typos in intended DB name.

Mitigation: log (or print) a one-time message when auto creation occurs.

---
## 6. Data Flow Example – Add Expense
```
User enters form → Presses Save
   ↓
`ExpenseForm` constructs `Expense`
   ↓
`ExpenseService.addExpense()`
   ↓ validate() all rules
        ↳ if invalid throw ValidationException → GUI dialog
   ↓
`ExpenseDAO.insert()` builds PreparedStatement
   ↓
`DB.get()` (ensures schema) returns Connection
   ↓
Execute INSERT, retrieve generated key
   ↓
Success dialog; table can refresh
```

---
## 7. Filtering & Totals Implementation
- Full list retrieved via single SELECT (ordered by date desc).
- In-memory filter: subset by selected category (avoids extra round-trips for small data).
- Total recomputed using BigDecimal accumulation of currently displayed rows → avoids floating point error.

Scalability Consideration: For very large datasets switch to server-side filtering using WHERE clause & pagination.

---
## 8. PreparedStatement Patterns
| Operation | SQL | Risk Avoided |
|-----------|-----|--------------|
| INSERT | `INSERT INTO expenses(amount, description, category, expense_date) VALUES(?,?,?,?)` | Injection, numeric/Date formatting issues |
| UPDATE | `UPDATE expenses SET amount=?, description=?, category=?, expense_date=? WHERE id=?` | Same |
| DELETE | `DELETE FROM expenses WHERE id=?` | Injection |
| SELECT | `SELECT id, amount, description, category, expense_date, created_at FROM expenses ORDER BY expense_date DESC, id DESC` | Consistent ordering |

---
## 9. Threading & Swing Principles
- All UI creation via `SwingUtilities.invokeLater()` to run on EDT.
- Database operations are short; for heavy operations a background worker would be recommended (e.g., `SwingWorker`).
- Current scale acceptable for synchronous calls.

---
## 10. Extension Roadmap (If Asked in Viva)
| Idea | Core Changes | Difficulty (1–5) |
|------|--------------|------------------|
| CSV Export | Add service method to fetch all; write lines to file | 1 |
| PDF Report | Integrate PDF library (e.g., PDFBox) | 3 |
| Charts | Add JFreeChart JAR + aggregated queries | 3 |
| Authentication | Add `users` table & login dialog; FK in expenses | 4 |
| Monthly Budgets | Add `budgets` + summary query per month | 3 |
| External Config | Load props from `db.properties` | 1 |
| Logging | Add `java.util.logging` or SLF4J wrapper | 2 |
| Pagination | Modify SELECT with LIMIT/OFFSET & GUI controls | 2 |

---
## 11. Risks & Mitigations
| Risk | Impact | Mitigation |
|------|--------|------------|
| Hard-coded DB credentials | Security exposure | Externalize to file or env vars, Git ignore it |
| UI freeze on slow DB | Poor UX | Use background thread/SwingWorker for long ops |
| Silent auto DB creation of wrong name | Unexpected empty data | Log creation, consider a config flag to disable in prod |
| Manual dependency management | Version drift | Move to Maven if complexity grows |

---
## 12. Comprehensive Viva / Exam Question Bank
### Section A: Core Concepts
1. Explain how JDBC resources are safely released.  
**Answer:** Try-with-resources encloses `Connection`, `PreparedStatement`, `ResultSet` so `close()` runs automatically even on exceptions.
2. Why separate validation from DAO?  
**Answer:** Keeps persistence layer focused on storage concerns; validation becomes reusable and testable independently.
3. Difference between interface and abstract class usage here?  
**Answer:** Interface (`ExpenseDAO`) defines behavior contract; abstract class (`BaseEntity`) shares state & partial implementation.
4. Benefits of BigDecimal vs double for currency?  
**Answer:** Exact decimal arithmetic, avoids binary rounding errors inherent in floating point.
5. Where does polymorphism appear?  
**Answer:** Code references `ExpenseDAO` (interface) allowing alternate implementations (e.g., in-memory, mock, different DB).

### Section B: Validation & Business Logic
6. List all validation checks performed on an expense.  
**Answer:** Amount (non-null, >0, ≤1,000,000, scale ≤2), description (non-blank ≤120 chars), category (in whitelist), date (non-null, not future).
7. If a future date is entered, what happens?  
**Answer:** Service throws `ValidationException`; GUI shows error dialog; nothing hits DB.
8. How to add a new category?  
**Answer:** Add string to `CATEGORIES` set in `ExpenseService`, recompile.

### Section C: Database & SQL
9. How is SQL injection mitigated?  
**Answer:** Use of `PreparedStatement` with positional parameters; no string concatenation of user input.
10. Why is table ordering by date then id?  
**Answer:** Ensures newest expenses (recent dates, latest inserts) appear first predictably.
11. What would you change to support pagination?  
**Answer:** Add `LIMIT ? OFFSET ?` to SELECT; expose parameters from UI.
12. How does auto schema creation detect missing database?  
**Answer:** Checks SQLState error code 1049 or message containing "unknown database".

### Section D: Swing & UI
13. Why centralize dialog display logic in `BaseFrame`?  
**Answer:** Avoid code duplication; consistent look & future customization.
14. When should a `SwingWorker` be introduced?  
**Answer:** When DB operations become long-running to keep UI responsive.
15. How is total recalculated after filter?  
**Answer:** Iterate filtered list, sum `amount` with BigDecimal; update label.

### Section E: Exceptions & Error Handling
16. Checked vs unchecked choice rationale here?  
**Answer:** Checked exceptions force caller (GUI) to handle or propagate, making error flow explicit in educational context.
17. Why wrap SQLException instead of throwing it directly?  
**Answer:** Hide vendor-specific details; unify error contract for service/GUI layers.

### Section F: Design & Extensibility
18. Replace MySQL with PostgreSQL—what changes?  
**Answer:** JDBC driver dependency, URL format, maybe SQL dialect (minimal for this table), potentially sequence handling. Core design unchanged.
19. How would you test `ExpenseService` independently?  
**Answer:** Supply a mock/stub `ExpenseDAO` capturing inserts; assert validation paths.
20. What design principle guides not letting GUI call `DB` directly (except ping button)?  
**Answer:** Separation of concerns & single responsibility to keep UI logic isolated from persistence.

### Section G: Performance & Reliability
21. How to optimize mass imports?  
**Answer:** Batch operations (`addBatch()` / `executeBatch()`), single transaction (disable auto-commit during batch), prepared statement reuse.
22. Possible issue with catching broad exceptions in GUI?  
**Answer:** Might mask root cause; solution: log full stack trace, show concise message.

### Section H: Security & Configuration
23. Why externalize configuration?  
**Answer:** Avoid exposing credentials, ease environment portability, allows different dev/prod settings.
24. Minimal steps to externalize DB credentials?  
**Answer:** Create `db.properties`; read with `Properties`; fetch keys; fallback or error if missing.

### Section I: Advanced Scenarios
25. Add monthly aggregate chart—data query?  
**Answer:** `SELECT category, SUM(amount) FROM expenses WHERE expense_date BETWEEN ? AND ? GROUP BY category`.
26. How to ensure thread safety if multi-threaded features added?  
**Answer:** Avoid shared mutable state or synchronize on critical sections; use immutable objects for models; each DB call uses new connection.
27. How would you implement soft delete?  
**Answer:** Add `deleted BOOLEAN DEFAULT 0`; modify queries `WHERE deleted=0`; update delete to set flag instead of physical removal.

### Section J: Troubleshooting
28. Driver not found error—root cause and fix?  
**Answer:** Driver JAR missing from `lib`; add correct version; recompile/ rerun.
29. Unknown database error persists—why?  
**Answer:** Lack of permissions to create DB or wrong credentials; check MySQL user privileges.
30. Empty table after adding entry—why?  
**Answer:** Transaction not committed (if default autocommit disabled) or validation prevented insert; or filter hides category.

### Section K: Oral Quick Fire (Short Answers)
31. Why BigDecimal? → Exact money.  
32. Why interface? → Abstraction & testability.  
33. Why inheritance? → Shared fields/utilities.  
34. Why custom exceptions? → Clear error semantics.  
35. Why prepared statements? → Safety + performance.  
36. Why validation before DAO? → Fail fast.  
37. Why layered design? → Maintainability.  
38. Why no framework? → Educational transparency.  
39. Why auto schema? → Faster onboarding.  
40. Why possible move to Maven later? → Dependency management scalability.

---
## 13. Demonstration Script (5-Minute Viva)
1. Launch app → show main menu.  
2. Demonstrate Add Expense (enter valid + invalid to show validation).  
3. Open table → show filter & total changes.  
4. Edit & delete an entry.  
5. Click DB Test button; mention auto schema logic.  
6. Point to package structure in file explorer.  
7. Show DAO code with prepared statements.  
8. Summarize validation rules & exception separation.  
9. Outline extension plan (CSV export, charts).  
10. Conclude with why design aids teamwork.

---
## 14. Quick Memory Sheet
| Topic | 3 Keywords |
|-------|------------|
| Validation | Early, Centralized, Safe |
| DAO | Prepared, CRUD, Isolation |
| Service | Rules, Delegation, Shield |
| GUI | Events, Dialogs, Feedback |
| Exceptions | Meaningful, Layered, Propagate |
| DB Utility | Driver, AutoSchema, Ping |
| Inheritance | Reuse, BaseFields, Less Dup |
| Interfaces | Contract, Swap, Test |

---
## 15. Potential Improvements (Mention Briefly if Asked)
- Introduce logging abstraction.
- Unit test suite (mock DAO & integration test with test DB).
- Externalized configuration (prod vs dev DB).
- Pagination & search (LIKE on description).
- Internationalization (resource bundles for labels).
- Dark theme / Look & Feel customization.

---
## 16. Key Selling Points (Closing Lines)
- Cleanly demonstrates the *exact* syllabus points: JDBC, Swing, Packages, Interfaces, Inheritance, Exceptions, Validation.
- Compact yet extensible; ideal for team collaboration and oral defense.
- Added reliability features (auto schema, DB ping) show initiative beyond minimum.

---
**End of EXAM PREP GUIDE.**

Add more Q&A as you discover weak spots—treat this as a living document.
