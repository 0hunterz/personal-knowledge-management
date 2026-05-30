import json
import re
import urllib.parse
from pathlib import Path

log_file = r"C:\Users\HunterZ\.gemini\antigravity\brain\6222e6b8-b806-458c-bda9-cbb51783c917\.system_generated\logs\transcript.jsonl"

recovered_files = {}

with open(log_file, 'r', encoding='utf-8') as f:
    for line in f:
        try:
            data = json.loads(line)
        except:
            continue
            
        if data.get("type") == "VIEW_FILE" and data.get("step_index", 999) <= 45:
            content = data.get("content", "")
            if not content:
                continue
            
            # Extract file path
            path_match = re.search(r"File Path: `file:///([^`]+)`", content)
            if not path_match:
                continue
                
            raw_path = path_match.group(1)
            # URL decode the path (e.g., %20 to space)
            file_path = urllib.parse.unquote(raw_path)
            # Fix windows drive letter format if needed (c:/ -> C:/)
            
            # Find where the actual code starts
            marker = "Please note that any changes targeting the original code should remove the line number, colon, and leading space.\n"
            if marker in content:
                code_section = content.split(marker, 1)[1]
            else:
                continue
                
            # Remove trailing message if it exists
            trailing_msg = "The above content does NOT show the entire file contents."
            trailing_msg2 = "The above content shows the entire, complete file contents of the requested file."
            if trailing_msg in code_section:
                code_section = code_section.split(trailing_msg)[0]
            if trailing_msg2 in code_section:
                code_section = code_section.split(trailing_msg2)[0]
                
            # Strip line numbers
            recovered_lines = []
            for code_line in code_section.split("\n"):
                if not code_line.strip():
                    recovered_lines.append("")
                    continue
                # Match "123: " at start
                line_match = re.match(r"^\d+:\s?(.*)", code_line)
                if line_match:
                    recovered_lines.append(line_match.group(1))
                else:
                    recovered_lines.append(code_line)
                    
            # Join and save to our dictionary
            # Only keep the first occurrence of each file (since they are viewed at the start)
            if file_path not in recovered_files:
                recovered_files[file_path] = "\n".join(recovered_lines).strip()

print(f"Found {len(recovered_files)} files to recover.")
for path, text in recovered_files.items():
    print(f"Recovering: {path}")
    # Write to disk
    try:
        p = Path(path)
        p.parent.mkdir(parents=True, exist_ok=True)
        with open(p, 'w', encoding='utf-8') as out_f:
            out_f.write(text)
    except Exception as e:
        print(f"Failed to write {path}: {e}")
