FROM maven:3.8.5-openjdk-18-slim as yggdrasil-corec

# Initialize the container with dirs
RUN mkdir /opt/build
RUN mkdir /opt/app
RUN mkdir /opt/app/config
# Build the project
WORKDIR /opt/build
COPY . .
RUN mvn clean install -q
# Copy the application to the app folder
WORKDIR /opt/app
RUN cp /opt/build/yggdrasil-node/target/yggdrasil-*.jar .
WORKDIR /opt/app/config
RUN cp /opt/build/yggdrasil-node/target/classes/application.yml .
# Clean up the container
WORKDIR /opt/app
RUN rm -r /opt/build
# Run the app
CMD java -jar -Dspring.loader="/opt/app/config" /opt/app/yggdrasil-*.jar