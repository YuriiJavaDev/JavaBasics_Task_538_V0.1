## The Task Manager implementation is a layered architecture.

### What We're Building and Why

Before writing code, it's important to understand the big picture. Our application is divided into **layers** — each layer is responsible for a single task and doesn't know anything about the others.

```
UI (TaskPanel, TaskRowPanel)
            ↕
Controller (TaskController)
            ↕
Service (TaskService / TaskServiceImpl)
            ↕
Repository (TaskRepository / JsonTaskRepository)
            ↕
tasks.json File
```

**Why not write everything in one class?**
Because when we connect the database, we only need to replace `JsonTaskRepository` with `DatabaseRepository`. Everything else will remain untouched. That's the whole point of separation.

---

### Step 1 — Data Model: `Task.java`

**Package:** `model/`

The `model` package stores classes that describe the application's data. These are just structures—no logic, no file handling. Just fields and methods to access them.

```java
package com.example.myassistant.model;

import java.util.UUID;

/**
* Model of a single task in a list.
*/
public class Task {
    
    private String id;
    private String text;
    private boolean completed;
    
    // constructor for creating a new task
    public Task(String text) {
    this.id = UUID.randomUUID().toString();
    this.text = text;
    this.completed = false;
    }
    
    public String getId() { return id; }
    public String getText() { return text; }
    public boolean isCompleted() { return completed; }
    
    public void setText(String text) { this.text = text; }
    public void setCompleted(boolean completed) { this.completed = completed; }
}
```

**Why a `UUID` for the id?**
Each task must have a unique identifier so that you know exactly which task to edit or delete. `UUID.randomUUID()` generates a string like `a3f2c1d0-4b5e-...`, which is guaranteed to be unique across the entire planet.

- **`UUID`** under the hood

**🎲 How it's created**

A v4 UUID is:

> 122 random bits + a few fixed bits (version and variant)
>

That is:

- 128 bits total
- ~6 bits reserved for version/format
- ~122 random bits remaining

---

**📊 How "unique" is it**

122 bits =

```
≈ 5.3 × 10^36 possible UUIDs
```

This number:

- more than the number of grains of sand on Earth
- and almost more than the number of atoms on a small planet

---

**💥 Probability of a Match**

For two UUIDs to match, you need to:

> randomly generate identical 122-bit numbers
> This is the "birthday problem."

Even if we generate:

- 1 billion UUIDs per second
- for a billion years

→ the chance of collision is still practically zero.

**Why not use a sequential number (1, 2, 3...)?**
If you delete a task with number 2 and add a new one, it will also receive the same number 2. This will lead to confusion. UUIDs don't have this problem.

---

### Step 2 — Task Row in the UI: `TaskRowPanel.java`

**Package:** `ui/panels/`

The ui/panels/ package stores panels—the visual blocks that make up the interface. TaskRowPanel is not a designer form, but a regular Java class. It is created programmatically because there can be any number of such rows—as many as the user has tasks.

```java
package com.example.myassistant.ui.panels;

import com.example.myassistant.model.Task;

import javax.swing.*;
import java.awt.*;

/**
* A single-row task panel: checkbox, text, edit and delete buttons.
*/
public class TaskRowPanel extends JPanel {
    
    private final Task task;
    private final JCheckBox checkBox;
    private final JLabel taskLabel;
    private final JButton editButton;
    private final JButton deleteButton;
    
    public TaskRowPanel(Task task) { 
        this.task = task; 
        
        setLayout(new BorderLayout()); 
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 40)); 
        setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8)); 
        
        checkBox = new JCheckBox(); 
        checkBox.setSelected(task.isCompleted()); 
        
        taskLabel = new JLabel(task.getText()); 
        
        // apply the style immediately when creating the line 
        if (task.isCompleted()) { 
            taskLabel.setText("<html><strike>" + task.getText() + "</strike></html>"); 
            taskLabel.setForeground(Color.GRAY); 
        } 
        
        editButton = new JButton("Edit"); 
        deleteButton = new JButton("✕"); 
        
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0)); 
        leftPanel.add(checkBox); 
        leftPanel.add(taskLabel); 
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0)); 
        buttonPanel.add(editButton); 
        buttonPanel.add(deleteButton); 
        
        add(leftPanel, BorderLayout.WEST); 
        add(buttonPanel, BorderLayout.EAST); 
    } 
    
    public Task getTask() { return task; } 
    public JCheckBox getCheckBox() { return checkBox; } 
    public JButton getEditButton() { return editButton; } 
    public JButton getDeleteButton(){ return deleteButton; }
}
```

**Why is the style applied in the constructor and not when clicking the checkbox?**
The controller completely recreates all rows with each change by calling `refreshTaskList()`. This means that on the next repaint, a new `TaskRowPanel` is created with the updated `Task` object. Therefore, it is sufficient to apply the style once during creation.

**`setMaximumSize`** limits the row height—otherwise, `BoxLayout` might stretch it to fill the entire screen.

---

### Step 3 — Constants: `AppConstants.java`

**Package:** `util/`

The `util/` package stores utility classes and constants. The rule is simple: if the same string is used in multiple places, it must be a constant. Otherwise, when changing a value, you'll have to search for all occurrences throughout the entire project.

```java
package com.example.myassistant.util;

/**
* Application constants — file paths, URLs, etc.
*/
public class AppConstants {
    public static final String TASKS_FILE = "tasks.json";
}
```

---

### Step 4 — Data Layer: `TaskRepository` and `JsonTaskRepository`

**Package:** `repository/` and `repository/impl/`

The `repository/` package stores the **interface** — a description of what the repository can do, without implementation details. The `repository/impl/` package stores the **concrete implementation** — currently via a JSON file, later via a database.

This separation allows the service to work with any repository without knowing how the data is stored.

### Interface

```java
package com.example.myassistant.repository;

import com.example.myassistant.model.Task;
import java.util.List;

/**
* Contract for accessing task data. The implementation can be any: file, database, network.
*/
public interface TaskRepository {
    void save(List<Task> tasks); //save (save) a task (e.g. to a file)
    List<Task> load(); //load a list of tasks (e.g. from a file)
}
```

### Implementation

```java
package com.example.myassistant.repository.impl;

import com.example.myassistant.model.Task;
import com.example.myassistant.repository.TaskRepository;
import com.example.myassistant.util.AppConstants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/** 
* Saves and loads tasks from a JSON file. 
*/
public class JsonTaskRepository implements TaskRepository { 
    
    private static final Logger log = LoggerFactory.getLogger(JsonTaskRepository.class); 
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    
    @Override
    public void save(List<Task> tasks) {
        //Creates a file writer. All old contents are ERASED
        //Java will automatically close the file after the block
        try (Writer writer = new FileWriter(AppConstants.TASKS_FILE)) {
            //Gson takes a List<Task>, converts each Task to a JSON object, and immediately writes it to the file via writer
            gson.toJson(tasks, writer);
        } catch (IOException e) {
            log.error("Error saving tasks to file", e);
        }
    }
    
    //Read the file: tasks.json and convert it back to: List<Task>
    @Override
    public List<Task> load() {
        File file = new File(AppConstants.TASKS_FILE);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        //This is a reader stream: opens a file, reads text (JSON), and automatically closes after a block. Therefore, Gson needs to be explicitly told: "I want a List of Tasks, not just a List."
        try (Reader reader = new FileReader(file)) {
            //Java does NOT know what a List<Task> is,
            Type listType = new TypeToken<List<Task>>(){}.getType();
            //Gson does: reads JSON text; understands array structure; creates Task objects; puts them in a List
            List<Task> tasks = gson.fromJson(reader, listType);
            return tasks != null ? tasks : new ArrayList<>();
        } catch (IOException e) {
            log.error("Error loading tasks from file", e);
            return new ArrayList<>();
        }
    }
}
```

**Why `TypeToken`?**
Java erases information about generic types during compilation—this is called "type erasure." Gson can't figure out that it needs a `List<Task>`, not just a `List`. `TypeToken` stores this information explicitly.

- **Logger (SLF4J)**
- ✅ How to include via Maven

Open `pom.xml` and add:

```xml
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-api</artifactId>
    <version>2.0.13</version>
</dependency>
```

---

**⚠️ IMPORTANT**

This is only the **API (logger interface)**.

For logging to actually work, you also need an "engine" (implementation).

```xml 
<dependency> 
    <groupId>ch.qos.logback</groupId> 
    <artifactId>logback-classic</artifactId> 
    <version>1.5.6</version> 
</dependency> 
``` 

--- 

**📦 Final set in pom.xml**

```xml 
<dependencies> 
    <dependency> 
        <groupId>org.slf4j</groupId> 
        <artifactId>slf4j-api</artifactId> 
        <version>2.0.13</version> 
    </dependency> 
    
    <dependency> 
        <groupId>ch.qos.logback</groupId> 
        <artifactId>logback-classic</artifactId> 
        <version>1.5.6</version> 
    </dependency> 
    
    <dependency>
        <groupId>com.google.code.gson</groupId>
        <artifactId>gson</artifactId>
        <version>2.10.1</version>
        </dependency>
</dependencies>
```

---

**🚀 After this**

In IntelliJ: 👉 click **Reload Maven Projects**

**Check:**

📁 External Libraries (on the left in Project)

The following should appear there:

`Maven: slf4j-api`

`Maven: logback-classic`

`Maven: gson`

If they are there → everything is installed ✔

```java
private static final Logger log = LoggerFactory.getLogger(JsonTaskRepository.class);
```

This is a **logger** — a tool for recording program execution events.

It replaces `System.out.println()` in "adult" projects.

---

**🧩 What parts does it consist of**

### 🔹 `Logger`

This is an interface:

> an object that can log messages (info, error, warn, etc.)

---

### 🔹 `LoggerFactory.getLogger(...)`

Creates a logger **for a specific class**.

```
JsonTaskRepository.class
```

👉 means:

> “This logger belongs to the JsonTaskRepository class”

---

**📌 Why `static`**

👉 Logger:

- one per class
- not re-created for each object

---

**📌 Why `final`**

👉 because:

- the logger shouldn't change
- it's created once and for all

---

**📌 Why use a logger at all if you have println?**

### ❌ System.out.println:

- no severity levels
- difficult to disable
- can't write to a file nicely
- can't filter

---

### ✅ Logger can:

### 1. Log levels

- `info` → regular events
- `warn` → warnings
- `error` → errors

---

### 2. Example:

```java
log.info("Tasks loaded");
log.error("Loading error",e);
```

---

### 3. You can enable/disable it in the config.

👉 For example:

- Show only errors in production
- Show everything in development

---

## 🧠 Simple analogy

Logger = “airplane log”

- What happened
- When did it happen
- Was there an error
- To start writing logs to a file

To start writing logs to a file, you need to **add a logging configuration.**

**📌 What you need to do (step by step)**

**✅ 1. Create a configuration file**

```
src/main/resources/logback.xml
```

---

**📄 2. Insert the basic configuration (console + file)**

```xml
<configuration>

    <!-- 📌 Logs to console -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender"> 
        <encoder> 
            <pattern>%d{HH:mm:ss} %-5level %logger - %msg%n</pattern> 
        </encoder> 
    </appender> 
    
    <!-- 📌 Logs to file --> 
    <appender name="FILE" class="ch.qos.logback.core.FileAppender"> 
        <file>app.log</file> 
        
        <append>true</append> 
        
        <encoder> 
            <pattern>%d{yyyy-MM-dd HH:mm:ss} %-5level %logger - %msg%n</pattern> 
        </encoder> 
    </appender> 
    
    <!-- 📌 what log levels are enabled --> 
    <root level="info"> 
        <appender-ref ref="CONSOLE"/> 
        <appender-ref ref="FILE"/> 
    </root>

</configuration>
```

---

After starting the project:

**✔ we will see logs in the IDE console

**✔ a file will appear in the project:** app.log and all logs will be written there

- **Gson**

**Dependency:**

```xml 
<!-- Gson: working with JSON --> 
<dependency> 
    <groupId>com.google.code.gson</groupId> 
    <artifactId>gson</artifactId> 
    <version>2.10.1</version> 
</dependency> 
``` 

```java 
private final Gson gson = 
new GsonBuilder().setPrettyPrinting().create(); 
``` 

`Gson` - a library from Google for:

> Java object conversions ↔ JSON

--- 

**🔄Two main operations**

### 1. Java → JSON

```java 
gson.toJson(tasks,writer); 
``` 

👉 the task list turns into text:

```java 
[ 
    { 
        "id":"123", 
        "text":"Buy milk", 
        "completed":false 
    } 
] 
``` 

--- 

### 2. JSON → Java

``` 
gson.fromJson(reader,listType); 
``` 

👉 back:

JSON → List<Task>

--- 

**📌What does `GsonBuilder`**

```java 
new GsonBuilder() 
``` 

👉 this is the Gson “customer”

--- 

**🎨 `.setPrettyPrinting()`**

👉 makes JSON beautiful:

### without it:

``` 
{"id":"1","text":"task","completed":false} 
``` 

### with him:

``` 
{ 
"id":"1", 
"text":"task", 
"completed":false 
} 
``` 

--- 

**🔧 `.create()`**

👉 final step:

creates a ready-made `Gson` object

--- 

**🧠 Why is it `final`**

```java 
private final Gsongson 
``` 

Because:

- the configuration is fixed once
- no need to recreate every time
- it is a light and safe object

--- 

**⚖️ Connection with our architecture**

**Repository does:**

- JSON ↔ Java
- saving to file
- loading from file

👉 and for this uses:

- Gson (conversion)
- Logger (debugging and errors)

**`try-with-resources`** - the `try (Writer writer = ...)` construct automatically closes the file after the block completes, even if an error occurs. This protects against resource leakage.

**`Logger` instead of `System.out.println`** - in real applications, logs are written via SLF4J. This allows you to manage logging levels (INFO, ERROR, DEBUG) and send them to a file or monitoring system.

---

### Step 5 — Business Logic: `TaskService` and `TaskServiceImpl`

**Package:** `service/` and `service/impl/`

The service is the heart of the application. It contains the rules: what it means to add a task, how to sort the list, what happens when a task is marked complete.

### Interface

```java
package com.example.myassistant.service;

import com.example.myassistant.model.Task;
import java.util.List;

/**
* Task management business logic.
*/
public interface TaskService {
    void addTask(String text);
    void editTask(String id, String newText);
    void deleteTask(String id);
    void toggleCompleted(String id);
    void deleteCompleted();
    void clearAll();
    List<Task> getAllTasks();
}
```

### Implementation

```java
package com.example.myassistant.service.impl;

import com.example.myassistant.model.Task;
import com.example.myassistant.repository.TaskRepository;
import com.example.myassistant.service.TaskService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
* Implementation of the task service. Stores the list in memory and synchronizes with the repository.
*/
public class TaskServiceImpl implements TaskService {
    
    private final TaskRepository repository;
    private final List<Task> tasks; //This is an in-memory copy of the data, which we work with quickly without reading the file each time.
    
    public TaskServiceImpl(TaskRepository repository) {
        this.repository = repository;
        // load saved tasks on startup
        this.tasks = new ArrayList<>(repository.load());
    }
    
    @Override
    public void addTask(String text) {
        tasks.add(new Task(text));
        repository.save(tasks);
    }
    
    @Override
    public void editTask(String id, String newText) {
        tasks.stream()
        .filter(t -> t.getId().equals(id))
        .findFirst()
        .ifPresent(t -> t.setText(newText));
        repository.save(tasks);
    } 
    
    @Override 
    public void deleteTask(String id) { 
        tasks.removeIf(t -> t.getId().equals(id)); 
        repository.save(tasks); 
    } 
    
    @Override 
    public void toggleCompleted(String id) { 
        tasks.stream() 
        .filter(t -> t.getId().equals(id)) 
        .findFirst() 
        .ifPresent(t -> t.setCompleted(!t.isCompleted())); 
        repository.save(tasks); 
    } 
    
    @Override 
    public void deleteCompleted() { 
        tasks.removeIf(Task::isCompleted); 
        repository.save(tasks); 
    } 
    
    @Override 
    public void clearAll() { 
        tasks.clear(); 
        repository.save(tasks); 
    }
    
    @Override
    public List<Task> getAllTasks() {
        // uncompleted at the top, completed at the bottom
        //false (uncompleted) will come before; true (completed) will come last
        return tasks.stream()
        .sorted(Comparator.comparing(Task::isCompleted))
        .collect(Collectors.toList());
    }
}
```

**Why is the list stored in memory (`tasks`) instead of being read from a file each time it's accessed?**
Reading a file is slow. Keeping the current list in memory and saving it each time it's modified is the standard approach for small amounts of data.

**`tasks.stream().filter(...).findFirst().ifPresent(...)`** — search for a task by id via the Stream API. This is a modern Java way of working with collections instead of a `for` loop.

---

### Step 6 — Controller: `TaskController.java`

**Package:** `controller/`

The controller is an intermediary between the UI and the service. It neither stores nor displays data. Its only job is to listen for user actions, pass them to the service, and update the UI.

```java
package com.example.myassistant.controller;

import com.example.myassistant.model.Task;
import com.example.myassistant.service.TaskService;
import com.example.myassistant.ui.panels.TaskPanel;
import com.example.myassistant.ui.panels.TaskRowPanel;

import javax.swing.*;
import java.util.List;

/**
* Connects the task UI with the business logic. Handles all user actions. 
*/
public class TaskController { 
    
    private final TaskService taskService; 
    private final TaskPanel taskPanel; 
    
    public TaskController(TaskPanel taskPanel, TaskService taskService) { 
        this.taskService = taskService; 
        this.taskPanel = taskPanel; 
        init(); 
    } 
    
    private void init() { 
        taskPanel.getAddButton().addActionListener(e -> addTask()); 
        taskPanel.getTaskInputField().addActionListener(e -> addTask()); 
        taskPanel.getDeleteCompletedButton().addActionListener(e -> deleteCompleted()); 
        taskPanel.getClearAllButton().addActionListener(e -> clearAll()); 
        refreshTaskList(); 
    } 
    
    private void addTask() { 
        String text = taskPanel.getTaskInputField().getText().trim(); 
        if (text.isEmpty()) return; 
        taskService.addTask(text); 
        taskPanel.getTaskInputField().setText(""); 
        refreshTaskList(); 
    } 
    
    private void deleteCompleted() { 
        int confirm = JOptionPane.showConfirmDialog( 
        taskPanel.getMainPanel(), 
        "Delete all completed tasks?", 
        "Confirmation", 
        JOptionPane.YES_NO_OPTION); 
        if (confirm == JOptionPane.YES_OPTION) { 
            taskService.deleteCompleted(); 
            refreshTaskList(); 
        } 
    } 
    
    private void clearAll() { 
        int confirm = JOptionPane.showConfirmDialog( 
        taskPanel.getMainPanel(), 
        "Clear entire task list?", 
        "Confirmation", 
        JOptionPane.YES_NO_OPTION); 
        if (confirm == JOptionPane.YES_OPTION) { 
            taskService.clearAll(); 
            refreshTaskList(); 
        } 
    } 
    
    private void refreshTaskList() { 
        taskPanel.getTaskListPanel().removeAll(); 
        
        for (Task task : taskService.getAllTasks()) { 
            TaskRowPanel row = new TaskRowPanel(task); 
            
            row.getCheckBox().addActionListener(e -> { 
                taskService.toggleCompleted(task.getId()); 
                refreshTaskList(); 
            }); 
            
            row.getEditButton().addActionListener(e -> { 
                String newText = JOptionPane.showInputDialog( 
                taskPanel.getMainPanel(), 
                "Edit task:", 
                task.getText()); 
                if (newText != null && !newText.trim().isEmpty()) { 
                    taskService.editTask(task.getId(), newText.trim()); 
                    refreshTaskList(); 
                } 
            }); 
            
            row.getDeleteButton().addActionListener(e -> { 
                int confirm = JOptionPane.showConfirmDialog( 
                taskPanel.getMainPanel(), 
                "Delete task\"" + task.getText() + "\"?", 
                "Confirmation", 
                JOptionPane.YES_NO_OPTION); 
                if (confirm == JOptionPane.YES_OPTION) { 
                    taskService.deleteTask(task.getId()); 
                    refreshTaskList(); 
                } 
            }); 
            
            taskPanel.getTaskListPanel().add(row); 
        } 
        
        taskPanel.getTaskListPanel().revalidate(); 
        taskPanel.getTaskListPanel().repaint(); 
        updateStats(); 
    } 
    
    private void updateStats() { 
        List<Task> all = taskService.getAllTasks(); 
        long completed = all.stream().filter(Task::isCompleted).count();
        long pending = all.size() - completed;
        int progress = all.isEmpty() ? 0 : (int) (completed * 100 / all.size());
        
        taskPanel.getTotalLabel().setText(String.valueOf(all.size()));
        taskPanel.getCompletedLabel().setText(String.valueOf(completed));
        taskPanel.getPendingLabel().setText(String.valueOf(pending));
        taskPanel.getProgressLabel().setText(progress + "%");
    }
}
```

**Why are button listeners assigned in the controller and not in `TaskRowPanel`?**``TaskRowPanel` is a visual component; it shouldn't be aware of the service and business logic. The controller knows about both the UI and the service, so it connects them.

**`revalidate()` + `repaint()`** — after programmatically adding or removing components, Swing doesn't automatically repaint them. `revalidate()` recalculates the layout, `repaint()` repaints the content.

---

### Step 7 — Building in `MainFrame`

All layers are created and connected in `MainFrame`. Note the order: first the repository is created, then the service, then the controller—each controller receives the previous one through the constructor.

```java
TaskPanel taskPanel = new TaskPanel();
new TaskController(taskPanel, new TaskServiceImpl(new JsonTaskRepository()));
tabbedPane.addTab("Tasks", taskPanel.getMainPanel());
```

This is called **Dependency Injection**—each object receives its dependencies through the constructor, rather than creating them itself. Thanks to this, tomorrow we can write:

```java
new TaskController(taskPanel, new TaskServiceImpl(new DatabaseTaskRepository()));
```

And all the rest of the code will remain untouched.

---

### Summary of Package Diagrams and Their Responsibilities

```
model/ — WHAT is stored (data, without logic)
repository/ — WHERE it is stored (data access contract)
repository/impl/ — HOW it is stored (specifically: file, DB, etc.)
service/ — WHAT can be done (business logic contract)
service/impl/ — HOW it is done (specific logic)
controller/ — WHO responds to user actions
ui/panels/ — WHAT the user sees
util/ — auxiliary (constants, utilities)
```

### Common Mistakes

- Writing business logic directly in the controller or UI quickly makes the code unreadable
- Not creating interfaces for services and repositories — then when changing the implementation, you'll have to rewrite everything that depends on them
- Forgetting to call `revalidate()` + `repaint()` after programmatically changing the panel's contents — components won't appear screen
- Store constants (file paths, URLs) directly in the code as strings – when changing, you need to search the entire project
- Use `System.out.println` instead of a logger – this is unacceptable in a real application.

---

---

## Task#1 Filtering the task list

### What needs to be implemented

Add the ability to filter tasks by status to the Task Manager:
- **All** — show all tasks
- **Active** — only uncompleted tasks
- **Completed** — only completed tasks

The user selects a filter from the drop-down list — the task list is updated instantly.

---

### Before you begin — think

Before writing code, answer the following questions:

1. In which application layer does the "which tasks to show" logic reside?
2. Does the Task model need to be changed?
3. Where will the drop-down list be located — which Swing component is suitable for this?

---

### Step 1 — Create a FilterType

In the model package, create an enum called FilterType.

An enum is a data type with a fixed set of values. Instead of the strings `"All"` and `"Active"`, use type-safe constants.

The enum must contain three values: `ALL`, `COMPLETED`, `PENDING`.

Each value must store a string for display in the UI (e.g., `"All"`).

Override the `toString()` method to return this string – then the `JComboBox` will display human-readable names automatically.

> **Hint:** An enum with fields is declared like this:
>
>
> ```java
> public enum Season {
>   SUMMER("Summer"),
>   WINTER("Winter");
>
>   private final String name;
>
>   Season(String name) { this.name = name; }
>
>   @Override
>   public String toString() { return name; }
> }
> ```
>
> - **More details**
>
> ```java
> public enum Season {
>   SUMMER("Summer"),
>   WINTER("Winter");
>   //...
> }
> ```
>
> When the JVM first loads the Season class, it creates all of its constants.
>
> Essentially, something like this happens:
>
> ```java
> SUMMER=new Season("Summer");
> WINTER=new Season("Winter");
> ```
>
> (The actual code is a bit more complicated, but that's the idea).
>
> **What happens next?**
>
> After the class is loaded, the objects already exist in memory. >
> It looks something like this:
>
> ```
> Season
> ├─ SUMMER
> │ └─ displayName = "Summer"
> │
> ├─ WINTER
> │ └─ displayName = "Winter"
> │
> └─ ...
> ```
>
> ---
>
> **what does this line do?**
>
> ```java
> Season f = Season.WINTER;
> ```
>
> It does NOT create an object.
>
> It simply stores a reference to an existing object in the variable `f`. >

---

### Step 2 — Add a method to `TaskService`

Add a new method to the `TaskService` interface:

```java
List<Task> getTasksByFilter(FilterType filter);
```

> **Why add it to the interface and not directly to the implementation?**
An interface is a contract. The controller works with the interface without knowing the specific implementation. If you add the method only to `TaskServiceImpl`, the controller won't see it.
>

---

### Step 3 — Implement the method in `TaskServiceImpl`

Implement `getTasksByFilter` using a `switch` on the `FilterType` value.

For `COMPLETED`, return only completed tasks.
For `PENDING`, return only uncompleted tasks. For `ALL`, return everything (you can reuse an existing method).

> **Hint:** Use the Stream API for filtering:
>
>
> ```java
> tasks.stream()
> .filter(t -> t.isCompleted())
> .collect(Collectors.toList());
> ```
>

> **Hint:** Switch expression in Java 17:
>
>
> ```java
> return switch (filter) {
>   case COMPLETED -> /* ... */;
>   case PENDING -> /* ... */;
>   default -> /* ... */;
> };
> ```
>

---

### Step 4 - Add a `JComboBox` to the form

Open `TaskPanel.form` in the GUI Designer.

Find a suitable location for the drop-down list—a logical place would be next to the input area or above the task list.

From the palette, drag a **JComboBox** to the desired location.
Give it a `field name`: `filterBox`.

> **What is a JComboBox?**
A drop-down list. The user clicks it, and options appear. Suitable when there are only a few options and the user needs to select one.

---

### Step 5 — Update `TaskPanel.java`

Add a field with the correct generic type:

```java
private JComboBox<FilterType> filterBox;
```

In the constructor, fill it with values from the enum. Use `FilterType.values()`—it returns all the enumeration values in order:

```java
for (FilterType type : FilterType.values()) {
filterBox.addItem(type);
}
```

Add a getter:

```java
public JComboBox<FilterType> getFilterBox() { return filterBox; }
```

---

### Step 6 — Connect the filter to the `TaskController`

In the `init()` method, add a listener to `filterBox` — when the value changes, `refreshTaskList()` should be called.

In the `refreshTaskList()` method, get the selected filter and pass it to the service:

```java
FilterType filter = (FilterType) taskPanel.getFilterBox().getSelectedItem();
List<Task> tasksToShow = taskService.getTasksByFilter(filter);
```

Use `tasksToShow` instead of `taskService.getAllTasks()` when building a list of strings.

---

### Test

After implementation, ensure that:

- [ ] When selecting "Active," only uncompleted tasks are shown in the list.
- [ ] When selecting "Completed," only completed tasks are shown in the list.
- [ ] When selecting "All," all tasks are shown.
- [ ] Statistics always display all tasks, regardless of the filter.
- [ ] When adding a new task, the list is updated based on the current filter.

> **Please note the last point:** statistics are calculated based on `taskService.getAllTasks()`, not the filtered list. Consider why this is correct.
>

---

### ⭐ Additional Task

Currently, when switching the filter to "Completed," the "Delete Completed" button is meaningless—the user only sees those tasks anyway. Consider how to improve this. Options:

- Hide/show the button depending on the selected filter
- Rename the button ("Delete all in the list")
- Disable the button if there are no completed tasks

---

## Solution:

### Step 1 — Create `FilterType.java`

In the `model` package, create a new file `FilterType.java`.

**How to create:** Right-click the `model` package → New → Java Class → select the **Enum** type → enter `FilterType`.

```java
package com.example.myassistant.model;

/**
* Todo list filter types.
*/
public enum FilterType {
    ALL("All"),
    COMPLETED("Completed"),
    PENDING("Active");
    
    private final String displayName;
    
    FilterType(String displayName) {
        this.displayName = displayName;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}
```

**What's going on here:**
- `ALL`, `COMPLETED`, `PENDING` are three enumeration constants
- Each stores the string `displayName` — a human-readable name for the UI
- `toString()` returns this name — this is what the `JComboBox` will show to the user

---

### Step 2 — Add a method to the `TaskService` interface

Open `TaskService.java` and add one method to the end of the interface:

```java
package com.example.myassistant.service;

import com.example.myassistant.model.FilterType;
import com.example.myassistant.model.Task;

import java.util.List;

/**
* Task management business logic.
*/
public interface TaskService {
    
    void addTask(String text);
    void editTask(String id, String newText);
    void deleteTask(String id);
    void toggleCompleted(String id);
    void deleteCompleted();
    void clearAll();
    List<Task> getAllTasks();
    List<Task> getTasksByFilter(FilterType filter); // ← added
}
```

After adding, IntelliJ will underline `TaskServiceImpl` in red—this is normal, meaning the implementation hasn't been added yet. Let's move on to the next step.

---

### Step 3 — Implement the method in `TaskServiceImpl`

Open `TaskServiceImpl.java` and add the implementation of the new method to the end of the class:

```java
@Override
public List<Task> getTasksByFilter(FilterType filter) {
    return switch (filter) {
        case COMPLETED -> tasks.stream()
        .filter(Task::isCompleted)
        .collect(Collectors.toList());
        case PENDING -> tasks.stream()
        .filter(t -> !t.isCompleted())
        .collect(Collectors.toList());
        default -> getAllTasks();
    };
}
```

Add imports at the top of the file:

```java
import com.example.myassistant.model.FilterType;
```

**What's going on here:**
- `switch` by enum value — each filter has its own logic
- `Task::isCompleted` — method reference, shorthand for `t -> t.isCompleted()`
- `default` (case `ALL`) — reusing the existing `getAllTasks()`, which returns sorted tasks

---

### Step 4 — Add a `JComboBox` to the form

Open `TaskPanel.form` in the GUI Designer.

**Where to place:** above the task list — between `topPanel` and `JScrollPane`. This makes sense: first the filter, then the filtered list.

**Steps:**
1. In the component palette, find **JComboBox**
2. Drag it to the `mainPanel` between the `topPanel` and the `JScrollPane` - watch for the blue hint line; it should appear between these two elements.
3. Click the added `JComboBox`
4. In the properties panel, set **field name** to `filterBox`

After adding, the component tree should look like this:

```
mainPanel
├── topPanel
│   ├── taskInputField
│   └── addButton
├── filterBox ← new element
├── JScrollPane
│   └── taskListPanel
├── buttonsPanel
│   ├── deleteCompletedButton
│   └── clearAllButton
└── statsPanel
    └── ...
```

---

### Step 5 — Update `TaskPanel.java`

Open `TaskPanel.java`. Make three changes.

**1. Add a field** (with the generic type `FilterType`):

```java
private JComboBox<FilterType> filterBox;
```

**2. In the constructor, populate the list with values** — add the following lines after configuring `taskListPanel`:

```java
public TaskPanel() {
    taskListPanel.setLayout(new BoxLayout(taskListPanel, BoxLayout.Y_AXIS));
    
    // populate the drop-down list with all filter options
    for (FilterType type : FilterType.values()) {
        filterBox.addItem(type);
    }
}
```

`FilterType.values()` returns all enum constants in declaration order: `ALL`, `COMPLETED`, `PENDING`. By default, the first one, `ALL`, will be selected.

**3. Add a getter** to the end of the getter list:

```java
public JComboBox<FilterType> getFilterBox() { return filterBox; }
```

Add import at the top of the file:

```java
import com.example.myassistant.model.FilterType;
```

---

### Step 6 — Connecting the filter to `TaskController`

Open `TaskController.java`. Two changes.

**1. In the `init()` method, add a listener** — after the existing listeners:

```java
private void init() {
    taskPanel.getAddButton().addActionListener(e -> addTask());
    taskPanel.getTaskInputField().addActionListener(e -> addTask());
    taskPanel.getDeleteCompletedButton().addActionListener(e -> deleteCompleted());
    taskPanel.getClearAllButton().addActionListener(e -> clearAll());
    taskPanel.getFilterBox().addActionListener(e -> refreshTaskList()); // ← added
    refreshTaskList();
}
```

**2. In the `refreshTaskList()` method, replace the task list retrieval:**

Was:

```java
for (Task task : taskService.getAllTasks()) {
```

Now:

```java
FilterType filter = (FilterType) taskPanel.getFilterBox().getSelectedItem();
List<Task> tasksToShow = taskService.getTasksByFilter(filter);

    for (Task task : tasksToShow) {
```

Add import at the top of the file:

```java
import com.example.myassistant.model.FilterType;
```

**Important:** The `updateStats()` method is not touched—it calls `taskService.getAllTasks()` and always calculates statistics for all tasks, regardless of the filter. This is correct: the statistics show the actual state of the task, not just what is currently filtered.

---

### Final view of the modified `TaskController` methods

```java
private void init() {
    taskPanel.getAddButton().addActionListener(e -> addTask());
    taskPanel.getTaskInputField().addActionListener(e -> addTask());
    taskPanel.getDeleteCompletedButton().addActionListener(e -> deleteCompleted());
    taskPanel.getClearAllButton().addActionListener(e -> clearAll());
    taskPanel.getFilterBox().addActionListener(e -> refreshTaskList()); 
    refreshTaskList();
}

private void refreshTaskList() { 
    taskPanel.getTaskListPanel().removeAll(); 
    
    FilterType filter = (FilterType) taskPanel.getFilterBox().getSelectedItem(); 
    List<Task> tasksToShow = taskService.getTasksByFilter(filter); 
    
    for (Task task : tasksToShow) { 
        TaskRowPanel row = new TaskRowPanel(task); 
        
        row.getCheckBox().addActionListener(e -> { 
            taskService.toggleCompleted(task.getId()); 
            refreshTaskList(); 
        }); 
        
        row.getEditButton().addActionListener(e -> { 
            String newText = JOptionPane.showInputDialog( 
            taskPanel.getMainPanel(), 
            "Edit task:", 
            task.getText()); 
            if (newText != null && !newText.trim().isEmpty()) { 
                taskService.editTask(task.getId(), newText.trim()); 
                refreshTaskList(); 
            } 
        }); 
        
        row.getDeleteButton().addActionListener(e -> { 
            int confirm = JOptionPane.showConfirmDialog( 
            taskPanel.getMainPanel(), 
            "Delete task\"" + task.getText() + "\"?", 
            "Confirmation", 
            JOptionPane.YES_NO_OPTION); 
            if (confirm == JOptionPane.YES_OPTION) { 
                taskService.deleteTask(task.getId()); 
                refreshTaskList(); 
            } 
        }); 
        
        taskPanel.getTaskListPanel().add(row); 
    } 
    
    taskPanel.getTaskListPanel().revalidate(); 
    taskPanel.getTaskListPanel().repaint(); 
    updateStats();
}
```

---

### Test

Run the app and check:

1. Add several tasks
2. Mark some of them as completed
3. Switch the filter to **"Active"** - only uncompleted tasks should remain
4. Switch to **"Completed"** - only completed tasks
5. Switch to **"All"** - all tasks
6. Make sure the statistics don't change when you change the filter
7. Add a new task while the filter is active - it should appear if it matches the filter

---

### What we used in this task

| Concept | Where used |
| --- | --- |
| `enum` | `FilterType` — type-safe constants instead of strings |
| Interface extension | Added method to `TaskService` |
| Stream API | Filtering in `getTasksByFilter` |
| `switch` expression (Java 17) | Selecting logic by filter type |
| `JComboBox<T>` | Drop-down list in UI |
| Event listener | `addActionListener` on `filterBox` |


---

---

## Task#2 Adding the task creation time and date

## Task: Task creation date

### What needs to be implemented

Each task should store the creation date and time. A label like `03.06.2026 14:35` should appear in the list next to the task text.

Affected files:
- `Task.java` — add the `createdAt` field
- `AppConstants.java` — add the date format constant
- `util/LocalDateTimeAdapter.java` — new class for Gson to work correctly with dates
- `JsonTaskRepository.java` — register the adapter
- `TaskRowPanel.java` — display the date in the task row

---

### Before you begin, think about it

1. Which class stores information about a single task? Where would it be logical to add a date field?
2. What data type in Java represents date and time? How can I get the current moment?
3. Does Gson store `LocalDateTime` in JSON out of the box? What should I do if it doesn't?

---

### Step 1 — Add the `createdAt` field to `Task.java`

Add a `LocalDateTime` field to the model and populate it in the constructor when the task is created.

Don't forget the getter.

> **Hint:** To get the current date and time, use:
>
>
> ```java
> LocalDateTime.now()
> ```
>

---

### Step 2 — Add a constant to `AppConstants.java`

Add a string constant with the date display format so that the same format is used in multiple places in the code.

The required format is `dd.MM.yyyy HH:mm` — it will produce a string like `03.06.2026 14:35`.

> **Why are we storing it in a constant instead of writing the string directly?**
The format is used in at least two places: in the adapter and in the UI. If we want to change the display, we change it in one place.
>

---

### Step 3 — Create a `LocalDateTimeAdapter` in the `util` package

**Why is it needed?**

Gson cannot serialize `LocalDateTime` by default. Without an adapter, when saving a task to a file, the `createdAt` field will be written incorrectly or will cause an error.

An adapter is a class that tells Gson how to convert `LocalDateTime` to a string and back.

Create a `LocalDateTimeAdapter` class that implements two interfaces (interfaces from the Gson library):
- `JsonSerializer<LocalDateTime>` — serialization (object → JSON)
- `JsonDeserializer<LocalDateTime>` — deserialization (JSON → object)

> **Hint:** The serialization method receives a `LocalDateTime src` and should return a `JsonElement`.
The deserialization method receives a `JsonElement json` and should return a `LocalDateTime`.
>
>
> For formatting, use `DateTimeFormatter.ofPattern(...)`. >

> **Important note:** use **different formats** for storage and display:
- In a file — machine-readable ISO format: `yyyy-MM-dd'T'HH:mm:ss`
- In the UI — human-readable: `dd.MM.yyyy HH:mm` (from `AppConstants`)
>
>
> This is standard practice: data is stored in a format convenient for the program and displayed in a format convenient for the user.
>

---

### Step 4 — Register the adapter in `JsonTaskRepository.java`

Find the location where the `Gson` object is created via `GsonBuilder` and add the adapter registration.

> **Hint:** `GsonBuilder` has the `.registerTypeAdapter(Class, Object)` method. The first argument is `LocalDateTime.class`, the second is an instance of your adapter.
>

---

### Step 5 — Display the date in `TaskRowPanel.java`

Add a `JLabel` with the formatted date next to the task text.

> **Hint:** To format the date for display:
>
>
> ```java
> DateTimeFormatter formatter = DateTimeFormatter.ofPattern(AppConstants.DATE_FORMAT);
> String dateText = task.getCreatedAt().format(formatter);
> ```
>

> **Hint:** Make the label gray so it doesn't distract from the task text:
>
>
> ```java
> dateLabel.setForeground(Color.GRAY);
> ```
>

Add a label to `leftPanel` after `taskLabel`.

---

### Test

After implementation, ensure that:

- [ ] New tasks display the creation date and time
- [ ] The date is grayed out and doesn't distract from the task text
- [ ] Dates are preserved (not reset) after restarting the application
- [ ] Old tasks (without the `createdAt` field in the file) load without errors

> **Note the last point:** if you already have a `tasks.json` with tasks without the `createdAt` field, it will be `null` when loaded. A simple solution is to delete the file before running the new code for the first time. Consider: how can this be handled more cleanly without deleting data?
>

---

### ⭐ Additional Task

Currently, the date is only shown for new tasks—old tasks (from a file without `createdAt`) will show `null` and cause a formatting error.

Add a security check: if `createdAt` == null, show a dash or the text "date unknown" instead of the date.

Where is the best place to place this check: in `TaskRowPanel` or in the `Task` class itself?

---

## Solution:

## Task Creation Date — Step-by-Step Instructions

### What will change

Each task will store its creation date and time. A label like `03.06.2026 14:35` will appear next to the task text in the list.

Affected files:
- `Task.java` — adding the `createdAt` field
- `AppConstants.java` — adding the date format constant
- `util/LocalDateTimeAdapter.java` — a new class for Gson to work correctly with dates
- `JsonTaskRepository.java` — registering the adapter
- `TaskRowPanel.java` — displaying the date in the task row

---

### Step 1 — Adding the `createdAt` field to `Task.java`

Open `Task.java`. Add a field and populate it in the constructor:

```java
package com.example.myassistant.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
* Model of a single task in the list.
*/
public class Task {
    
    private String id;
    private String text;
    private boolean completed;
    private LocalDateTime createdAt; // ← added
    
    public Task(String text) {
    this.id = UUID.randomUUID().toString();
    this.text = text;
    this.completed = false;
    this.createdAt = LocalDateTime.now(); // ← added
    }
    
    public String getId() { return id; }
    public String getText() { return text; }
    public boolean isCompleted() { return completed; }
    public LocalDateTime getCreatedAt() { return createdAt; } // ← added
    
    public void setText(String text) { this.text = text; }
    public void setCompleted(boolean completed) { this.completed = completed; }
}
```

`LocalDateTime.now() captures the current date and time at the time the `Task` object was created.

---

### Step 2 — Add a constant to `AppConstants.java`

Open `AppConstants.java` and add a format constant:

```java
package com.example.myassistant.util;

/**
* Application constants — file paths, formats, URLs, etc.
*/
public class AppConstants {
    public static final String TASKS_FILE = "tasks.json";
    public static final String DATE_FORMAT = "dd.MM.yyyy HH:mm"; // ← added
}
```

The format `dd.MM.yyyy HH:mm` will produce a string like `03.06.2026 14:35`. We store it in a constant so that the same format is used in the UI and the adapter.

---

### Step 3 — Create `LocalDateTimeAdapter.java`

**Why is it needed?**

Gson, a library for working with JSON, cannot serialize `LocalDateTime` by default. If you don't add the adapter, the date will be written unreadably or even cause an error when saving the task to a file. **The adapter explains to Gson how to convert `LocalDateTime` to a string and back.**

In the `util` package, create a new class `LocalDateTimeAdapter.java`:

```java
package com.example.myassistant.util;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
* Adapter for Gson: teaches it to save and load LocalDateTime as a string.
*/
public class LocalDateTimeAdapter 
implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> { 
    
    private static final DateTimeFormatter FORMATTER = 
    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"); 
    
    @Override 
    public JsonElement serialize(LocalDateTime src, Type type, JsonSerializationContext ctx) { 
        // when saving: LocalDateTime → string 
        return new JsonPrimitive(src.format(FORMATTER)); 
    }
    
    @Override
    public LocalDateTime deserialize(JsonElement json, Type type, JsonDeserializationContext ctx) {
        // on load: string → LocalDateTime
        return LocalDateTime.parse(json.getAsString(), FORMATTER);
    }
}
```

- Explanation

# First, the problem

Let's say we have a class:

```java
public class Task {
    private String title;
    private LocalDateTime createdAt;
}
```

And an object:

```java
Task task = new Task(
    "Buy milk",
    LocalDateTime.now()
);
```

When Gson saves the object:

```java
gson.toJson(task);
```

It can work with simple types:

```java
String
int
boolean
List
```

But with `LocalDateTime` the situation is more complicated.

---

# What is serialization

Serialization =

```
Java object
↓
JSON string
```

For example:

```java
task
```

becomes:

```json
{
"title": "Buy milk",
"createdAt": "2026-06-03T14:20:15"
}
```

---

# What is deserialization

The reverse process:

```
JSON
↓
Java object
```

For example:

```json
{
"title": "Buy milk",
"createdAt": "2026-06-03T14:20:15"
}
```

becomes:

```java
Task task
```

where field

```java
createdAt
```

becomes a `LocalDateTime` object.

---

# Why do you need an adapter

Gson meets:

```java
LocalDateTime
```

and thinks:

> I don't know how to save it.
>

That's why we are writing a special "translator".

---

# What does it mean

```java
implements JsonSerializer<LocalDateTime>, 
JsonDeserializer<LocalDateTime>
```

We say to Gson:

> If you come across LocalDateTime, use me.
>

---

# Where do the serialize and deserialize methods come from?

They are declared in interfaces.

---

## JsonSerializer interface

Simplified it looks like this:

```java
public interface JsonSerializer<T> { 
    
    JsonElement serialize( 
        T src, 
        Type type 
        JsonSerializationContext context 
    );
}
```

For our case:

```java
T = LocalDateTime
```

so the method becomes:

```java
JsonElement serialize( 
    LocalDateTime src, 
    Type type 
    JsonSerializationContext context
);
```

---

## JsonDeserializer interface

Simplified:

```java
public interface JsonDeserializer<T> { 
    
    T deserialize( 
        JsonElement json, 
        Type type 
        JsonDeserializationContext context 
    );
}
```

After type substitution:

```java
LocalDateTime deserialize(...)
```

---

# Why @Override

Because we are required to implement interface methods.

The compiler checks:

```java
@Override
```

and makes sure that the method actually exists in the interface.

---

# How serialize works

Method:

```java
public JsonElement serialize( 
    LocalDateTime src, 
    Type type 
    JsonSerializationContext ctx
);
```

receives:

```java
src
```

For example:

```java
2026-06-03T14:20:15
```

Next:

```java
src.format(FORMATTER)
```

we get the line:

```java
"2026-06-03T14:20:15"
```

Then:

```java
new JsonPrimitive(...)
```

creates a JSON value.

Result:

```json
"2026-06-03T14:20:15"
```

---

# What is JsonPrimitive

This is a simple JSON element:

```json
"string"
```

or

```json
123
```

or

```json
true
```

---

# How deserialize works

Let's say we read from the file:

```json
"2026-06-03T14:20:15"
```

This piece comes here:

```java
JsonElement json
```

We get the line:

```java
json.getAsString()
```

result:

```java
"2026-06-03T14:20:15"
```

Then:

```java
LocalDateTime.parse(...)
```

creates an object:

```java
LocalDateTime
```

---

# How does Gson know about the adapter

There's usually code somewhere:

```java
Gson gson = new GsonBuilder() 
    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()) 
    .create();
```

Registration takes place here.

We say:

> For all objects of type LocalDateTime, use LocalDateTimeAdapter.

---

# What happens when saving

Let's say:

```java
gson.toJson(task);
```

Gson goes through the fields of the object:

```java
title
createdAt
```

For `title` it knows what to do.

For:

```java
createdAt
```

sees type:

```java
LocalDateTime
```

and says:

> I have an adapter registered.

Calls:

```java
serialize(...)
```

---

# What happens when loading

When executed:

```java
gson.fromJson(...)
```

Gson reads the field:

```json
"createdAt": "2026-06-03T14:20:15"
```

Sees what you need to get:

```java
LocalDateTime
```

and calls:

```java
deserialize(...)
```

---

# If you imagine the adapter is quite simple

It works as a translator:

```
LocalDateTime 
    ↓ 
serialize() 
    ↓
JSON string

JSON string 
    ↓ 
deserialize() 
    ↓
LocalDateTime
```

That is, the adapter does not store data and does not read the file. He only explains to Gson:

> "If you see a LocalDateTime, convert it like this."

Please note: the format in the adapter (`yyyy-MM-dd'T'HH:mm:ss`) is the **file storage format**. It is different from the **UI display format** (`dd.MM.yyyy HH:mm` from `AppConstants`). This is intentional - we use a standard ISO format in the file, and a user-readable format in the UI.

---

### Step 4 — Register the adapter in `JsonTaskRepository.java`

Open `JsonTaskRepository.java`. Find the line where `Gson` is created and add the adapter registration:

```java
// was
private final Gson gson = new GsonBuilder()
    .setPrettyPrinting()
    .create();

// now
private final Gson gson = new GsonBuilder()
    .setPrettyPrinting()
    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
    .create();
```

Add the import at the top of the file:

```java
import java.time.LocalDateTime;
```

Now, every time you save and load, Gson will use the adapter for the `createdAt` field.

---

### Step 5 — Displaying the Date in `TaskRowPanel.java`

Open `TaskRowPanel.java`. Add a date label next to the task text.

Find where `taskLabel` is created and add the following after it:

```java
// Format the date for display
DateTimeFormatter formatter = DateTimeFormatter.ofPattern(AppConstants.DATE_FORMAT);
JLabel dateLabel = new JLabel(task.getCreatedAt().format(formatter));
dateLabel.setForeground(Color.GRAY); // The date is grayed out so it doesn't distract from the text
```

Then add `dateLabel` to `leftPanel`, after `taskLabel`:

```java
JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
leftPanel.add(checkBox);
leftPanel.add(taskLabel);
leftPanel.add(dateLabel); // ← added
```

Add imports at the top of the file:

```java
import com.example.myassistant.util.AppConstants;
import java.time.format.DateTimeFormatter;
```

---

### What will happen in the end?

Each task line will look like this:

```
☐ Buy groceries 06/03/2026 2:35 PM [Edit] [✕]
☐ Complete a task 06/03/2026 3:10 PM [Edit] [✕]
✓ Call a friend 06/02/2026 9:00 AM [Edit] [✕]
```

---

### Verification

Launch the app and make sure:

- [ ] New tasks display the creation date and time
- [ ] Date is grayed out (does not distract from the text)
- [ ] Dates are saved after restarting the app
- [ ] Old tasks (created before the changes) load without errors

> **Attention:** if you already have saved tasks in `tasks.json` without the `createdAt` field - the field will be `null` when loaded. Simple solution: delete the `tasks.json` file before running the new code for the first time.
>

---

### Common Mistakes

- Forgetting to register the adapter in `JsonTaskRepository` - Gson will throw an exception when saving.
- Using the same format for storage and display - this works, but is a bad practice: the storage format should be machine-readable, and the display format should be human-readable.
- Not adding `dateLabel` to `leftPanel` - the label is created, but not visible on the screen.

---

---

## Task #3: Adding a Task Priority and Priority Filter

## Task: Task Priority

### What needs to be implemented

Each task should have a priority: **High**, **Medium**, or **Low**.

- When creating a task, the user selects a priority from the drop-down list next to the input field.
- A colored priority label is displayed next to the text in the task row.
- When editing a task, both the text and priority can be changed in a single dialog.
- The list can be filtered by priority (a second filter, independent of the status filter).
- Tasks with a higher priority are displayed at the top.

Affected files:
- `model/Priority.java` — new enum
- `model/Task.java` — add the `priority` field
- `service/TaskService.java` — change the `addTask` and `editTask` signatures, add `getTasksByPriority`
- `service/impl/TaskServiceImpl.java` — update implementations, update sorting
- `ui/panels/TaskRowPanel.java` — display the priority label
- `ui/panels/TaskPanel.java` — add two `JComboBoxes`: one for creation and one for filtering
- `controller/TaskController.java` — pass priority on creation, extend the editing dialog, enable a filter

---

### Before you begin, consider this:

1. How should the priority be stored in the model—as an int, a String, or an enum? Why is one option safer than the others?
2. The `addTask` method currently only accepts a String text. How can I change its signature to pass the priority?
3. We already have `editTask(String id, String newText)`. How can I extend it so that it also updates the priority without adding a separate method?
4. The priority filter for **creating** a task and the filter for **displaying**—are these two components? Why?

---

### Step 1 — Create a `Priority` enum in the `model` package

The enum should contain four values: `ALL`, `HIGH`, `MEDIUM`, `LOW`.

- `ALL` — only for the filter ("show all"); a task doesn't have such a priority.
- `HIGH`, `MEDIUM`, `LOW` — the actual task priorities.

Each value stores a string to display in the UI and a numeric weight (0 for ALL, 1–3 for others).

Override `toString()` and add a getter for the numeric value.

> **Hint:** You've already seen an enum structure with two fields when filtering. The same thing applies here:
>
>
> ```java
> HIGH(1, "High"),
> // ...
> private final int value;
> private final String displayName;
> ```
>

---

### Step 2 — Add a `priority` field to `Task.java`

Add a `Priority` field and a setter. In the constructor, set the default value to `MEDIUM`.

> **Important:** Existing tasks in `tasks.json` do not have a `priority` field — Gson will set it to `null`. Handle this in the getter:
>
>
> ```java
> public Priority getPriority() {
> return priority != null ? priority : Priority.MEDIUM;
> }
> ```
>

---

### Step 3 — Update `addTask` and `editTask` in the service

**`addTask`** must accept a priority as the second parameter:

```java
void addTask(String text, Priority priority);
```

**`editTask`** — extend the existing method by adding a third parameter:

```java
void editTask(String id, String newText, Priority priority);
```

Update the signatures in the `TaskService` interface and the `TaskServiceImpl` implementation.

In `TaskServiceImpl`:
- `addTask` — creates a task and sets its priority before saving
- `editTask` — updates both the text and priority in a single `ifPresent`

> **Hint:** You can call two methods in a row in `ifPresent`:
>
>
> ```java
> .ifPresent(t -> {
> t.setText(newText);
> t.setPriority(priority);
> });
> ```
>

Also add to the interface and implement:

```java
List<Task> getTasksByPriority(Priority filter);
```

If `filter == Priority.ALL` — return all tasks; otherwise — filter by priority.

---

### Step 4 — Update sorting in `getAllTasks()`

Add a secondary sort by priority — among uncompleted tasks, high priority ones should be higher.

> **Hint:** Replace `stream().sorted().collect()` with a simpler notation:
>
>
> ```java
> List<Task> result = new ArrayList<>(tasks);
> result.sort(Comparator.comparing(Task::isCompleted)
> .thenComparingInt(...));
> return result;
> ```
>
> `thenComparingInt` is a method from `Comparator` in Java that adds a **second level of sorting (tie-breaker)** by `int`.
>
> If two tasks have the same completion status, they are additionally sorted by priority.
>

---

### Step 5 — Display the priority in `TaskRowPanel.java`

Add a `JLabel` with a priority color:
- High — red
- Medium — orange
- Low — gray

Add the label to `leftPanel` next to the text.

> **Hint:** Determine the color using the `switch` expression for `task.getPriority()`. For a custom color, use `new Color(r, g, b)`.
>

---

### Step 6 - Add two `JComboBox` to the form and `TaskPanel.java`

Open `TaskPanel.form` in GUI Designer and add two new `JComboBox`:

1. **`newTaskPriorityBox`** - in `topPanel` next to the input field and the “Add” button. Without the `ALL` value - when creating a task, the priority must be specific.
2. **`priorityFilterBox`** - separately, next to `filterBox`. With all values ​​including `ALL`.

In `TaskPanel.java` for each:
- Add a typed field
- Fill in the constructor with values (for `newTaskPriorityBox` - without `ALL`)
- Add a getter

> **Hint:** to exclude `ALL` when filling:
>
>
> ```java
> for (Priority p : Priority.values()) {
> if (p != Priority.ALL) newTaskPriorityBox.addItem(p);
> }
> ```
>

---

### Step 7 - Update `TaskController`

**Creating a task** - read the selected priority from the `newTaskPriorityBox` and pass it to `addTask`:

```java
Priority priority = (Priority) taskPanel.getNewTaskPriorityBox().getSelectedItem();
taskService.addTask(text, priority);
```

**Editing** - Replace the existing `JOptionPane.showInputDialog` with a dialog with two fields: text and priority. Call the updated `editTask` with three parameters.

> **Hint:** `JOptionPane.showConfirmDialog` accepts any `Component`:
>
>
> ```java
> JPanel panel = new JPanel(new GridLayout(2, 2, 8, 8));
> JTextField textField = new JTextField(task.getText());
> JComboBox<Priority> priorityBox = new JComboBox<>();
> // fill without ALL...
> panel.add(new JLabel("Text:")); panel.add(textField);
> panel.add(new JLabel("Priority:")); panel.add(priorityBox);
> int result = JOptionPane.showConfirmDialog(
> taskPanel.getMainPanel(), panel, "Edit task",
> JOptionPane.OK_CANCEL_OPTION);
> ```
>

**Filtering** - in `init()` add a listener on `priorityFilterBox`. In `refreshTaskList()` apply both filters:

```java
List<Task> tasksToShow = taskService.getTasksByFilter(statusFilter).stream() 
.filter(t -> priorityFilter == Priority.ALL || t.getPriority() == priorityFilter) 
.collect(Collectors.toList());
```

---

### Check

- [ ] When creating a task, a drop-down list of priorities is visible next to the input field
- [ ] The new task receives the selected priority (not always “Medium”)
- [ ] A colored priority mark is visible in the task line
- [ ] The “Edit” dialog shows both text and priority (without “All”)
- [ ] After editing, the priority label is updated
- [ ] Tasks with high priority are ranked higher among the outstanding ones
- [ ] The priority filter works regardless of the status filter
- [ ] Both filters together: “Active” + “High” show only the necessary tasks
- [ ] Old tasks from `tasks.json` are loaded without errors

---

### ⭐ Additional task

Now after creating a task, `newTaskPriorityBox` remains at the last selected value. Reset it to `MEDIUM` along with clearing the text field.

Think: should this behavior be in the controller or in the `TaskPanel` itself?

---

## Solution:

### Step 1 — Create `Priority.java`

Create a new file in the `model` package. Type — **Enum**.

```java
package com.example.myassistant.model;

/**
* Task priority. ALL is used only in the filter — tasks do not have this value.
*/
public enum Priority {
    ALL(0, "All"),
    HIGH(1, "High"),
    MEDIUM(2, "Medium"),
    LOW(3, "Low");
    
    private final int value;
    private final String displayName;
    
    Priority(int value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }
    
    public int getValue() { return value; }
    
    @Override
    public String toString() { return displayName; }
}
```

---

### Step 2 - Add `priority` to `Task.java`

```java
package com.example.myassistant.model;

import java.time.LocalDateTime;
import java.util.UUID;

/** 
* Model of one task in a list. 
*/
public class Task { 
    
    private String id; 
    private String text; 
    private boolean completed; 
    private LocalDateTime createdAt; 
    private Priority priority; 
    
    public Task(String text) { 
        this.id = UUID.randomUUID().toString(); 
        this.text = text; 
        this.completed = false; 
        this.createdAt = LocalDateTime.now(); 
        this.priority = Priority.MEDIUM; 
    } 
    
    public String getId() { return id; } 
    public String getText() { return text; } 
    public boolean isCompleted() { return completed; } 
    public LocalDateTime getCreatedAt() { return createdAt; } 
    
    // protection against null: old tasks from a file without the priority field will be loaded without errors 
    public Priority getPriority() { 
        return priority != null ? priority : Priority.MEDIUM; 
    } 
    
    public void setText(String text) { this.text = text; } 
    public void setCompleted(boolean completed) { this.completed = completed; } 
    public void setPriority(Priority priority) { this.priority = priority; }
}
```

---

### Step 3 — Updating `TaskService`

Extend the `addTask` and `editTask` signatures — the priority is now passed immediately, eliminating the need for a separate `setPriority` method.

```java
package com.example.myassistant.service;

import com.example.myassistant.model.FilterType;
import com.example.myassistant.model.Priority;
import com.example.myassistant.model.Task;

import java.util.List;

/**
* Task management business logic.
*/
public interface TaskService {
    
    void addTask(String text, Priority priority); // ← changed
    void editTask(String id, String newText, Priority priority); // ← changed 
    void deleteTask(String id); 
    void toggleCompleted(String id); 
    void deleteCompleted(); 
    void clearAll(); 
    List<Task> getAllTasks(); 
    List<Task> getTasksByFilter(FilterType filter); 
    List<Task> getTasksByPriority(Priority filter); // ← added
}
```

---

### Step 4 — Update `TaskServiceImpl`

```java
package com.example.myassistant.service.impl;

import com.example.myassistant.model.FilterType;
import com.example.myassistant.model.Priority;
import com.example.myassistant.model.Task;
import com.example.myassistant.repository.TaskRepository;
import com.example.myassistant.service.TaskService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
* Implementation of the task service. Stores the list in memory and synchronizes with the repository. */
public class TaskServiceImpl implements TaskService { 
    
    private final TaskRepository repository; 
    private final List<Task> tasks; 
    
    public TaskServiceImpl(TaskRepository repository) { 
        this.repository = repository; 
        this.tasks = new ArrayList<>(repository.load()); 
    } 
    
    @Override 
    public void addTask(String text, Priority priority) { 
        Task task = new Task(text); 
        task.setPriority(priority); 
        tasks.add(task); 
        repository.save(tasks); 
    } 
    
    @Override 
    public void editTask(String id, String newText, Priority priority) { 
        tasks.stream() 
        .filter(t -> t.getId().equals(id)) 
        .findFirst() 
        .ifPresent(t -> {t.setText(newText); t.setPriority(priority);}); 
        repository.save(tasks); 
    } 
    
    @Override 
    public void deleteTask(String id) { 
        tasks.removeIf(t -> t.getId().equals(id)); 
        repository.save(tasks); 
    } 
    
    @Override 
    public void toggleCompleted(String id) { 
    tasks.stream() 
        .filter(t -> t.getId().equals(id)) 
        .findFirst() 
        .ifPresent(t -> t.setCompleted(!t.isCompleted())); 
        repository.save(tasks); 
    } 
    
    @Override 
    public void deleteCompleted() { 
        tasks.removeIf(Task::isCompleted); 
        repository.save(tasks); 
    } 
    
    @Override 
    public void clearAll() { 
        tasks.clear(); 
        repository.save(tasks); 
    } 
    
    @Override 
    public List<Task> getAllTasks() { 
        List<Task> result = new ArrayList<>(tasks); 
        result.sort(Comparator.comparing(Task::isCompleted)
        .thenComparingInt(t -> t.getPriority().getValue())); //<--If two tasks have the same completion status, they are additionally sorted by priority.
        return result;
    }
    
    @Override
    public List<Task> getTasksByFilter(FilterType filter) {
        return switch (filter) {
            case COMPLETED -> tasks.stream()
            .filter(Task::isCompleted)
            .collect(Collectors.toList());
            case PENDING -> tasks.stream()
            .filter(t -> !t.isCompleted())
            .collect(Collectors.toList());
            default -> getAllTasks();
        };
    }
    
    //!!!
    @Override 
    public List<Task> getTasksByPriority(Priority filter) { 
        if (filter == Priority.ALL) return getAllTasks(); 
        return getAllTasks().stream() 
        .filter(t -> t.getPriority() == filter) 
        .collect(Collectors.toList()); 
    }
}
```

---

### Step 5 — Displaying the priority in `TaskRowPanel.java`

```java
package com.example.myassistant.ui.panels;

import com.example.myassistant.model.Priority;
import com.example.myassistant.model.Task;
import com.example.myassistant.util.AppConstants;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;

/**
* Single task row panel: checkbox, priority, text, date, buttons.
  */
  public class TaskRowPanel extends JPanel {
    
    private final Task task;
    private final JCheckBox checkBox;
    private final JLabel taskLabel;
    private final JButton editButton;
    private final JButton deleteButton;
    
    public TaskRowPanel(Task task) {
        this.task = task;
        
        setLayout(new BorderLayout());
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        
        checkBox = new JCheckBox();
        checkBox.setSelected(task.isCompleted());
        
        taskLabel = new JLabel(task.getText());
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(AppConstants.DATE_FORMAT);
        JLabel dateLabel = new JLabel(task.getCreatedAt().format(formatter));
        dateLabel.setForeground(Color.GRAY);
        
        //!!!
        JLabel priorityLabel = new JLabel(task.getPriority().toString());
        Color priorityColor = switch (task.getPriority()) {
            case HIGH -> new Color(200, 50, 50);
            case MEDIUM -> new Color(200, 130, 0);
            default -> Color.GRAY;
        };
        priorityLabel.setForeground(priorityColor);
        
        if (task.isCompleted()) {
            taskLabel.setText("<html><strike>" + task.getText() + "</strike></html>");
            taskLabel.setForeground(Color.GRAY);
        }
        
        editButton = new JButton("Edit");
        deleteButton = new JButton("✕");
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        leftPanel.add(checkBox);
        leftPanel.add(priorityLabel);//<-- added
        leftPanel.add(taskLabel);
        leftPanel.add(dateLabel);
        
        add(leftPanel, BorderLayout.WEST);
        add(buttonPanel, BorderLayout.EAST);
    }
    
    public Task getTask() { return task; }
    public JCheckBox getCheckBox() { return checkBox; }
    public JButton getEditButton() { return editButton; }
    public JButton getDeleteButton() { return deleteButton; }
}
```

---

### Step 6 — Add two JComboBoxes to the form and TaskPanel.java

**In the GUI Designer:**

Add two new JComboBoxes:

1. **`newTaskPriorityBox`** — in the `topPanel`, between the `taskInputField` and `addButton`
2. **`priorityFilterBox`** — separately, next to the `filterBox`

Component tree:

```
mainPanel
├── topPanel
│    ├── taskInputField
│    ├── newTaskPriorityBox ← new
│    └── addButton
├── filterBox
├── priorityFilterBox ← new
├── JScrollPane
│    └── taskListPanel
├── buttonsPanel
└── statsPanel
```

**`TaskPanel.java`:**

```java
package com.example.myassistant.ui.panels;

import com.example.myassistant.model.FilterType;
import com.example.myassistant.model.Priority;

import javax.swing.*;

public class TaskPanel { 
    private JPanel mainPanel; 
    private JPanel topPanel; 
    private JTextField taskInputField; 
    private JComboBox<Priority> newTaskPriorityBox; // ← new 
    private JButton addButton; 
    private JPanel taskListPanel; 
    private JPanel buttonsPanel; 
    private JButton deleteCompletedButton; 
    private JButton clearAllButton; 
    private JPanel statsPanel; 
    private JLabel totalLabel; 
    private JLabel completedLabel; 
    private JLabel pendingLabel; 
    private JLabel progressLabel; 
    private JComboBox<FilterType> filterBox; 
    private JComboBox<Priority> priorityFilterBox; // ← new 
    
    public TaskPanel() { 
        taskListPanel.setLayout(new BoxLayout(taskListPanel, BoxLayout.Y_AXIS)); 
        
        for (FilterType type : FilterType.values()) { 
            filterBox.addItem(type); 
        }
        
        // Filter: all values, including ALL
        for (Priority p : Priority.values()) {
            priorityFilterBox.addItem(p);
        }
        
        // ALL is not needed when creating a task
        for (Priority p : Priority.values()) {
            if (p != Priority.ALL) newTaskPriorityBox.addItem(p);
        }
    
    }
    
    public JPanel getMainPanel() { return mainPanel; }
    public JTextField getTaskInputField() { return taskInputField; }
    public JComboBox<Priority> getNewTaskPriorityBox() { return newTaskPriorityBox; }
    public JButton getAddButton() { return addButton; }
    public JPanel getTaskListPanel() { return taskListPanel; } 
    public JButton getDeleteCompletedButton() { return deleteCompletedButton; } 
    public JButton getClearAllButton() { return clearAllButton; } 
    public JLabel getTotalLabel() { return totalLabel; } 
    public JLabel getCompletedLabel() { return completedLabel; } 
    public JLabel getPendingLabel() { return pendingLabel; } 
    public JLabel getProgressLabel() { return progressLabel; } 
    public JComboBox<FilterType> getFilterBox() { return filterBox; } 
    public JComboBox<Priority> getPriorityFilterBox() { return priorityFilterBox; }
}
```

---

### Step 7 — Update `TaskController`

```java
package com.example.myassistant.controller;

import com.example.myassistant.model.FilterType;
import com.example.myassistant.model.Priority;
import com.example.myassistant.model.Task;
import com.example.myassistant.service.TaskService;
import com.example.myassistant.ui.panels.TaskPanel;
import com.example.myassistant.ui.panels.TaskRowPanel;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

/**
* Connects the task UI with the business logic. Handles all user actions.
*/
public class TaskController {

private final TaskService taskService;
private final TaskPanel taskPanel;

public TaskController(TaskPanel taskPanel, TaskService taskService) {
        this.taskService = taskService;
        this.taskPanel = taskPanel;
        init();
    }
    
    private void init() {
        taskPanel.getAddButton().addActionListener(e -> addTask());
        taskPanel.getTaskInputField().addActionListener(e -> addTask());
        taskPanel.getDeleteCompletedButton().addActionListener(e -> deleteCompleted());
        taskPanel.getClearAllButton().addActionListener(e -> clearAll());
        taskPanel.getFilterBox().addActionListener(e -> refreshTaskList()); 
        taskPanel.getPriorityFilterBox().addActionListener(e -> refreshTaskList()); 
        
        refreshTaskList(); 
    } 
    
    private void addTask() { 
        String text = taskPanel.getTaskInputField().getText().trim(); 
        if (text.isEmpty()) return;
        
        //!!! 
        Priority priority = (Priority) taskPanel.getNewTaskPriorityBox().getSelectedItem(); 
        taskService.addTask(text, priority); 
        
        taskPanel.getTaskInputField().setText(""); 
        
        //!!!
        
        taskPanel.getNewTaskPriorityBox().setSelectedItem(Priority.MEDIUM); // reset after adding
        refreshTaskList();
    }
    
    private void deleteCompleted() {
        int confirm = JOptionPane.showConfirmDialog(
        taskPanel.getMainPanel(),
        "Delete all completed tasks?",
        "Confirm",
        JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            taskService.deleteCompleted();
            refreshTaskList();
        }
    }
    
    private void clearAll() {
        int confirm = JOptionPane.showConfirmDialog(
        taskPanel.getMainPanel(),
        "Clear the entire task list?",
        "Confirm",
        JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) { 
            taskService.clearAll(); 
            refreshTaskList(); 
        } 
    } 
    
    private void refreshTaskList() { 
        taskPanel.getTaskListPanel().removeAll();
        
        //!!! 
        FilterType statusFilter = (FilterType) taskPanel.getFilterBox().getSelectedItem(); 
        Priority priorityFilter = (Priority) taskPanel.getPriorityFilterBox().getSelectedItem(); 
        
        List<Task> tasksToShow = taskService.getTasksByFilter(statusFilter).stream() 
        .filter(t -> priorityFilter == Priority.ALL || t.getPriority() == priorityFilter) 
        .collect(Collectors.toList()); 
        
        for (Task task : tasksToShow) { 
        TaskRowPanel row = new TaskRowPanel(task); 
        
        row.getCheckBox().addActionListener(e -> { 
            taskService.toggleCompleted(task.getId()); 
            refreshTaskList(); 
        }); 
        
        row.getEditButton().addActionListener(e -> editTask(task)); 
        
        row.getDeleteButton().addActionListener(e -> { 
            int confirm = JOptionPane.showConfirmDialog( 
            taskPanel.getMainPanel(), 
            "Delete task\"" + task.getText() + "\"?", 
            "Confirmation", 
            JOptionPane.YES_NO_OPTION); 
            if (confirm == JOptionPane.YES_OPTION) { 
                taskService.deleteTask(task.getId()); 
                refreshTaskList(); 
            } 
        }); 
        
        taskPanel.getTaskListPanel().add(row); 
        } 
        
        taskPanel.getTaskListPanel().revalidate(); 
        taskPanel.getTaskListPanel().repaint(); 
        updateStats(); 
    } 
    
    private void editTask(Task task) { 
        JTextField textField = new JTextField(task.getText()); 
        
        JComboBox<Priority> priorityBox = new JComboBox<>(); 
        for (Priority p : Priority.values()) { 
            if (p != Priority.ALL) priorityBox.addItem(p); 
        } 
        priorityBox.setSelectedItem(task.getPriority()); 
        
        JPanel panel = new JPanel(new GridLayout(2, 2, 8, 8)); 
        panel.add(new JLabel("Text:")); 
        panel.add(textField); 
        panel.add(new JLabel("Priority:")); 
        panel.add(priorityBox); 
        
        int result = JOptionPane.showConfirmDialog( 
        taskPanel.getMainPanel(), 
        panel 
        "Edit task" 
        JOptionPane.OK_CANCEL_OPTION); 
        
        if (result == JOptionPane.OK_OPTION) { 
            String newText = textField.getText().trim(); 
            if (!newText.isEmpty()) { 
                taskService.editTask( 
                task.getId(), 
                newText, 
                (Priority) priorityBox.getSelectedItem()); 
                refreshTaskList(); 
            } 
        } 
    } 
    
    private void updateStats() { 
        List<Task> all = taskService.getAllTasks(); 
        long completed = all.stream().filter(Task::isCompleted).count(); 
        long pending = all.size() - completed; 
        int progress = all.isEmpty() ? 0 : (int) (completed * 100 / all.size()); 
        
        taskPanel.getTotalLabel().setText(String.valueOf(all.size())); 
        taskPanel.getCompletedLabel().setText(String.valueOf(completed)); 
        taskPanel.getPendingLabel().setText(String.valueOf(pending)); 
        taskPanel.getProgressLabel().setText(progress + "%"); 
    }
}
```

---

### Test

1. Launch the app
2. Ensure there is a priority drop-down list next to the input field
3. Create tasks with different priorities - the labels should be different colors
4. Check the sorting: "High" is listed above "Medium" and "Low"
5. Click "Edit" - the dialog displays the text and priority
6. Change the priority - the label in the row is updated
7. Test both filters separately and together
8. Restart - the priorities are saved

---

### What we used in this task

| Concept | Where we used |
| --- | --- |
| `enum` with multiple fields | `Priority` — numeric weight and name |
| Defensive getter | `getPriority()` — `null` → `MEDIUM` for old tasks |
| Method signature extension | `addTask` and `editTask` now accept `Priority` |
| Secondary sorting | `Comparator.comparing(...).thenComparingInt(...)` |
| Sequential filtering | Status → Priority in `refreshTaskList()` |
| Custom dialog | `JOptionPane` + `JPanel` with `GridLayout` |
| `switch` expression | Label color by priority in `TaskRowPanel` |

---
