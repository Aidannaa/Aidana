import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class SessionRegistry {
    private SessionRegistry() {}

    private static class Holder {
        private static final SessionRegistry INSTANCE = new SessionRegistry();
    }

    public static SessionRegistry getInstance() {
        return Holder.INSTANCE;
    }

    private final ConcurrentMap<String, SessionInfo> sessions = new ConcurrentHashMap<>();

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "session-cleaner");
        t.setDaemon(true);
        return t;
    });

    private volatile boolean cleanupStarted = false;
    public synchronized void startAutoCleanup(long periodMillis, long timeoutMillis) {
        if (cleanupStarted) return;
        if (periodMillis <= 0 || timeoutMillis <= 0) {
            throw new IllegalArgumentException("periodMillis және timeoutMillis > 0 болуы керек");
        }

        cleanupStarted = true;
        scheduler.scheduleAtFixedRate(
                () -> {
                    try {
                        cleanupInactiveSessions(timeoutMillis);
                    } catch (Exception ignored) {
                    }
                },
                periodMillis, periodMillis,
                TimeUnit.MILLISECONDS
        );
    }

    public synchronized void stopAutoCleanup() {
        scheduler.shutdownNow();
        cleanupStarted = false;
    }
    public void beginSession(String userId) {
        Objects.requireNonNull(userId, "userId");

        long now = System.currentTimeMillis();
        sessions.compute(userId, (k, oldVal) -> {
            if (oldVal == null) {
                return new SessionInfo(k, SessionStatus.ACTIVE, now, now, 0L);
            }
            return oldVal.withStatus(SessionStatus.ACTIVE)
                    .withLastActivity(now)
                    .withEndedAt(0L);
        });
    }
    public void terminateSession(String userId) {
        Objects.requireNonNull(userId, "userId");

        long now = System.currentTimeMillis();
        sessions.computeIfPresent(userId, (k, oldVal) ->
                oldVal.withStatus(SessionStatus.TERMINATED)
                        .withEndedAt(now)
        );
    }
    public void touchSession(String userId) {
        Objects.requireNonNull(userId, "userId");

        long now = System.currentTimeMillis();
        sessions.computeIfPresent(userId, (k, oldVal) -> {
            if (oldVal.status == SessionStatus.TERMINATED || oldVal.status == SessionStatus.EXPIRED) {
                return oldVal;
            }
            return oldVal.withLastActivity(now);
        });
    }

    public SessionInfo getSessionState(String userId) {
        Objects.requireNonNull(userId, "userId");
        return sessions.get(userId);
    }
    public int cleanupInactiveSessions(long timeoutMillis) {
        if (timeoutMillis <= 0) throw new IllegalArgumentException("timeoutMillis > 0 болуы керек");

        long now = System.currentTimeMillis();
        int removed = 0;

        for (var entry : sessions.entrySet()) {
            String userId = entry.getKey();
            SessionInfo st = entry.getValue();

            if (st.status == SessionStatus.TERMINATED) {
                if (sessions.remove(userId, st)) removed++;
                continue;
            }

            long inactiveFor = now - st.lastActivityAt;

            if (inactiveFor > timeoutMillis) {

                SessionInfo expired = st.withStatus(SessionStatus.EXPIRED).withEndedAt(now);

                boolean replaced = sessions.replace(userId, st, expired);
                if (replaced) {
                    if (sessions.remove(userId, expired)) removed++;
                }
            }
        }

        return removed;
    }

    public int getActiveSessionCount() {
        return sessions.size();
    }

    public enum SessionStatus {
        ACTIVE,
        TERMINATED,
        EXPIRED
    }

    public static final class SessionInfo {
        public final String userId;
        public final SessionStatus status;
        public final long createdAt;
        public final long lastActivityAt;
        public final long endedAt;
        public SessionInfo(String userId, SessionStatus status, long createdAt, long lastActivityAt, long endedAt) {
            this.userId = userId;
            this.status = status;
            this.createdAt = createdAt;
            this.lastActivityAt = lastActivityAt;
            this.endedAt = endedAt;
        }

        public SessionInfo withStatus(SessionStatus newStatus) {
            return new SessionInfo(this.userId, newStatus, this.createdAt, this.lastActivityAt, this.endedAt);
        }

        public SessionInfo withLastActivity(long newLastActivityAt) {
            return new SessionInfo(this.userId, this.status, this.createdAt, newLastActivityAt, this.endedAt);
        }

        public SessionInfo withEndedAt(long newEndedAt) {
            return new SessionInfo(this.userId, this.status, this.createdAt, this.lastActivityAt, newEndedAt);
        }

        @Override
        public String toString() {
            return "SessionInfo{" +
                    "userId='" + userId + '\'' +
                    ", status=" + status +
                    ", createdAt=" + createdAt +
                    ", lastActivityAt=" + lastActivityAt +
                    ", endedAt=" + endedAt +
                    '}';
        }
    }

    public static void main(String[] args) throws InterruptedException {
        SessionRegistry registry = SessionRegistry.getInstance();

        registry.startAutoCleanup(2000, 3000);

        registry.beginSession("Aidana1");
        System.out.println("BEGIN: " + registry.getSessionState("Aidana1"));

        Thread.sleep(1000);
        registry.touchSession("Aidana1");
        System.out.println("TOUCH: " + registry.getSessionState("Aidana1"));

        Thread.sleep(1500);
        System.out.println("STATE: " + registry.getSessionState("Aidana1"));

        Thread.sleep(4000);
        System.out.println("AFTER CLEANUP: " + registry.getSessionState("Aidana1"));

        registry.beginSession("Uldana2");
        registry.terminateSession("Uldana2");
        System.out.println("TERMINATED: " + registry.getSessionState("Uldana2"));

        registry.cleanupInactiveSessions(3000);
        System.out.println("TERMINATED AFTER CLEANUP: " + registry.getSessionState("Uldana2"));
        registry.stopAutoCleanup();
    }
}
