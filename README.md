# Spring AI MCP Demo

One MCP server, many clients. A course catalog is exposed over the **Model Context Protocol**,
then used by a Spring AI chat app *and* by off-the-shelf tools like MCP Inspector and Claude Code
— without changing a line of server code.

```
┌─────────────────────┐                        ┌───────────────────────┐
│  mcp-client  :8080  │ ── Streamable HTTP ──▶ │  mcp-server  :8081    │
│  ChatClient + Groq  │                        │  tools · resources ·  │
│  (gpt-oss-120b)     │                        │  prompts              │
└─────────────────────┘                        └───────────────────────┘
          │                                              ▲
          └── STDIO ──▶ filesystem MCP server            │
                        (optional profile)      MCP Inspector / Claude Code
```

| | Version |
|---|---|
| Java | 25 |
| Spring Boot | 4.1.2 |
| Spring AI | 2.0.2 |
| Model | `openai/gpt-oss-120b` on Groq |

## Teaching sequence

### 1. The server: three things an MCP server can offer

| Concept | What it is | In this demo |
|---|---|---|
| **Tool** | An action the model can call | `listCourses`, `searchCourses`, `enrollStudent` — [`CourseTools`](mcp-server/src/main/java/com/yourjavaguy/mcpserver/mcp/CourseTools.java) |
| **Resource** | Read-only context, addressed by URI | `course://{courseId}` — [`CourseResources`](mcp-server/src/main/java/com/yourjavaguy/mcpserver/mcp/CourseResources.java) |
| **Prompt** | A reusable prompt template | `study-plan` — [`CoursePrompts`](mcp-server/src/main/java/com/yourjavaguy/mcpserver/mcp/CoursePrompts.java) |

Key points:
- One starter: `spring-ai-starter-mcp-server-webmvc`.
- `@McpTool`, `@McpResource`, `@McpPrompt` on plain Spring beans — the annotation scanner registers them.
- `CourseCatalog` knows nothing about MCP. The `mcp` package is just another adapter, like a controller.
- Throwing an exception from a tool becomes an MCP tool error the model can read and explain.

```bash
cd mcp-server
./mvnw spring-boot:run
```

The server listens on `http://localhost:8081/mcp` (Streamable HTTP, the default in Spring AI 2.0).

### 2. Prove it works — before writing any client code

**MCP Inspector** (raw protocol view):

```bash
npx @modelcontextprotocol/inspector
```

Pick transport **Streamable HTTP**, URL `http://localhost:8081/mcp`, then list tools, read
`course://kafka`, and get the `study-plan` prompt.

**Claude Code** (a real AI host — zero Claude-specific code on the server):

```bash
claude mcp add --transport http course-catalog http://localhost:8081/mcp
```

Ask: *"What Spring courses are there? Enroll ada@example.com in the Spring AI one."*

### 3. The client: Spring AI app using the server's tools

- One starter: `spring-ai-starter-mcp-client`, plus a connection in `application.yaml`.
- Spring AI connects, discovers the tools, and exposes them as a `SyncMcpToolCallbackProvider`.
- [`CourseAssistant`](mcp-client/src/main/java/com/yourjavaguy/mcpclient/assistant/CourseAssistant.java)
  hands that provider to `ChatClient` with `defaultToolCallbacks(...)`. That's the whole integration.
- Groq speaks the OpenAI API, so the OpenAI starter points at `https://api.groq.com/openai/v1`.

```bash
export GROQ_API_KEY=your-key   # https://console.groq.com/keys
cd mcp-client
./mvnw spring-boot:run
```

```bash
# Which tools did the client discover?
curl localhost:8080/tools

# Let the model use them
curl -X POST localhost:8080/ask -H 'Content-Type: application/json' \
  -d '{"question": "Which advanced courses do you have?"}'

# Watch a tool error come back: spring-ai has only 2 seats
curl -X POST localhost:8080/ask -H 'Content-Type: application/json' \
  -d '{"question": "Enroll ada@example.com, grace@example.com and linus@example.com in spring-ai"}'
```

### 4. Plug in someone else's server

The same client can use any MCP server. The `filesystem` profile adds the official filesystem
server over **STDIO** (needs Node.js), pointed at [`mcp-client/notes`](mcp-client/notes):

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=filesystem
```

```bash
curl -X POST localhost:8080/ask -H 'Content-Type: application/json' \
  -d '{"question": "Read student-goals.md and enroll each student in the course that fits their goal"}'
```

One question, two MCP servers: one reads files, the other enrolls students.

## Tests

```bash
cd mcp-server && ./mvnw test
```

[`CourseToolsIntegrationTest`](mcp-server/src/test/java/com/yourjavaguy/mcpserver/CourseToolsIntegrationTest.java)
starts the server on a random port and talks to it with a real MCP client.
