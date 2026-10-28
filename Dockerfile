# ============================================================
# 多阶段构建:第一阶段用 Maven 打包,第二阶段只留 JRE 运行
# 服务器上不需要装 JDK/Maven,只要有 Docker 即可
# ============================================================

# ---------- 阶段 1:编译打包 ----------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# 先只拷贝 pom.xml,把依赖下载缓存成一个独立层
# 以后只改代码不改依赖时,这一层直接命中缓存,构建快很多
COPY pom.xml .
RUN mvn -B dependency:go-offline

# 再拷贝源码并打包(测试需要数据库,容器构建阶段跳过)
COPY src ./src
RUN mvn -B package -DskipTests

# ---------- 阶段 2:运行 ----------
FROM eclipse-temurin:17-jre
WORKDIR /app

# 从构建阶段只拿最终的 jar,镜像体积从 ~800MB 降到 ~300MB
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8089
ENTRYPOINT ["java", "-jar", "app.jar"]
