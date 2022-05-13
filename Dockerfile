FROM ibm-semeru-runtimes:open-11-jdk as yggdrasil-corec

# Initialize the container with dirs
RUN mkdir /opt/build
RUN mkdir /opt/build/maven
RUN mkdir /opt/app
RUN mkdir /opt/app/config
# Setup the maven compiler
WORKDIR /opt/build
RUN curl -LJo maven.tar.gz https://dlcdn.apache.org/maven/maven-3/3.8.5/binaries/apache-maven-3.8.5-bin.tar.gz
RUN tar -xzf maven.tar.gz -C /opt/build/maven
# Build the project
WORKDIR /opt/build
COPY . .
RUN /opt/build/maven/apache-maven-3.8.5/bin/mvn clean install -q
# Copy the application to the app folder
WORKDIR /opt/app
RUN cp /opt/build/yggdrasil-node/target/yggdrasil-*.jar .
WORKDIR /opt/app/config
RUN cp /opt/build/yggdrasil-node/target/classes/application.yml .
# Clean up the container
WORKDIR /opt/app
RUN rm -r /opt/build
# Run the app
CMD java -jar -Dspring.loader="/opt/app/config" -Dspring.profiles.active="headless" /opt/app/yggdrasil-*.jar