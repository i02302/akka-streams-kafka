FROM bigtruedata/sbt

RUN mkdir /opt/receiver

WORKDIR /opt/receiver

COPY target/scala-2.12/receiver-assembly-0.1.jar receiver-assembly-0.1.jar

ENV TZ Asia/Tokyo

ENTRYPOINT ["java", "-Xms512m", "-Xms64m", "-jar", "receiver-assembly-0.1.jar"]

EXPOSE 5000
