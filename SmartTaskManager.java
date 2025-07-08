import java.util.*;
import java.time.*;

public class SmartTaskManager {
    public static void main(String[] args) {
        Scanner scn = new Scanner(System.in);
        while (true) {
            System.out.println("1-Add Task");
            System.out.println("2-Complete Task");
            System.out.println("3-Undo");
            System.out.println("4-List Completed");
            System.out.println("5-Check Reminders");
            System.out.println("6-List by category");
            System.out.println("0-Exit");
            System.out.println("Select an option");
            int choice = scn.nextInt();
            scn.nextLine();
            switch (choice) {
                case 1:
                    System.out.println("Please enter your tasks name to add");
                    String title = scn.nextLine();
                    System.out.println("Please enter  description  to add");
                    String description = scn.nextLine();
                    System.out.println("Please enter priority (1-5)  to add");
                    int priority = scn.nextInt();
                    scn.nextLine();
                    System.out.println("Please enter task category");
                    String category = scn.nextLine();
                    System.out.println("Please due date (yyyy-MM-dd):");
                    String datePart = scn.nextLine();
                    System.out.println(" Enter due time (HH:mm):");
                    String timePart = scn.nextLine();
                    LocalDateTime dueDate = LocalDateTime.parse(datePart + "T" + timePart);
                    Task newTask = new Task(title, description, priority, category, dueDate);
                    addTask(newTask);
                    break;
                case 2:
                    System.out.println("Please enter tasks title to complete");
                    String titletocomplete = scn.nextLine();
                    if (titletocomplete.trim().isEmpty()) {
                        System.out.println(" Title cannot be empty.");
                    } else {
                        completeTask(titletocomplete);
                    }
                    break;
                case 3:
                    System.out.println(" Trying to undo the last completed task...");
                    undoLastCompletion();
                    break;
                case 4:
                    System.out.println("Listing all completed tasks:");
                    listCompletedTasks();
                    break;
                case 5:
                    checkReminders();
                    break;
                case 6:
                    System.out.println("Please enter a tasks category: ");
                    String catecogrytoList = scn.nextLine();
                    listTasksByCategory(catecogrytoList);
                    break;
                case 0:
                    System.out.println("Exiting the system...");
                    return;
                default:
                    System.out.println("Please enter a valid option (0-6)");
            }
        }
    }

    static class Task implements Comparable<Task> {
        String title;
        String description;
        int priority;
        String category;
        LocalDateTime dueDate;
        boolean isCompleted;

        public Task(String title, String description, int priority, String category, LocalDateTime localDate) {
            this.title = title;
            this.description = description;
            this.priority = priority;
            this.category = category;
            this.dueDate = localDate;
            this.isCompleted = false;
        }

        public void markAsCompleted() {
            this.isCompleted = true;
        }

        public void undoCompletion() {
            this.isCompleted = false;
        }

        @Override
        public int compareTo(Task other) {
            return Integer.compare(other.priority, this.priority);
        }

        @Override
        public String toString() {
            return "[" + "IsCompleted:" + isCompleted + "]" + "[Ttitle" + title + "]" + "[Priority: " + priority + "]"
                    + "[Category" + category
                    + "]" + "Due: " + dueDate.toString() + "]";
        }
    }

    static PriorityQueue<Task> activeTask = new PriorityQueue<>();
    static LinkedList<Task> completedTask = new LinkedList<>();
    static Stack<Task> undoStack = new Stack<>();
    static Queue<Task> reminderQueue = new LinkedList<>();
    static HashMap<String, LinkedList<Task>> categoryMap = new HashMap<>();

    public static void addTask(Task task) {
        if (task.dueDate.isBefore(LocalDateTime.now().plusDays(1))) {
            reminderQueue.offer(task);
        }
        activeTask.add(task);
        if (!categoryMap.containsKey(task.category)) {
            categoryMap.put(task.category, new LinkedList<>());
        }
        categoryMap.get(task.category).add(task);
        System.out.println("Task is appllied: " + task.title);
    }

    public static void completeTask(String title) {
        Task found = null;
        for (Task task : activeTask) {
            if (task.title.equalsIgnoreCase(title) && !task.isCompleted) {
                found = task;
                break;
            }
        }
        if (found != null) {
            activeTask.remove(found);
            found.markAsCompleted();
            completedTask.add(found);
            undoStack.push(found);
            System.out.println("The task is completed: " + found.title);
        } else {
            System.out.println("There no this task or task was completed before:" + found.title);
        }
    }

    public static void listCompletedTasks() {
        System.out.println("Tasks which are completed:");
        for (Task completedTaskk : completedTask) {
            System.out.println(completedTaskk);
        }
    }

    public static void undoLastCompletion() {
        if (!undoStack.isEmpty()) {
            Task lasttask = undoStack.pop();
            lasttask.isCompleted = false;
            completedTask.remove(lasttask);
            activeTask.add(lasttask);
            System.out.println("The task is added again : " + lasttask.title);
        } else {
            System.out.println("There no task to undo.");
        }
    }

    public static void listTasksByCategory(String category) {
        System.out.println("Tasks in category: " + category);
        LinkedList<Task> tasks = categoryMap.get(category);
        if (tasks == null || tasks.isEmpty()) {
            System.out.println("No tasks found in this category");
        } else {
            for (Task task : tasks) {
                System.out.println(task.title);
            }
        }
    }

    public static void checkReminders() {
        System.out.println("Upcoming Tasks (due in 24 hours: )");
        boolean found = false;
        for (Task tasks : reminderQueue) {
            if (!tasks.isCompleted) {
                System.out.println(tasks.title + " Due: " + tasks.dueDate);
                found = true;
            }
        }
        if (!found) {
            System.out.println("No upcoming task within 24 hpurs");
        }
    }
}