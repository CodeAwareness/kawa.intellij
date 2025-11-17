# Code Awareness extension for IntelliJ IDEA
====================================

> Real-time team collaboration for IntelliJ IDEA developers

## Overview

The **Code Awareness IntelliJ Plugin** brings real-time collaborative coding features to JetBrains IntelliJ IDEA, enabling developers to see code intersections, conflicts, and overlaps with teammates before committing changes.

This plugin is part of the Code Awareness suite, which includes extensions for:
- **Emacs** ([kawa.emacs](https://github.com/CodeAwareness/kawa.emacs))
- **Visual Studio Code** ([kawa.vscode](https://github.com/CodeAwareness/kawa.vscode))
- **PyCharm** ([kawa.pycharm](https://github.com/CodeAwareness/kawa.pycharm))
- **IntelliJ IDEA** (this repository)

## Key Features

- **Real-time Peer Code Highlighting** - See which lines your teammates are modifying
- **Conflict Detection** - Identify merge conflicts before they happen
- **Overlap Detection** - Find overlapping changes across team members
- **Side-by-Side Diff Viewing** - Compare your code with teammates' versions
- **Branch Comparison** - Compare your working copy against other branches
- **Low-Noise Design** - Non-intrusive visual indicators that don't interrupt your workflow
- **Multi-Language Support** - Works with Java, Kotlin, Python, JavaScript, and all languages supported by IntelliJ IDEA

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
- **[IMPLEMENTATION.md](IMPLEMENTATION.md)** - Detailed 5-phase implementation plan

**Estimated Development Timeline:** 12-18 days across 5 phases

## Architecture Highlights

The plugin is built on a **3-layer architecture**:

1. **Communication Layer** - Socket-based IPC with Code Awareness backend
2. **Event Handling System** - Message routing and event dispatch
3. **UI Integration Layer** - Editor highlighting, status bar, diff viewer

### Technology Stack

- **Language:** Java/Kotlin
- **Platform:** IntelliJ Platform SDK
- **Build Tool:** Gradle
- **JSON Library:** Gson
- **Communication:** Unix Domain Sockets / Windows Named Pipes

## Communication Protocol

The plugin communicates with the Code Awareness backend using:
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
    "fpath": "/path/to/file.java",
    "doc": "file.java"
  },
  "caw": "client-guid"
}
```

## Implementation Strategy

This plugin will be ported from the **kawa.pycharm** implementation with ~95% code reuse, as both PyCharm and IntelliJ IDEA use the same IntelliJ Platform. The primary changes are:
- Package renaming: `com.codeawareness.pycharm` → `com.codeawareness.intellij`
- Plugin metadata updates
- IntelliJ IDEA branding

## Getting Started

### For Developers

1. **Clone the repositories**
   ```bash
   git clone https://github.com/CodeAwareness/kawa.intellij.git
   cd kawa.intellij

   # Clone the PyCharm reference implementation
   git clone https://github.com/CodeAwareness/kawa.pycharm.git ../kawa.pycharm
   ```

2. **Review the architecture**
   ```bash
   # Read the architecture overview
   cat ARCHITECTURE.md

   # Read the implementation plan
   cat IMPLEMENTATION.md
   ```

3. **Set up development environment**
   - Open project in IntelliJ IDEA
   - Install IntelliJ Platform Plugin SDK
   - Configure Gradle
   - See implementation plan for detailed setup

### For Users

The plugin is not yet available for installation. Check back soon for release updates!

## Related Projects

- [kawa.pycharm](https://github.com/CodeAwareness/kawa.pycharm) - Code Awareness for PyCharm (reference implementation)
- [kawa.emacs](https://github.com/CodeAwareness/kawa.emacs) - Code Awareness for Emacs
- [kawa.vscode](https://github.com/CodeAwareness/kawa.vscode) - Code Awareness for VS Code
- Code Awareness Backend - Core service (proprietary)

## Project Structure

```
kawa.intellij/
├── README.md                    # This file
├── ARCHITECTURE.md              # Architecture overview
├── IMPLEMENTATION.md            # Implementation plan
├── build.gradle.kts             # Coming soon
├── src/
│   ├── main/
│   │   ├── java/                # Java source code
│   │   └── resources/           # Plugin resources
│   └── test/
│       └── java/                # Unit & integration tests
└── docs/                        # Additional documentation
```

## Implementation Phases

### Phase 1: Foundation (3-4 days) - CRITICAL
- Project setup
- Socket communication layer
- Message protocol
- Connection state machine
- Logging & debugging

### Phase 2: Core Features (3-4 days) - HIGH
- Event dispatcher system
- File change monitoring
- Active file tracking
- Basic event handlers
- Authentication flow

### Phase 3: UI Integration (2-3 days) - HIGH
- Highlighting system
- Status bar widget
- Notifications
- Actions & menus
- Settings UI

### Phase 4: Advanced Features (2-3 days) - MEDIUM
- Diff viewer integration
- Context management
- Project management

### Phase 5: Polish & Testing (2-4 days) - MEDIUM
- Comprehensive testing
- Performance optimization
- Error handling
- Documentation
- Distribution preparation

## Documentation

- **ARCHITECTURE.md** - Detailed architecture overview (15 sections)
- **IMPLEMENTATION.md** - Phase-by-phase implementation guide
- Additional documentation will be added as development progresses:
  - Developer guide
  - User guide
  - API reference
  - Troubleshooting guide

## Requirements

### Development Environment
- IntelliJ IDEA 2023.3+
- JDK 17+
- Gradle 8.0+
- Git

### Runtime Requirements
- IntelliJ IDEA 2023.3+ (Community or Ultimate)
- Code Awareness backend application
- Java 17+ runtime

### Platform Support
- Windows 10/11
- macOS (Intel & Apple Silicon)
- Linux (Ubuntu, Fedora, etc.)

## Contributing

Contributions are welcome! Please:

1. Read the architecture and implementation plan
2. Follow the coding standards (see implementation plan)
3. Write tests for new features
4. Submit pull requests with clear descriptions

## Development Progress

Track the implementation progress in [IMPLEMENTATION.md](IMPLEMENTATION.md). Current phase: **Planning**

## License

[TBD - To be determined based on project requirements]

## Support

For issues, questions, or feature requests:

- **Issues:** [GitHub Issues](https://github.com/CodeAwareness/kawa.intellij/issues)
- **Discussions:** [GitHub Discussions](https://github.com/CodeAwareness/kawa.intellij/discussions)

## Acknowledgments

This implementation is based on the proven architecture from:
- **kawa.pycharm** - Primary reference implementation for IntelliJ Platform
- **kawa.emacs** - Original Code Awareness implementation
- **kawa.vscode** - VS Code implementation

Special thanks to the Code Awareness team and all contributors to the existing extensions.
