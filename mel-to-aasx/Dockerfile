# Define which Docker image to use as base:
FROM openjdk:16.0.1-jdk-buster

#
# Install OS packages & Python modules:
#
RUN apt-get update && apt-get install -y \
  python3 \
  python3-pip \
  && rm -rf /var/lib/apt/lists/*
RUN pip3 install pyi40aas
RUN pip3 install rdflib

#
# Create directory structure:
#
RUN mkdir -p /temp
RUN mkdir -p /config
RUN mkdir -p /config/excel
RUN mkdir -p /config/ottr/tpl
RUN mkdir -p /config/bin

#
# Copy files:
#
COPY prefixes.ttl /config/
COPY excel/MEL_TOC.xlsx /config/excel/
ARG src="excel/MEL Mapping.xlsx"
COPY ${src} /config/excel/
COPY excel/TemplateRegister.xlsx /config/excel/
COPY bin/lutra.jar /config/bin/
COPY python/rdf-to-aas.py /config/bin/
COPY ottr/tpl/ /config/ottr/tpl/

#
# Create application.properties:
#
ARG APP_PROPERTIES=/config/application.properties
RUN touch ${APP_PROPERTIES}
RUN echo "logging.level.com.aibel.mel.mel2aas=DEBUG" >> ${APP_PROPERTIES}
RUN echo "melws.temporaryDirectory=/temp" >> ${APP_PROPERTIES}
RUN echo "melws.temporaryDirectoryPurgeFiles=true" >> ${APP_PROPERTIES}
RUN echo "melws.prefixFile=/config/prefixes.ttl" >> ${APP_PROPERTIES}
RUN echo "melws.tocFile=/config/excel/MEL_TOC.xlsx" >> ${APP_PROPERTIES}
RUN echo "melws.mapFile=/config/excel/MEL Mapping.xlsx" >> ${APP_PROPERTIES}
RUN echo "melws.tplRegFile=/config/excel/TemplateRegister.xlsx" >> ${APP_PROPERTIES}
RUN echo "melws.pathToTemplateLibrary=/config/ottr/tpl" >> ${APP_PROPERTIES}
RUN echo "melws.javaCommand=java" >> ${APP_PROPERTIES}
RUN echo "melws.lutraCommand=\${melws.javaCommand} -jar /config/bin/lutra.jar" >> ${APP_PROPERTIES}
RUN echo "melws.pythonCommand=python3" >> ${APP_PROPERTIES}
RUN echo "melws.rdfToAasxCommand=\${melws.pythonCommand} /config/bin/rdf-to-aas.py" >> ${APP_PROPERTIES}
RUN cat ${APP_PROPERTIES}

#
# Prepare Spring Boot Application:
#
ARG JAR_FILE=java-code/MEL2AAS/target/*.jar
COPY ${JAR_FILE} /webservice.jar

#
# Startup command:
#
ENTRYPOINT ["java","-jar","/webservice.jar"]