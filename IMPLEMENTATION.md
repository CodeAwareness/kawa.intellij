# Code Awareness IntelliJ Plugin - Implementation Plan



## Executive Summary



This document provides a detailed, phase-by-phase implementation plan for the Code Awareness IntelliJ plugin. The plan is structured into 5 phases over an estimated **12-18 development days**, with clear priorities, dependencies, and success criteria for each phase.



**Development Approach:** Iterative with working increments at each phase

**Risk Level:** Very Low (direct port from proven kawa.pycharm implementation)

**Target Platforms:** Windows, macOS, Linux

**Code Reuse:** ~95% code reuse from kawa.pycharm (same IntelliJ Platform)



---



## Timeline Overview



| Phase | Duration | Priority | Deliverable |

|-------|----------|----------|-------------|

| **Phase 1: Foundation** | 3-4 days | Critical | Working socket communication |

| **Phase 2: Core Features** | 3-4 days | High | File monitoring & basic events |

| **Phase 3: UI Integration** | 2-3 days | High | Highlighting & status bar |

| **Phase 4: Advanced Features** | 2-3 days | Medium | Diff viewer & context mgmt |

| **Phase 5: Polish & Testing** | 2-4 days | Medium | Bug fixes & documentation |

| **Total** | **12-18 days** | - | Production-ready plugin |



**Note:** Timeline is shorter than PyCharm implementation due to direct code reuse from kawa.pycharm repository.



---



## Phase 1: Foundation (3-4 days) - CRITICAL



### Objective

Port the communication infrastructure and plugin scaffolding from kawa.pycharm. This phase primarily involves renaming packages and updating plugin metadata.



### Tasks



#### 1.1 Project Setup (0.5 day)

- [ ] Create IntelliJ Platform plugin project structure

- [ ] Copy and adapt `build.gradle.kts` from kawa.pycharm

- [ ] Update `plugin.xml` with IntelliJ IDEA metadata

- [ ] Configure development environment (IntelliJ IDEA)

- [ ] Set up version control and branching strategy

- [ ] Configure CI/CD pipeline (GitHub Actions)



**Deliverable:** Buildable plugin skeleton that can be loaded in IntelliJ IDEA



**Package Renaming:**

- `com.codeawareness.pycharm` → `com.codeawareness.intellij`



**Files Created:**

```

build.gradle.kts

src/main/resources/META-INF/plugin.xml

src/main/java/com/codeawareness/intellij/CodeAwarenessApplicationService.java

```



**Key Changes from PyCharm:**

```xml

<!-- plugin.xml -->

<id>com.codeawareness.intellij</id>

<name>Code Awareness</name>

<description>

  Real-time team collaboration for IntelliJ IDEA.

  See code intersections, conflicts, and overlaps with teammates

  before committing changes.

</description>

```



---



#### 1.2 Socket Communication Layer (1.5-2 days)

- [ ] Port `SocketManager` from kawa.pycharm

- [ ] Port Unix domain socket support

- [ ] Port Windows named pipe support

- [ ] Port socket path resolution utilities

- [ ] Update package references

- [ ] Test cross-platform socket connections



**Deliverable:** Working socket connection to catalog service



**Files to Port:**

```

src/main/java/com/codeawareness/intellij/communication/SocketManager.java

src/main/java/com/codeawareness/intellij/communication/UnixSocketAdapter.java

src/main/java/com/codeawareness/intellij/communication/WindowsNamedPipeAdapter.java

src/main/java/com/codeawareness/intellij/utils/PathUtils.java

```



**Changes Required:** Minimal - mostly package name updates



**Test Cases:**

- Connect to catalog service

- Handle connection timeout

- Retry on connection failure

- Close connection gracefully



---



#### 1.3 Message Protocol Layer (0.5-1 day)

- [ ] Port `MessageProtocol` from kawa.pycharm

- [ ] Port JSON message serialization/deserialization

- [ ] Port form-feed delimiter parsing

- [ ] Port message builder utilities

- [ ] Update package references



**Deliverable:** Reliable message send/receive over sockets



**Files to Port:**

```

src/main/java/com/codeawareness/intellij/communication/MessageProtocol.java

src/main/java/com/codeawareness/intellij/communication/Message.java

src/main/java/com/codeawareness/intellij/communication/MessageBuilder.java

src/main/java/com/codeawareness/intellij/communication/MessageParser.java

```



**Changes Required:** None - protocol is identical across all clients



---



#### 1.4 Connection State Machine (0.5 day)

- [ ] Port `CatalogConnection` from kawa.pycharm

- [ ] Port `IpcConnection` from kawa.pycharm

- [ ] Port GUID generator

- [ ] Update package references

- [ ] Test connection sequence



**Deliverable:** Complete connection sequence from catalog to main service



**Files to Port:**

```

src/main/java/com/codeawareness/intellij/communication/CatalogConnection.java

src/main/java/com/codeawareness/intellij/communication/IpcConnection.java

src/main/java/com/codeawareness/intellij/utils/GuidGenerator.java

```



**Changes Required:** None - connection logic is platform-agnostic



---



#### 1.5 Logging & Debugging (0.5 day)

- [ ] Port logging utilities from kawa.pycharm

- [ ] Configure debug mode

- [ ] Add log viewer action

- [ ] Update package references



**Deliverable:** Comprehensive logging for troubleshooting



**Files to Port:**

```

src/main/java/com/codeawareness/intellij/utils/Logger.java

```



---



### Phase 1 Success Criteria

- [x] Plugin loads in IntelliJ IDEA without errors

- [x] Successfully connects to catalog service

- [x] Successfully connects to main IPC service

- [x] Can send and receive JSON messages

- [x] Graceful disconnect on plugin disable

- [x] Cross-platform support (Windows, macOS, Linux)



---



## Phase 2: Core Features (3-4 days) - HIGH PRIORITY



### Objective

Port file monitoring, event handling, and basic request/response flows from kawa.pycharm.



### Tasks



#### 2.1 Event Dispatcher System (1 day)

- [ ] Port `EventDispatcher` from kawa.pycharm

- [ ] Port `EventHandler` interface

- [ ] Port `ResponseHandlerRegistry`

- [ ] Update package references

- [ ] Test event routing



**Deliverable:** Working event routing system



**Files to Port:**

```

src/main/java/com/codeawareness/intellij/events/EventDispatcher.java

src/main/java/com/codeawareness/intellij/events/EventHandler.java

src/main/java/com/codeawareness/intellij/events/ResponseHandlerRegistry.java

```



**Changes Required:** None - event system is platform-agnostic



---



#### 2.2 File Change Monitoring (1 day)

- [ ] Port `BulkFileListener` implementation from kawa.pycharm

- [ ] Port file save detection logic

- [ ] Port debouncing implementation

- [ ] Update `plugin.xml` listeners

- [ ] Test file save notifications



**Deliverable:** File save notifications sent to backend



**Files to Port:**

```

src/main/java/com/codeawareness/intellij/listeners/FileChangeListener.java

src/main/java/com/codeawareness/intellij/monitoring/FileMonitor.java

```



**plugin.xml Update:**

```xml

<applicationListeners>

  <listener class="com.codeawareness.intellij.listeners.FileChangeListener"

            topic="com.intellij.openapi.vfs.newvfs.BulkFileListener"/>

</applicationListeners>

```



**Changes Required:** Package name updates only



---



#### 2.3 Active File Tracking (0.5 day)

- [ ] Port `FileEditorManagerListener` from kawa.pycharm

- [ ] Port active file tracking logic

- [ ] Update `plugin.xml` listeners

- [ ] Test active file notifications



**Deliverable:** Active file notifications sent to backend



**Files to Port:**

```

src/main/java/com/codeawareness/intellij/listeners/ActiveFileListener.java

```



**plugin.xml Update:**

```xml

<projectListeners>

  <listener class="com.codeawareness.intellij.listeners.ActiveFileListener"

            topic="com.intellij.openapi.fileEditor.FileEditorManagerListener"/>

</projectListeners>

```



---



#### 2.4 Basic Event Handlers (0.5-1 day)

- [ ] Port peer selection handlers from kawa.pycharm

- [ ] Port branch selection handlers

- [ ] Port authentication handlers

- [ ] Update package references

- [ ] Test event handling



**Deliverable:** Basic event handling for peer/branch selection



**Files to Port:**

```

src/main/java/com/codeawareness/intellij/events/handlers/PeerSelectHandler.java

src/main/java/com/codeawareness/intellij/events/handlers/PeerUnselectHandler.java

src/main/java/com/codeawareness/intellij/events/handlers/BranchSelectHandler.java

src/main/java/com/codeawareness/intellij/events/handlers/AuthInfoHandler.java

```



**Changes Required:** None - handlers are platform-agnostic



---



#### 2.5 Authentication Flow (0.5 day)

- [ ] Port `AuthManager` from kawa.pycharm

- [ ] Port authentication request/response handling

- [ ] Update package references

- [ ] Test authentication flow



**Deliverable:** Working authentication flow



**Files to Port:**

```

src/main/java/com/codeawareness/intellij/auth/AuthManager.java

```



---



### Phase 2 Success Criteria

- [x] File saves trigger backend notifications

- [x] Active file changes trigger backend notifications

- [x] Peer/branch selection events are handled

- [x] Authentication completes successfully

- [x] State is maintained correctly



---



## Phase 3: UI Integration (2-3 days) - HIGH PRIORITY



### Objective

Port visual feedback components (highlighting, status bar, notifications) from kawa.pycharm.



### Tasks



#### 3.1 Highlighting System (1-1.5 days)

- [ ] Port `HighlightManager` from kawa.pycharm

- [ ] Port `HighlightType` enum

- [ ] Port `ColorSchemeProvider`

- [ ] Update package references

- [ ] Test highlighting in light and dark themes



**Deliverable:** Working code highlighting for all 4 types



**Files to Port:**

```

src/main/java/com/codeawareness/intellij/highlighting/HighlightManager.java

src/main/java/com/codeawareness/intellij/highlighting/HighlightType.java

src/main/java/com/codeawareness/intellij/highlighting/ColorSchemeProvider.java

```



**Changes Required:** None - IntelliJ Platform APIs are identical



---



#### 3.2 Status Bar Widget (0.5 day)

- [ ] Port `StatusBarWidgetFactory` from kawa.pycharm

- [ ] Port status bar widget UI

- [ ] Update package references

- [ ] Update `plugin.xml`

- [ ] Test status bar integration



**Deliverable:** Status bar widget showing connection status



**Files to Port:**

```

src/main/java/com/codeawareness/intellij/ui/CodeAwarenessStatusBarWidget.java

```



**plugin.xml Update:**

```xml

<extensions defaultExtensionNs="com.intellij">

  <statusBarWidgetFactory

      implementation="com.codeawareness.intellij.ui.CodeAwarenessStatusBarWidget"/>

</extensions>

```



---



#### 3.3 Notifications (0.25 day)

- [ ] Port notification helper from kawa.pycharm

- [ ] Update package references

- [ ] Test notifications



**Deliverable:** User-facing notifications for key events



**Files to Port:**

```

src/main/java/com/codeawareness/intellij/ui/NotificationHelper.java

```



---



#### 3.4 Actions & Menus (0.5 day)

- [ ] Port action classes from kawa.pycharm

- [ ] Update `plugin.xml` action definitions

- [ ] Update package references

- [ ] Test actions



**Deliverable:** User-accessible actions for plugin control



**Files to Port:**

```

src/main/java/com/codeawareness/intellij/actions/RefreshAction.java

src/main/java/com/codeawareness/intellij/actions/ClearHighlightsAction.java

src/main/java/com/codeawareness/intellij/actions/ConnectionStatusAction.java

```



**plugin.xml Update:**

```xml

<actions>

  <group id="CodeAwareness.Menu" text="Code Awareness" popup="true">

    <action id="CodeAwareness.Refresh"

            class="com.codeawareness.intellij.actions.RefreshAction"

            text="Refresh Highlights"/>

    <action id="CodeAwareness.ClearHighlights"

            class="com.codeawareness.intellij.actions.ClearHighlightsAction"

            text="Clear All Highlights"/>

    <action id="CodeAwareness.ConnectionStatus"

            class="com.codeawareness.intellij.actions.ConnectionStatusAction"

            text="Connection Status"/>

  </group>

</actions>

```



---



#### 3.5 Settings UI (0.5 day)

- [ ] Port `CodeAwarenessSettings` from kawa.pycharm

- [ ] Port `CodeAwarenessConfigurable`

- [ ] Update `plugin.xml`

- [ ] Update package references

- [ ] Test settings persistence



**Deliverable:** Settings page in IntelliJ IDEA preferences



**Files to Port:**

```

src/main/java/com/codeawareness/intellij/settings/CodeAwarenessSettings.java

src/main/java/com/codeawareness/intellij/settings/CodeAwarenessConfigurable.java

```



---



### Phase 3 Success Criteria

- [x] Highlights appear in editor for all 4 types

- [x] Status bar shows connection status

- [x] Notifications appear for key events

- [x] User can refresh/clear highlights via actions

- [x] Settings page is functional

- [x] UI works in light and dark themes



---



## Phase 4: Advanced Features (2-3 days) - MEDIUM PRIORITY



### Objective

Port diff viewing, context management, and project management features from kawa.pycharm.



### Tasks



#### 4.1 Diff Viewer Integration (1-1.5 days)

- [ ] Port `DiffViewerManager` from kawa.pycharm

- [ ] Port `TempFileManager`

- [ ] Port peer file extraction logic

- [ ] Update package references

- [ ] Test diff viewer



**Deliverable:** Working side-by-side diff with peer code



**Files to Port:**

```

src/main/java/com/codeawareness/intellij/diff/DiffViewerManager.java

src/main/java/com/codeawareness/intellij/diff/TempFileManager.java

```



**Changes Required:** Update temp directory path to use `caw.intellij` instead of `caw.pycharm`



---



#### 4.2 Context Management (0.5 day)

- [ ] Port context event handlers from kawa.pycharm

- [ ] Update package references

- [ ] Test context events



**Deliverable:** Context management events handled



**Files to Port:**

```

src/main/java/com/codeawareness/intellij/events/handlers/ContextAddHandler.java

src/main/java/com/codeawareness/intellij/events/handlers/ContextDeleteHandler.java

```



---



#### 4.3 Project Management (0.5-1 day)

- [ ] Port `ProjectManager` from kawa.pycharm

- [ ] Port git repository detection

- [ ] Update package references

- [ ] Test project metadata extraction



**Deliverable:** Automatic project detection and metadata



**Files to Port:**

```

src/main/java/com/codeawareness/intellij/project/ProjectManager.java

```



---



### Phase 4 Success Criteria

- [x] Diff viewer opens successfully

- [x] Context events are handled

- [x] Project metadata is detected

- [x] All features work end-to-end



---



## Phase 5: Polish & Testing (2-4 days) - MEDIUM PRIORITY



### Objective

Testing, documentation, and preparation for distribution.



### Tasks



#### 5.1 Comprehensive Testing (1-2 days)

- [ ] Port unit tests from kawa.pycharm

- [ ] Update test package references

- [ ] Add IntelliJ-specific integration tests

- [ ] Manual testing on all platforms

- [ ] Test with real Code Awareness backend

- [ ] Load testing (multiple projects, large files)



**Deliverable:** Comprehensive test suite



**Files to Port:**

```

src/test/java/com/codeawareness/intellij/SocketManagerTest.java

src/test/java/com/codeawareness/intellij/MessageProtocolTest.java

src/test/java/com/codeawareness/intellij/EventDispatcherTest.java

src/test/java/com/codeawareness/intellij/HighlightManagerTest.java

```



**Test Coverage Target:** 85%+



---



#### 5.2 Performance Optimization (0.5 day)

- [ ] Profile plugin performance

- [ ] Optimize any IntelliJ-specific bottlenecks

- [ ] Verify memory footprint

- [ ] Optimize file change debouncing



**Deliverable:** Optimized plugin with <50MB memory footprint



**Performance Targets:**

- Connection time: <1s

- Message round-trip: <100ms

- Highlight update: <50ms

- Memory: <50MB per project



---



#### 5.3 Error Handling Review (0.5 day)

- [ ] Review error handling from kawa.pycharm

- [ ] Add IntelliJ-specific error cases

- [ ] Test error recovery mechanisms

- [ ] Verify graceful degradation



**Deliverable:** Robust error handling



---



#### 5.4 Documentation (0.5-1 day)

- [ ] Adapt README.md from kawa.pycharm

- [ ] Update architecture references to IntelliJ

- [ ] Update implementation plan status

- [ ] Add screenshots

- [ ] Create CHANGELOG.md



**Deliverable:** Complete documentation



**Files to Create/Update:**

```

README.md

ARCHITECTURE.md (already created)

IMPLEMENTATION.md (this file)

CHANGELOG.md

```



---



#### 5.5 Plugin Metadata & Distribution (0.5 day)

- [ ] Finalize plugin.xml metadata

- [ ] Create plugin icon (SVG)

- [ ] Write plugin description for JetBrains Marketplace

- [ ] Configure publishing workflow

- [ ] Create release notes

- [ ] Test plugin installation



**Deliverable:** Plugin ready for distribution



**Files to Create:**

```

src/main/resources/META-INF/pluginIcon.svg

```



---



### Phase 5 Success Criteria

- [x] Test coverage ≥85%

- [x] All manual tests pass

- [x] Performance targets met

- [x] Documentation complete

- [x] Plugin ready for JetBrains Marketplace

- [x] No critical bugs



---



## Code Reuse Strategy



### Direct Port (95% of code)



The following components can be directly ported from kawa.pycharm with only package name changes:



**Communication Layer:**

- SocketManager

- CatalogConnection

- IpcConnection

- MessageProtocol

- Message classes



**Event System:**

- EventDispatcher

- EventHandler interface

- ResponseHandlerRegistry

- All event handler implementations



**File Monitoring:**

- FileChangeListener

- ActiveFileListener

- FileMonitor



**Highlighting:**

- HighlightManager

- HighlightType

- ColorSchemeProvider



**UI Components:**

- StatusBarWidget

- NotificationHelper

- All actions



**Settings:**

- CodeAwarenessSettings

- CodeAwarenessConfigurable



**Utilities:**

- GuidGenerator

- PathUtils

- Logger



### Adaptations Required (5% of code)



**Plugin Metadata:**

- plugin.xml (update IDs, names, descriptions)

- build.gradle.kts (update plugin ID)



**Package Names:**

- All: `com.codeawareness.pycharm` → `com.codeawareness.intellij`



**Temp Directory:**

- Update from `caw.pycharm` to `caw.intellij`



---



## Development Workflow



### Daily Workflow



1. **Porting Session** (3-4 hours)

   - Copy files from kawa.pycharm

   - Update package names

   - Update references

   - Test compilation



2. **Testing** (2-3 hours)

   - Run unit tests

   - Manual testing

   - Fix any issues



3. **Documentation** (1 hour)

   - Update implementation notes

   - Update CHANGELOG



### Weekly Milestones



- **Week 1:** Complete Phase 1-2 (Foundation + Core Features)

- **Week 2:** Complete Phase 3-4 (UI + Advanced Features)

- **Week 3:** Complete Phase 5 (Polish & Testing) + Distribution



---



## Risk Management



### Risk Assessment



| Risk | Impact | Likelihood | Mitigation |

|------|--------|------------|------------|

| Package renaming errors | Medium | Low | Automated refactoring tools |

| Platform compatibility issues | Low | Very Low | PyCharm uses same platform |

| Missing PyCharm code | High | Very Low | kawa.pycharm is complete |

| API differences | Low | Very Low | Same IntelliJ Platform version |



### Contingency Plans



**If kawa.pycharm code is incomplete:**

- Reference kawa.vscode or kawa.emacs implementations

- Implement missing features from scratch



**If platform differences found:**

- Document differences

- Implement IntelliJ-specific workarounds

- Share findings with PyCharm team



---



## Testing Strategy



### Unit Testing



**Approach:** Port all unit tests from kawa.pycharm



**Components to Test:**

- SocketManager

- MessageProtocol

- EventDispatcher

- HighlightManager

- GuidGenerator



**Framework:** JUnit 5 + Mockito (same as PyCharm)



---



### Integration Testing



**Scenarios:** Same as kawa.pycharm

- End-to-end connection flow

- File save → highlight update

- Peer selection → highlight appearance

- Diff viewer flow



**Framework:** IntelliJ Platform Test Framework



---



### Manual Testing Checklist



**Platform Testing:**

- [ ] Windows 10/11

- [ ] macOS (Intel + Apple Silicon)

- [ ] Linux (Ubuntu, Fedora)



**IDE Versions:**

- [ ] IntelliJ IDEA 2023.3

- [ ] IntelliJ IDEA 2024.1

- [ ] IntelliJ IDEA 2024.2



**Feature Testing:**

- [ ] Connection establishment

- [ ] File save detection

- [ ] Active file tracking

- [ ] Highlighting (all 4 types)

- [ ] Diff viewer

- [ ] Settings persistence

- [ ] Status bar updates

- [ ] Notifications

- [ ] Actions (refresh, clear)



---



## Success Metrics



### Overall Project Success



**Primary Metrics:**

- [ ] All features from kawa.pycharm ported successfully

- [ ] Plugin loads without errors in IntelliJ IDEA

- [ ] All unit tests pass (≥85% coverage)

- [ ] Performance matches or exceeds kawa.pycharm

- [ ] Cross-platform compatibility verified



**Secondary Metrics:**

- [ ] Code quality matches kawa.pycharm standards

- [ ] Documentation is complete and accurate

- [ ] Plugin is ready for JetBrains Marketplace



---



## Dependencies & Prerequisites



### Development Environment

- IntelliJ IDEA 2023.3+ (for plugin development)

- JDK 17+ (required for IntelliJ Platform 2023.3+)

- Gradle 8.0+ (build tool)

- Git (version control)



### Source Code Access

- **kawa.pycharm** repository (primary source for porting)

- **kawa.vscode** repository (reference if needed)

- **kawa.emacs** repository (reference if needed)



### External Services

- Code Awareness backend application (for testing)



### Libraries

```kotlin

dependencies {

    implementation("com.google.code.gson:gson:2.10.1")

    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")

    testImplementation("org.mockito:mockito-core:5.5.0")

}

```



---



## Post-Implementation Tasks



### After Phase 5 Completion



1. **Cross-IDE Testing**

   - Test alongside kawa.pycharm

   - Verify message compatibility

   - Test peer collaboration between IntelliJ and PyCharm users



2. **Beta Testing**

   - Recruit 5-10 beta testers

   - Gather feedback

   - Fix critical bugs



3. **Documentation Review**

   - Technical review by team

   - User documentation review

   - Update based on feedback



4. **Release Preparation**

   - Finalize version number (1.0.0)

   - Create release notes

   - Prepare marketing materials



5. **JetBrains Marketplace Submission**

   - Submit plugin for review

   - Address review feedback

   - Publish to marketplace



---



## Appendix A: File Structure



```

kawa.intellij/

├── build.gradle.kts

├── settings.gradle.kts

├── gradle.properties

├── README.md

├── ARCHITECTURE.md

├── IMPLEMENTATION.md

├── CHANGELOG.md

├── LICENSE

├── src/

│   ├── main/

│   │   ├── java/com/codeawareness/intellij/

│   │   │   ├── CodeAwarenessApplicationService.java

│   │   │   ├── CodeAwarenessProjectService.java

│   │   │   ├── communication/

│   │   │   │   ├── SocketManager.java

│   │   │   │   ├── UnixSocketAdapter.java

│   │   │   │   ├── WindowsNamedPipeAdapter.java

│   │   │   │   ├── CatalogConnection.java

│   │   │   │   ├── IpcConnection.java

│   │   │   │   ├── MessageProtocol.java

│   │   │   │   ├── Message.java

│   │   │   │   ├── MessageBuilder.java

│   │   │   │   └── MessageParser.java

│   │   │   ├── events/

│   │   │   │   ├── EventDispatcher.java

│   │   │   │   ├── EventHandler.java

│   │   │   │   ├── ResponseHandlerRegistry.java

│   │   │   │   └── handlers/

│   │   │   │       ├── PeerSelectHandler.java

│   │   │   │       ├── PeerUnselectHandler.java

│   │   │   │       ├── BranchSelectHandler.java

│   │   │   │       ├── AuthInfoHandler.java

│   │   │   │       ├── ContextAddHandler.java

│   │   │   │       └── ContextDeleteHandler.java

│   │   │   ├── highlighting/

│   │   │   │   ├── HighlightManager.java

│   │   │   │   ├── HighlightType.java

│   │   │   │   └── ColorSchemeProvider.java

│   │   │   ├── diff/

│   │   │   │   ├── DiffViewerManager.java

│   │   │   │   └── TempFileManager.java

│   │   │   ├── listeners/

│   │   │   │   ├── FileChangeListener.java

│   │   │   │   └── ActiveFileListener.java

│   │   │   ├── monitoring/

│   │   │   │   └── FileMonitor.java

│   │   │   ├── ui/

│   │   │   │   ├── CodeAwarenessStatusBarWidget.java

│   │   │   │   ├── NotificationHelper.java

│   │   │   │   └── actions/

│   │   │   │       ├── RefreshAction.java

│   │   │   │       ├── ClearHighlightsAction.java

│   │   │   │       └── ConnectionStatusAction.java

│   │   │   ├── settings/

│   │   │   │   ├── CodeAwarenessSettings.java

│   │   │   │   └── CodeAwarenessConfigurable.java

│   │   │   ├── auth/

│   │   │   │   └── AuthManager.java

│   │   │   ├── project/

│   │   │   │   └── ProjectManager.java

│   │   │   └── utils/

│   │   │       ├── GuidGenerator.java

│   │   │       ├── PathUtils.java

│   │   │       └── Logger.java

│   │   └── resources/

│   │       └── META-INF/

│   │           ├── plugin.xml

│   │           └── pluginIcon.svg

│   └── test/

│       └── java/com/codeawareness/intellij/

│           ├── SocketManagerTest.java

│           ├── MessageProtocolTest.java

│           ├── EventDispatcherTest.java

│           ├── HighlightManagerTest.java

│           └── IntegrationTest.java

└── .github/

    └── workflows/

        ├── build.yml

        └── test.yml

```



---



## Appendix B: Package Renaming Reference



### Global Find/Replace



**From:** `com.codeawareness.pycharm`

**To:** `com.codeawareness.intellij`



**Files Affected:** All `.java`, `.kt`, `.xml` files



### Plugin ID Updates



**plugin.xml:**

- `<id>com.codeawareness.pycharm</id>` → `<id>com.codeawareness.intellij</id>`



**build.gradle.kts:**

- Plugin ID reference updates



### Temp Directory Updates



**From:** `~/.cache/caw.pycharm/`

**To:** `~/.cache/caw.intellij/`



---



## Appendix C: Verification Checklist



### Pre-Phase 1

- [ ] kawa.pycharm repository cloned locally

- [ ] Development environment set up

- [ ] Access to Code Awareness backend for testing



### Post-Phase 1

- [ ] All communication classes compile

- [ ] Socket connection successful

- [ ] Package names updated correctly



### Post-Phase 2

- [ ] File monitoring works

- [ ] Event handlers registered

- [ ] Authentication successful



### Post-Phase 3

- [ ] Highlighting appears correctly

- [ ] Status bar shows connection

- [ ] Settings page accessible



### Post-Phase 4

- [ ] Diff viewer opens

- [ ] All features functional



### Post-Phase 5

- [ ] All tests pass

- [ ] Documentation complete

- [ ] Plugin installable



---



## Conclusion



This implementation plan provides a clear, efficient roadmap for porting the Code Awareness plugin from PyCharm to IntelliJ IDEA. With ~95% code reuse from the proven kawa.pycharm implementation, this project has a very low risk profile and an estimated timeline of 12-18 days.



**Key Success Factors:**

- Direct access to working kawa.pycharm source code

- Identical IntelliJ Platform APIs between PyCharm and IntelliJ IDEA

- Proven architecture and design patterns

- Comprehensive test suite from kawa.pycharm



**Next Steps:**

1. Review and approve this implementation plan

2. Clone kawa.pycharm repository

3. Set up development environment

4. Begin Phase 1: Foundation (package renaming and project setup)

5. Track progress against success criteria



---



**Document Version:** 1.0

**Last Updated:** November 17, 2025

**Author:** Code Awareness Team
