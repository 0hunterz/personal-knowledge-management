import urllib.request
import os

icons = {
    "Home_new.png": ("home", "4F46E5"),
    "Knowledge_new.png": ("book", "10B981"),
    "LAN-Hub_new.png": ("network", "3B82F6"),
    "Tasks_new.png": ("todo-list", "F59E0B"),
    "Focus_new.png": ("meditation-guru", "8B5CF6"),
    "Tags_new.png": ("tags", "EC4899"),
    "icon_achievement_new.png": ("trophy", "F59E0B"),
    "icon_analytics_new.png": ("bar-chart", "06B6D4"),
    "Settings_new.png": ("settings", "6B7280"),
    "user_new.png": ("user", "4F46E5"),
    "icon_notification_new.png": ("bell", "F59E0B"),
    "icon_darkmode_new.png": ("moon-symbol", "6B7280"),
    # Dashboard specific icons
    "calendar_new.png": ("calendar", "3B82F6"),
    "timer_new.png": ("time", "EF4444"),
    "quoteicon_new.png": ("quote-left", "3B82F6"),
    "note_new.png": ("edit", "10B981"),
    "fire_new.png": ("fire-element", "F97316"),
    "work-order_new.png": ("clipboard", "3B82F6"),
    "study_new.png": ("reading", "10B981"),
    "idea_new.png": ("light-on", "F59E0B"),
    "link_new.png": ("link", "8B5CF6")
}

base_url = "https://img.icons8.com/ios/40/{color}/{name}.png"
out_dir = "C:/Users/HunterZ/Desktop/FINAL EXAM JAVA/src/main/resources/images/"

for filename, (name, color) in icons.items():
    url = base_url.format(color=color, name=name)
    out_path = os.path.join(out_dir, filename)
    try:
        urllib.request.urlretrieve(url, out_path)
        print(f"Downloaded {filename}")
    except Exception as e:
        print(f"Failed to download {filename}: {e}")
