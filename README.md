## A.E.G.I.S — Backend

### Overview
A.E.G.I.S. (AI Enterprise Governance & Identity Security) is a security-focused multi-agent platform that demonstrates authentication, least-privilege authorization, tool governance, auditability, risk controls, PAM/JIT workflows and AI-assisted knowledge/security analysis.

The backend is designed around a key principle:
A capability is not authorized merely because it exists. An agent must be explicitly assigned the capability, and the caller must have the required API permission.

### Core Capabilities
- OAuth 2.0 / OIDC authentication through Auth0
- JWT validation and RBAC enforcement with Spring Security
- Agent lifecycle management and active-state validation
- Agent → tool authorization through explicit agent_tools mappings
- Task execution API for specialized agents
- Oracle — document retrieval and RAG-based knowledge queries
- Shadow — semantic investigation of audit activity
- Titan — privileged-operation workflow with PAM/JIT controls
- Workload identity records using a SPIFFE-inspired application model
- Audit and risk event tracking
- Document ingestion using Apache Tika
- Embeddings and LLM responses using Spring AI + Google Gemini
- In-memory semantic indexing using Spring AI SimpleVectorStore
- Startup re-indexing of persisted documents and audit events after backend restarts

### Backend Architecture
```
A.E.G.I.S. Web-Application (React + Vite)
                  |
                  ↓  HTTPS + JWT
┌──────────────────────────────────┐
│          Spring Boot API         │
├──────────────────────────────────┤
│ Spring Security,                 │
│ Agents, Tool Management          │
│ Task Orchestration,              │
│ Security (PAM / Risk / Audit),   │
│ Documents, RAG                   │
└──────────────────────────────────┘
       |                    |
       ↓                    ↓
   PostgreSQL          Google Gemini
(Persistent Data)    (LLM + Embedding)
```

### Authorization Model
A.E.G.I.S. uses Auth0 RBAC permissions that are mapped to Spring Security authorities:
<table>
  <tr>
    <th>Permission</th>
    <th>Purpose</th>
  </tr>

  <tr>
    <td>agent:read</td>
    <td>Read agent information.</td>
  </tr>

  <tr>
    <td>agent:execute</td>
    <td>Execute approved agent tasks.</td>
  </tr>

  <tr>
    <td>agent:manage</td>
    <td>Administrative management of agents, tools, workloads and security controls.</td>
  </tr>
</table>

#### Access Flow:
```
User
  ↓
Auth0 Role
  ↓
API Permissions
  ↓
JWT
  ↓
Spring Security
  ↓
Endpoint Authorization
```

### Agent/Tool Governance
Agent execution is checked at multiple layers:
1. The caller must hold agent:execute for task execution.
2. The selected agent must be ACTIVE.
3. The agent must explicitly possess the required tool.
4. Specialized controls are applied for sensitive operations.
5. Relevant actions are written to audit/risk records.

Tool assignments:
<table>
  <tr>
    <th>Agent</th>
    <th>Purpose</th>
    <th>Assigned Tools</th>
  </tr>

  <tr>
    <td>Oracle</td>
    <td>Knowledge & Document Retrieval</td>
    <td>document.search, document.read</td>
  </tr>

  <tr>
    <td>Shadow</td>
    <td>Security Investigation & Audit Analysis</td>
    <td>audit.search</td>
  </tr>

  <tr>
    <td>Titan</td>
    <td>Privileged Operations</td>
    <td>service:restart</td>
  </tr>
</table>

### Agent Flows

#### Oracle
```
POST /api/tasks
      ↓
agent:execute
      ↓
Oracle ACTIVE?
      ↓
Oracle has document.search?
      ↓
Semantic search (top-k retrieval)
      ↓
Build grounded context
      ↓
Gemini response
      ↓
Persist task + audit event
```

#### Shadow
```
POST /api/tasks
      ↓
agent:execute
      ↓
Shadow ACTIVE?
      ↓
Shadow has audit.search?
      ↓
Semantic audit search
      ↓
Build investigation context
      ↓
Gemini security analysis
```

#### Titan
```
POST /api/tasks
      ↓
agent:execute
      ↓
Titan ACTIVE?
      ↓
Titan has service.restart?
      ↓
Create privilege request
      ↓
PAM approval
      ↓
JIT access window
      ↓
Simulated privileged operation
      ↓
Audit + expiration
```

### Persistance and RAG
The application currently uses Spring AI SimpleVectorStore for semantic search. Because this store is memory-resident, the backend rebuilds its document and audit indexes during application startup from persisted PostgreSQL records.

#### Document Pipeline
```
Upload
  ↓
Apache Tika text extraction
  ↓
Persist document metadata + content
  ↓
TokenTextSplitter
  ↓
Embedding generation
  ↓
SimpleVectorStore
```

#### Audit Pipeline
```
Audit event
  ↓
Persist in PostgreSQL
  ↓
Create semantic representation
  ↓
SimpleVectorStore
```

Startup initializers reconstruct these runtime indexes after a restart.

### Tech Stack
<table>
  <tr>
    <th>Layer</th>
    <th>Technology</th>
  </tr>

  <tr>
    <td>Language & Framework</td>
    <td>Java + Spring Boot</td>
  </tr>

  <tr>
    <td>Security</td>
    <td>Spring Security + OAuth 2.0/JWT</td>
  </tr>

  <tr>
    <td>Database</td>
    <td>PostgreSQL</td>
  </tr>

  <tr>
    <td>Persistence</td>
    <td>Spring Data JPA/Hibernate</td>
  </tr>

  <tr>
    <td>AI</td>
    <td>Spring AI</td>
  </tr>

  <tr>
    <td>LLM/Embeddings</td>
    <td>Google Gemini/Google Embeddings</td>
  </tr>

  <tr>
    <td>Vector Store</td>
    <td>Spring AI — SimpleVectoreStore</td>
  </tr>

  <tr>
    <td>Document Parsing</td>
    <td>Apache Tika</td>
  </tr>
</table>

### Project Structure
```
src/main/java/com/agenticai/aegisagent/
├── agent/          # Agent entities, services and APIs
├── audit/          # Audit events and audit search/indexing
├── document/       # Document upload, parsing and RAG indexing
├── pam/            # Privileged access / JIT workflows
├── risk/           # Risk events and assessment logic
├── security/       # Spring Security + Auth0 JWT configuration
├── task/           # Agent task execution/orchestration
├── tool/           # Tool registry and agent-tool authorization
└── workload/       # Workload identity records and lifecycle
```

### Local Setup

#### Prerequisites
- Java 21
- PostgreSQL
- Maven (or the included Maven Wrapper)
- Google Gemini API key
- Auth0 tenant/API configured for the application

#### 1. Clone
```
git clone https://github.com/Rohitha-25/Aegis-Backend.git
cd Aegis-Backend
```

#### 2. Configure environment variables
Configure local values using environment variables or an ignored local configuration file.
```
Eg: GEMINI_API_KEY=<your-gemini-key>
```
Configure your PostgreSQL and Auth0 properties separately for your local environment.

#### 3. Run
```
Windows:
mvnw.cmd spring-boot:run

macOS / Linux:
./mvnw spring-boot:run
```

#### Related Repository
https://github.com/Rohitha-25/Aegis-Frontend
