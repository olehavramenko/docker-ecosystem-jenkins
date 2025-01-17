#!/bin/sh

# JENKINS_MASTER_HOST_INTERNAL=jenkins-master
# JENKINS_MASTER_PORT=8080
# NAME=docker-agent-1

export NAME
envsubst < node.xml.template > node.xml

./wait-for-it.sh "${JENKINS_MASTER_HOST_INTERNAL}:${JENKINS_MASTER_PORT}"

while sleep 10
do
  curl \
    -u ${JENKINS_AGENT_USERNAME}:${JENKINS_AGENT_PASSWORD} \
    "http://${JENKINS_MASTER_HOST_INTERNAL}:${JENKINS_MASTER_PORT}/jnlpJars/jenkins-cli.jar" \
    --output jenkins-cli.jar

  cat node.xml | \
  java \
    -jar jenkins-cli.jar \
    -s "http://${JENKINS_MASTER_HOST_INTERNAL}:${JENKINS_MASTER_PORT}" \
    -auth ${JENKINS_AGENT_USERNAME}:${JENKINS_AGENT_PASSWORD} \
    create-node "${NAME}"

  curl \
    -u ${JENKINS_AGENT_USERNAME}:${JENKINS_AGENT_PASSWORD} \
    "http://${JENKINS_MASTER_HOST_INTERNAL}:${JENKINS_MASTER_PORT}/jnlpJars/slave.jar" \
    --output slave.jar

  java \
    -jar slave.jar \
    -jnlpCredentials ${JENKINS_AGENT_USERNAME}:${JENKINS_AGENT_PASSWORD} \
    -jnlpUrl "http://${JENKINS_MASTER_HOST_INTERNAL}:${JENKINS_MASTER_PORT}/computer/${NAME}/slave-agent.jnlp"
done
