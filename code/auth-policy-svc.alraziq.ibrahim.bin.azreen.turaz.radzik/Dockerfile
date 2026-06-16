# FROM registry-intl.ap-southeast-3.aliyuncs.com/myasnb/myasnb-app:BASE.v1
FROM  registry-intl.ap-southeast-3.aliyuncs.com/myasnb/zulu-openjdk-debian:8u402-jre

ENV JAR=AuthPolicyService-1.0-SNAPSHOT-all.jar

COPY build/libs/$JAR /opt/app/app.jar
COPY config/config.json /opt/app/config/config.json
#COPY src/main/resources /opt/app/resources
#COPY src/main/c3p0 /opt/app/c3p0
# RUN ["apk", "add", "-U", "tzdata"]
# RUN ["cp", "/usr/share/zoneinfo/Asia/Kuala_Lumpur", "/etc/localtime"]
# RUN ["apk", "add", "stunnel"]
RUN ["apt-get","update"]
RUN ["apt-get","-y","install","stunnel4"]
EXPOSE 8080
EXPOSE 5099
WORKDIR /opt/app
ENTRYPOINT ["java", "-XX:+UnlockExperimentalVMOptions","-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5099", "-XX:+UseCGroupMemoryLimitForHeap", "-Dvertx.cacheDirBase=/tmp","-XX:+UseG1GC","-Xmx5g","-Xms50m", "-jar", "/opt/app/app.jar"]
