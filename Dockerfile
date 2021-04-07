ARG APP_INSIGHTS_AGENT_VERSION=2.5.1

# Application image

FROM hmctspublic.azurecr.io/base/java:openjdk-11-distroless-1.2

COPY lib/AI-Agent.xml /opt/app/
COPY lib/applicationinsights-agent-2.5.1.jar /opt/app/
COPY build/libs/em-hrs-api.jar /opt/app/

EXPOSE 8080
CMD [ "em-hrs-api.jar" ]
