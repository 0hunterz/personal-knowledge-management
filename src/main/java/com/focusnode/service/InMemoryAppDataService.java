package com.focusnode.service;

import com.focusnode.model.CategoryBreakdown;
import com.focusnode.model.FocusPoint;
import com.focusnode.model.Note;
import com.focusnode.model.TagUsageItem;
import com.focusnode.model.Task;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class InMemoryAppDataService implements AppDataService {

    private final List<Task> tasks;
    private final List<Note> notes;
    private final List<TagUsageItem> tagUsage;
    private final List<FocusPoint> weeklyFocus;
    private final List<CategoryBreakdown> categoryBreakdown;
    private final List<com.focusnode.model.FocusSession> focusSessions;
    private final List<com.focusnode.model.ReviewItem> reviewItems;

    public InMemoryAppDataService() {
        this.tasks = new ArrayList<>(List.of(
                new Task(1, "Finish reading Markdown guide", "", "Study", Task.Status.COMPLETED, Task.Priority.HIGH, LocalDate.now().plusDays(0), 60, 60, -1, "General"),
                new Task(2, "Build LAN connection module", "", "Project", Task.Status.COMPLETED, Task.Priority.HIGH, LocalDate.now().minusDays(1), 90, 90, -1, "General"),
                new Task(3, "Review knowledge base UI", "", "Review", Task.Status.IN_PROGRESS, Task.Priority.MEDIUM, LocalDate.now().plusDays(0), 45, 20, -1, "General"),
                new Task(4, "Study Java Socket programming", "", "Study", Task.Status.IN_PROGRESS, Task.Priority.HIGH, LocalDate.now().plusDays(1), 120, 0, -1, "General"),
                new Task(5, "Prepare project presentation", "", "Work", Task.Status.PENDING, Task.Priority.MEDIUM, LocalDate.now().plusDays(2), 60, 0, -1, "General"),
                new Task(6, "Exercise 30 minutes", "", "Health", Task.Status.PENDING, Task.Priority.LOW, LocalDate.now().plusDays(2), 30, 0, -1, "General"),
                new Task(7, "Read AI research papers", "", "Study", Task.Status.PENDING, Task.Priority.MEDIUM, LocalDate.now().plusDays(3), 50, 0, -1, "General"),
                new Task(8, "Write daily journal", "", "Personal", Task.Status.PENDING, Task.Priority.LOW, LocalDate.now().plusDays(3), 20, 0, -1, "General")
        ));

        this.notes = new ArrayList<>(List.of(
                new Note(1, "Java Socket Programming", "Notes about TCP/IP, ServerSocket, Socket, and data communication...", "", List.of("#java", "#network"), "Tech", "Intermediate", "Reviewed", LocalDate.now().minusDays(0)),
                new Note(2, "TCP vs UDP", "Comparison of TCP and UDP protocols with use cases.", "", List.of("#network", "#study"), "Tech", "Beginner", "Needs Review", LocalDate.now().minusDays(1)),
                new Note(3, "Focus System Mindmap", "Visual map of my productivity and focus system.", "", List.of("#study", "#productivity"), "Personal", "Advanced", "Reviewed", LocalDate.now().minusDays(2)),
                new Note(4, "Java NIO Basics", "Understanding Non-blocking I/O in Java with examples.", "", List.of("#java", "#advanced"), "Tech", "Intermediate", "Needs Review", LocalDate.now().minusDays(3)),
                new Note(5, "LAN Focus Hub Roadmap", "Roadmap and milestones for LAN Focus Hub module.", "", List.of("#project", "#network"), "Project", "Advanced", "Reviewed", LocalDate.now().minusDays(4)),
                new Note(6, "Pomodoro Technique Guide", "Detailed guide to using Pomodoro for deep work.", "", List.of("#study", "#focus"), "Personal", "Beginner", "Reviewed", LocalDate.now().minusDays(5))
        ));

        this.tagUsage = List.of(
                new TagUsageItem("Study", 28, "#10B981"),
                new TagUsageItem("Project", 24, "#3B82F6"),
                new TagUsageItem("Java", 20, "#F59E0B"),
                new TagUsageItem("Network", 16, "#8B5CF6"),
                new TagUsageItem("Others", 12, "#CBD5E1")
        );

        this.weeklyFocus = List.of(
                new FocusPoint("Mon", 200),
                new FocusPoint("Tue", 255),
                new FocusPoint("Wed", 330),
                new FocusPoint("Thu", 365),
                new FocusPoint("Fri", 290),
                new FocusPoint("Sat", 170),
                new FocusPoint("Sun", 175)
        );

        this.categoryBreakdown = List.of(
                new CategoryBreakdown("Study", 45, "#10B981"),
                new CategoryBreakdown("Project", 30, "#60A5FA"),
                new CategoryBreakdown("Reading", 15, "#A78BFA"),
                new CategoryBreakdown("Other", 10, "#FBBF24")
        );

        this.focusSessions = new ArrayList<>();
        this.reviewItems = new ArrayList<>();
    }

    @Override
    public String getUserName() {
        return "User";
    }

    @Override
    public String getGreetingMessage() {
        int hour = LocalTime.now().getHour();
        if (hour < 12) {
            return "Good morning, " + getUserName() + "! 👋";
        } else if (hour < 18) {
            return "Good afternoon, " + getUserName() + "! 👋";
        } else {
            return "Good evening, " + getUserName() + "! 👋";
        }
    }

    @Override
    public List<Task> getTasks() {
        return tasks;
    }

    @Override
    public void saveTask(Task task) {
        if (!tasks.contains(task)) {
            tasks.add(task);
        }
    }

    @Override
    public void updateTask(Task task) {
        int index = tasks.indexOf(task);
        if (index != -1) {
            tasks.set(index, task);
        }
    }

    @Override
    public void deleteTask(Task task) {
        tasks.remove(task);
    }

    @Override
    public List<Note> getNotes() {
        return notes;
    }

    @Override
    public void saveNote(Note note) {
        if (!notes.contains(note)) {
            notes.add(note);
        }
    }

    @Override
    public void updateNote(Note note) {
        int index = notes.indexOf(note);
        if (index != -1) {
            notes.set(index, note);
        }
    }

    @Override
    public void deleteNote(Note note) {
        notes.remove(note);
    }

    @Override
    public List<TagUsageItem> getTagUsage() {
        return tagUsage;
    }

    @Override
    public List<FocusPoint> getWeeklyFocus() {
        return weeklyFocus;
    }

    @Override
    public List<CategoryBreakdown> getCategoryBreakdown() {
        return categoryBreakdown;
    }

    @Override
    public List<com.focusnode.model.FocusSession> getFocusSessions() {
        return focusSessions;
    }

    @Override
    public void saveFocusSession(com.focusnode.model.FocusSession session) {
        if (!focusSessions.contains(session)) {
            focusSessions.add(session);
        }
    }

    @Override
    public List<com.focusnode.model.ReviewItem> getReviewItems() {
        return reviewItems;
    }

    @Override
    public void saveReviewItem(com.focusnode.model.ReviewItem item) {
        if (!reviewItems.contains(item)) {
            reviewItems.add(item);
        }
    }
}
