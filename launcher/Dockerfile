FROM bigtruedata/sbt

RUN mkdir /opt/launcher

WORKDIR /opt/launcher

COPY target/scala-2.12/launcher-assembly-0.1.jar launcher-assembly-0.1.jar

ENV TZ Asia/Tokyo

ENTRYPOINT ["java", "-Xms512m", "-Xms64m", "-jar", "launcher-assembly-0.1.jar"]
