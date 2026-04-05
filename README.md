# 🤖 JavaChatbot — AI Chatbot with Java & Oracle DB

A console-based AI chatbot built with Core Java, Groq AI API, and Oracle Database.
Built from scratch as a learning project to understand how ChatGPT-like applications work.

---

## 📌 Project Summary

This project connects to a real AI model (Llama 3 via Groq API) and saves every conversation
to an Oracle Database. You can chat with the bot, view your chat history, and all messages
are stored permanently in the database.

---

## 🛠️ Technologies Used

| Technology      | Purpose                              |
|-----------------|--------------------------------------|
| Core Java       | Main programming language            |
| Maven           | Dependency/build management          |
| OkHttp          | Sending HTTP requests to Groq API    |
| Gson            | Parsing JSON responses from AI       |
| Groq API        | AI brain (Llama 3.1 8B model)        |
| Oracle XE       | Database to store chat history       |
| Oracle JDBC     | Connecting Java to Oracle DB         |
| Git & GitHub    | Version control and code storage     |

---

## 📁 Project Structure

```
JavaChatbot/
├── src/
│   └── main/
│       └── java/
│           └── com/buntu/chatbot/
│               └── App.java        ← Main chatbot code
├── pom.xml                         ← Maven dependencies
├── .gitignore                      ← Files to exclude from Git
└── README.md                       ← Project documentation
```

---

## 🗄️ Database Table

```sql
CREATE SEQUENCE chat_seq
START WITH 1
INCREMENT BY 1;

CREATE TABLE chat_history (
    id          NUMBER PRIMARY KEY,
    session_id  VARCHAR2(50)  NOT NULL,
    role        VARCHAR2(20)  NOT NULL,
    message     CLOB          NOT NULL,
    created_at  TIMESTAMP     DEFAULT CURRENT_TIMESTAMP
);
```

Every chat session gets a unique `session_id` and all messages
(both user and bot) are saved with timestamps.

---

## ⚙️ How It Works

```
You type a message
        ↓
Java sends it to Groq API (HTTP POST request)
        ↓
Groq API returns AI response (JSON)
        ↓
Java parses the JSON and displays the reply
        ↓
Both your message and bot reply are saved to Oracle DB
```

---

## 🚀 How to Run

### 1. Prerequisites
- Java JDK 11 or above
- Maven installed
- Oracle XE installed and running
- Groq API key (free at console.groq.com)

### 2. Clone the project
```bash
git clone https://github.com/gouthu07/JavaChatbot.git
cd JavaChatbot
```

### 3. Set your Groq API key
```cmd
# Windows
set GROQ_API_KEY=your-groq-api-key-here
```

### 4. Build the project
```bash
mvn clean install
```

### 5. Run the chatbot
```bash
mvn compile exec:java -Dexec.mainClass="com.buntu.chatbot.App"
```

---

## 💬 Chatbot Commands

| Command     | What it does                        |
|-------------|-------------------------------------|
| Any text    | Chat with the AI bot                |
| `history`   | Show last 10 messages from DB       |
| `exit`      | Exit the chatbot                    |

---

## 📦 Maven Dependencies

```xml
<!-- HTTP requests -->
<dependency>
    <groupId>com.squareup.okhttp3</groupId>
    <artifactId>okhttp</artifactId>
    <version>4.12.0</version>
</dependency>

<!-- JSON parsing -->
<dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
    <version>2.10.1</version>
</dependency>

<!-- Oracle JDBC -->
<dependency>
    <groupId>com.oracle.database.jdbc</groupId>
    <artifactId>ojdbc11</artifactId>
    <version>21.9.0.0</version>
</dependency>
```

---

## 🔐 Security Notes

- Never share your API key publicly
- The `secrets.properties` file is excluded from Git via `.gitignore`
- Always use environment variables for sensitive data

---

## 📚 What I Learned Building This

- How ChatGPT-like applications work internally
- Making HTTP requests from Java using OkHttp
- Parsing JSON responses using Gson
- Connecting Java to Oracle Database using JDBC
- Storing and retrieving data using SQL
- Managing dependencies with Maven
- Version control with Git and GitHub
- Keeping API keys and passwords secure

---

## 👨‍💻 Built By

**Buntu** — Built from scratch with zero copy-paste! 💪

> "First project where I actually understood every single line of code!"
