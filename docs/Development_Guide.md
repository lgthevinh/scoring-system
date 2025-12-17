# Fanroc Scoring System - Development Guide

## 🚀 Getting Started

### Prerequisites
- Java 17+
- Gradle 7.0+
- Node.js 16+
- NPM/Yarn

### Initial Setup
```bash
# Clone and setup
cd scoring-system
./gradlew build

# Frontend setup
cd webui
npm install
ng build --configuration development

# Start development server
./gradlew bootRun
```

### Database Setup
```bash
# Test database (SQLite)
# No setup required - created automatically at:
# src/test/resources/scoring_system.db

# Production database (configure in application.properties)
# Supports PostgreSQL, MySQL, etc.
```

## 🛠️ Development Workflow

### Backend Development (Java/Spring Boot)

#### Project Structure
```
src/main/java/org/thingai/app/
├── controller/          # REST API endpoints
├── scoringservice/
│   ├── entity/          # Database entities
│   ├── handler/         # Business logic
│   └── callback/        # Async callbacks
└── Main.java           # Application entry
```

#### Adding New Features
1. **Entity**: Create JPA entity in `entity/` package
2. **Repository**: Use `BaseDao` from applicationbase
3. **Handler**: Business logic in `handler/` package
4. **Controller**: REST endpoint in `controller/` package
5. **Tests**: Unit tests in `src/test/java/`

#### Code Style
- Use Lombok annotations (@Data, @Builder)
- Follow Spring Boot conventions
- Use `RequestCallback<T>` for async operations
- Document thread safety considerations

### Frontend Development (Angular)

#### Project Structure
```
webui/src/app/
├── core/
│   ├── services/        # API services
│   ├── models/          # TypeScript interfaces
│   └── interceptors/    # HTTP interceptors
├── features/            # Feature modules
│   ├── match-control/   # Match management UI
│   ├── referee/         # Referee scoring UI
│   └── scoring-display/ # Live display
└── app.routes.ts       # Routing configuration
```

#### Key Patterns
```typescript
// Service layer
@Injectable({ providedIn: 'root' })
export class ApiService {
  // HTTP calls with proper error handling
}

// Reactive components
export class MyComponent {
  data$ = signal(null);
  loading$ = signal(false);

  ngOnInit() {
    this.loadData();
  }

  async loadData() {
    this.loading$.set(true);
    try {
      const result = await this.apiService.getData();
      this.data$.set(result);
    } finally {
      this.loading$.set(false);
    }
  }
}
```

### WebSocket Integration

#### Sending Messages
```typescript
// Frontend: Broadcast service
this.broadcastService.publishMessage('/app/live/score/update/red', {
  type: 'LIVE_SCORE_SNAPSHOT',
  payload: { /* score data */ }
});
```

#### Receiving Messages
```java
// Backend: Broadcast handler
@Inject
private BroadcastHandler broadcastHandler;

public void sendMessage(String topic, Object payload, BroadcastMessageType type) {
    broadcastHandler.broadcast(topic, payload, type);
}
```

## 🔧 Testing & Debugging

### Running Tests
```bash
# Backend tests
./gradlew test

# Frontend tests
cd webui && ng test

# E2E tests
cd webui && ng e2e
```

### Common Issues

#### Database Connection
```properties
# application.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/fanroc
spring.datasource.username=postgres
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=validate
```

#### WebSocket Troubleshooting
- Check STOMP endpoint: `/ws`
- Verify topic subscriptions
- Use browser dev tools Network tab
- Test with: `src/test/resources/script/websocket_tester.html`

#### Angular Development
```bash
# Start Angular dev server
cd webui && npm start
# Then build for Spring Boot
ng build --configuration development
```

## 🚀 Deployment

### Production Build
```bash
# Build frontend
cd webui && ng build --configuration production

# Build and run backend
./gradlew bootJar
java -jar build/libs/scoring-system.jar
```

### Docker Deployment
```dockerfile
FROM openjdk:17-alpine
COPY build/libs/scoring-system.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
```

### Environment Variables
```
SPRING_PROFILES_ACTIVE=production
SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/fanroc
SPRING_DATASOURCE_USERNAME=...
SPRING_DATASOURCE_PASSWORD=...
```

## 📊 Monitoring & Logging

### Key Metrics
- WebSocket connections active
- API response times
- Match completion rates
- Score submission errors

### Log Levels
```properties
logging.level.org.thingai=DEBUG
logging.level.org.springframework.web.socket=TRACE
logging.level.org.springframework.security=WARN
```

### Monitoring Endpoints
- `/actuator/health` - Application health
- `/actuator/metrics` - System metrics
- `/actuator/websocket` - WebSocket diagnostics (custom)

---

## 🐛 Common Development Pitfalls

### Multi-step Scoring Process
**Mistake**: Expecting scores to show immediately after submission
```typescript
// ❌ Wrong - scores won't display yet
refereeService.submitScore(payload).subscribe()
// No "commit" call!
```

```typescript
// ✅ Correct - both submission AND commitment needed
refereeService.submitScore(payload).subscribe(() => {
  // Wait for both alliances, then commit
  scorekeeperService.commitFinalScore().subscribe()
})
```

### WebSocket vs Database State
**Live updates during matches**: Stored in memory (temporary)
**Final committed scores**: Saved to database (permanent)
**Display behavior**: Shows committed scores only

### Permission Levels
```typescript
// Referees cannot override
if (role === 'REFEREE' && action === 'override') {
  throw new Error('Unauthorized');
}
```

## 📚 Resources

- **API Documentation**: Postman collection in `docs/`
- **Database Schema**: ER diagrams in `docs/`
- **Architecture**: See `docs/Fanroc_Project_Reference.md`
- **Testing Data**: `src/test/resources/script/`

---

*Version:* Fanroc 1.0

