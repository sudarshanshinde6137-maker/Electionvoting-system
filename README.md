# 🗳 Election Voting System
**Java + MySQL Workbench | Eclipse IDE**

---

## 📋 Features
| Feature | Description |
|---|---|
| Login / Register | Voters register & log in securely |
| Cast Vote | Each voter can vote exactly once |
| Admin Panel | Add/edit/delete candidates & manage voters |
| Live Results | Bar chart showing real-time vote counts |
| Reset Votes | Admin can reset all votes |
| Transaction Safety | Votes use SQL transactions (no double-votes) |

---

## ⚙️ Setup Instructions

### Step 1 — MySQL Workbench
1. Open **MySQL Workbench**
2. Open the file: `sql/election_db.sql`
3. Run the entire script (Ctrl+Shift+Enter)
4. This creates the `election_db` database with tables and sample data

### Step 2 — Download MySQL Connector
1. Download **mysql-connector-java-8.0.33.jar** from:  
   https://dev.mysql.com/downloads/connector/j/
2. Place the `.jar` file inside the `lib/` folder of this project

### Step 3 — Eclipse Import
1. Open **Eclipse IDE**
2. Go to **File → Import → Existing Projects into Workspace**
3. Browse to this project folder → Click **Finish**
4. Eclipse will auto-detect `.classpath` and `.project`

### Step 4 — Configure Database Password
Edit: `src/com/election/util/DBConnection.java`

```java
private static final String USER     = "root";       // your MySQL username
private static final String PASSWORD = "root";        // ← change to YOUR password
```

### Step 5 — Run
- Right-click `src/com/election/Main.java` → **Run As → Java Application**

---

## 👤 Default Login Credentials

| Role  | Username | Password |
|-------|----------|----------|
| Admin | admin    | admin123 |
| Voter | voter1   | voter123 |
| Voter | voter2   | voter123 |
| Voter | voter3   | voter123 |

---

## 🗂 Project Structure
```
ElectionVotingSystem/
├── src/
│   └── com/election/
│       ├── Main.java                  ← Entry point
│       ├── model/
│       │   ├── User.java
│       │   └── Candidate.java
│       ├── dao/
│       │   ├── UserDAO.java
│       │   ├── CandidateDAO.java
│       │   └── VoteDAO.java
│       ├── ui/
│       │   ├── LoginFrame.java
│       │   ├── RegisterFrame.java
│       │   ├── VoterDashboard.java
│       │   ├── AdminDashboard.java
│       │   └── ResultsFrame.java
│       └── util/
│           └── DBConnection.java
├── lib/
│   └── mysql-connector-java-8.0.33.jar   ← Add this manually
├── sql/
│   └── election_db.sql                   ← Run in MySQL Workbench
├── .classpath
└── .project
```

---

## 🧩 Database Tables
- **users** — voters and admin accounts
- **candidates** — election candidates with vote counts
- **votes** — individual vote records (unique per voter)
- **election_settings** — election name and active status

---

## ⚠️ Notes
- Votes are protected by **SQL transactions** — no partial or duplicate votes
- Each voter can only vote **once** (enforced in DB + app)
- The `lib/` folder is **empty** — you must add the MySQL connector JAR
