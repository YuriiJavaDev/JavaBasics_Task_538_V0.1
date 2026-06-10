## This is a logical continuation of the previous task, JavaBasics_Task_537_V0.1.

### 1. Here you need to add the ability to write to a JSON file.

### 2. Additionally, add the ability to click buttons not only with the mouse but also with the Enter key.

#### Also add a warning dialog box if the Delete key is pressed.

### 3. Add a unique ID for each task.
### 4. Add the task creation date and time.
### 5. Add a priority setting for each task at creation (High - red text, Medium - orange, Low - gray by default).
### 6. Add task filters.

#### First level of filtering:
#### = Ability to filter tasks by status:
- All - show all tasks
- Active - only uncompleted tasks
- Completed - only completed tasks

#### Next level of filtering:
#### = Ability to filter tasks by priority:
- All, High, Medium, or Low.

So, here's the rule: first filter tasks by the first level, then by the second.
So, when we select, for example, "High" from the drop-down list (Enames), we first look at what happens with the first-level filter. If it's "Active," for example, then we filter out tasks with a High level within the already filtered Active status.

### 7. Add logging to the Logger (SLF4J) project.

#### All key actions (button clicks, creation, editing, deletion, various errors, etc.) must be recorded and written to the logger.txt file.
#### In the log line, the first phrase is the importance (status), the second is the date, the third phrase is where to find it in the code, and then the description of the action or error.

### 8. To complete steps 1 and 8, you must connect the project to Maven.
