Kawa Code extension for IntelliJ IDEs
=====================================

> Real-time team collaboration for IntelliJ editors: IDEA, PyCharm, etc

## Overview

The **Kawa Code PyCharm Plugin** brings real-time collaborative coding features to JetBrains IDEs, enabling developers to see code intersections, conflicts, and overlaps with teammates before committing changes.

This plugin is part of the Kawa Code suite, which includes extensions for:
- **Emacs** ([kawa.emacs](https://github.com/CodeAwareness/kawa.emacs))
- **Visual Studio Code** ([kawa.vscode](https://github.com/CodeAwareness/kawa.vscode))
- **PyCharm** (this repository)

## Key Features

- **Real-time Peer Code Highlighting** - See which lines your teammates are modifying
- **Conflict Detection** - Identify merge conflicts before they happen
- **Overlap Detection** - Find overlapping changes across team members
- **Side-by-Side Diff Viewing** - Compare your code with teammates' versions
- **Branch Comparison** - Compare your working copy against other branches
- **Low-Noise Design** - Non-intrusive visual indicators that don't interrupt your workflow

## Highlight Types

The plugin uses color-coded highlights to indicate different types of code changes:

| Type | Purpose | Light Theme | Dark Theme |
|------|---------|-------------|------------|
| **Conflict** | Merge conflict areas | Red | Dark Red |
| **Overlap** | Overlapping changes with peers | Orange | Dark Orange |
| **Peer** | Code modified by teammates | Blue | Dark Blue |
| **Modified** | Your local changes | Green | Dark Green |

## Status

🚧 **Currently in Planning Phase** 🚧

This repository currently contains:
- **[ARCHITECTURE.md](ARCHITECTURE.md)** - Comprehensive architecture overview
- **[IMPLEMENTATION_PLAN.md](IMPLEMENTATION_PLAN.md)** - Detailed 5-phase implementation plan

**Estimated Development Timeline:** 16-24 days across 5 phases

## Architecture Highlights

The plugin is built on a **3-layer architecture**:

1. **Communication Layer** - Socket-based IPC with Kawa Code backend
2. **Event Handling System** - Message routing and event dispatch
3. **UI Integration Layer** - Editor highlighting, status bar, diff viewer

### Technology Stack

- **Language:** Java/Kotlin
- **Platform:** IntelliJ Platform SDK
- **Build Tool:** Gradle
- **JSON Library:** Gson
- **Communication:** Unix Domain Sockets / Windows Named Pipes

## Communication Protocol

The plugin communicates with the Kawa Code backend using:
- **Transport:** Unix domain sockets (`~/.kawa-code/sockets/`) or Windows named pipes
- **Format:** JSON messages with form-feed delimiters (`\f`)
- **Flow:** Request/Response/Error patterns

Example message:
```json
{
  "flow": "req",
  "domain": "code",
  "action": "active-path",
  "data": {
    "fpath": "/path/to/file.py",
    "doc": "file.py"
  },
  "caw": "client-guid"
}
```

Getting Started
For Developers
Clone the repository

git clone https://github.com/CodeAwareness/kawa.intellij.git
cd kawa.intellij
Review the architecture

# Read the architecture overview
cat ARCHITECTURE.md

# Read the implementation plan
cat IMPLEMENTATION_PLAN.md
Set up development environment

Open project in IntelliJ IDEA
Install IntelliJ Platform Plugin SDK
Configure Gradle
See DEVELOPER_GUIDE.md (coming soon)
For Users

### Installation

The plugin will be available for installation from the JetBrains Marketplace once published.

**To install once available:**
1. Open PyCharm/IntelliJ IDEA
2. Go to `Settings/Preferences > Plugins`
3. Click `Marketplace`
4. Search for "Kawa Code"
5. Click `Install`
6. Restart the IDE

**Manual Installation (Development Builds):**
1. Download the plugin ZIP from releases
2. Go to `Settings/Preferences > Plugins`
3. Click the gear icon ⚙️ > `Install Plugin from Disk...`
4. Select the downloaded ZIP file
5. Restart the IDE

**For publishing information**, see [PUBLISHING.md](PUBLISHING.md)

Related Projects
kawa.emacs - Kawa Code for Emacs
kawa.vscode - Kawa Code for VS Code
Kawa Code Backend - Core service (proprietary)
Project Structure
kawa.intellij/
├── README.md                    # This file
├── build.gradle.kts             # Coming soon
├── src/
│   ├── main/
│   │   ├── java/                # Java source code
│   │   └── resources/           # Plugin resources
│   └── test/
│       └── java/                # Unit & integration tests
└── docs/                        # Additional documentation
Contributing
Contributions are welcome! Please:

Read the architecture and implementation plan
Follow the coding standards (see DEVELOPER_GUIDE.md - coming soon)
Write tests for new features
Submit pull requests with clear descriptions
License: MIT

Support
For issues, questions, or feature requests:

Issues: GitHub Issues
Discussions: GitHub Discussions
