# 使用一个包含 JDK 的基础镜像 (根据你的 Spring Boot 版本选择合适的 JDK 版本)
FROM amazoncorretto:17-alpine-jdk AS builder

# 设置工作目录
WORKDIR /app

# 复制 Maven Wrapper 和 pom.xml 文件，以便可以下载依赖项
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# 下载项目依赖 (利用 Docker 的层缓存)
RUN ./mvnw dependency:go-offline -B

# 复制源代码
COPY src ./src

# 打包应用程序 (生成 JAR 文件)
RUN ./mvnw package -DskipTests

# --- 第二阶段：创建运行镜像 ---
# 使用一个更小的 JRE 基础镜像来运行应用
FROM amazoncorretto:17-alpine-jdk

WORKDIR /app

# 从构建阶段复制 JAR 文件
# 注意：这里的路径取决于你的 pom.xml 中 <build><finalName> 的配置
# 通常是 target/your-app-name-version.jar
# 如果你不确定，可以先构建一次，然后查看 target 目录
COPY --from=builder /app/target/realtime-feedback-0.0.1-SNAPSHOT.jar app.jar
# 确保将 realtime-feedback-0.0.1-SNAPSHOT.jar 替换为你实际的 JAR 文件名

# 暴露应用程序运行的端口 (如果你的 Spring Boot 应用监听 8080)
EXPOSE 8080

# 运行应用程序的命令
ENTRYPOINT ["java", "-jar", "app.jar"]
