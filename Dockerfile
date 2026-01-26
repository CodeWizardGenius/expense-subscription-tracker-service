# Multi-stage Dockerfile for Render.com
# Build stage: use Maven to build the fat jar
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /app

# Copy only Maven files first to leverage Docker cache
COPY pom.xml ./
COPY src ./src

# Build the project (skip tests to speed up). Produces target/*.jar
RUN mvn -B -DskipTests package

# Run stage: slim runtime image
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy jar from build stage (pick the first jar in target)
COPY --from=build /app/target/*.jar app.jar

# Expose port (Render will map PORT env variable)
EXPOSE 1881

# Set JVM options for production (can be overridden via Docker env)
ENV JAVA_OPTS="-Xms256m -Xmx512m"

# Use PORT env variable provided by Render; fallback to 1881
ENV PORT=1881

# Healthcheck (optional) — simple curl to the root
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
  CMD wget -qO- http://localhost:${PORT:-1881}/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT} -jar /app/app.jar"]
