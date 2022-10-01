#!/bin/bash

# Master
export JENKINS_MASTER_SERVICE_NAME=jenkins-master
export JENKINS_PUBLIC_HOSTNAME=localhost
export JENKINS_ADMINISTRATOR_EMAIL=admin@mail.com
export JENKINS_MASTER_PORT=8080
export JENKINS_AGENT_PORT=50000
export JENKINS_TIMEZONE=UTC
export JENKINS_MASTER_DOCKER_VOLUME=jenkins_master_data

# Agents
export JENKINS_AGENT_NAME=docker-agent-1
export JENKINS_AGENT_DOCKER_VOLUME=docker_agent_1_data
# cat /etc/group and get docker group id
export LOCAL_DOCKER_GROUP_ID=998

# Backups
export JENKINS_BACKUP_RETENTION_DAYS=7
export JENKINS_BACKUP_CRON_EXPRESSION="0 */6 * * *"
export JENKINS_BACKUP_LOCAL_PATH=/opt/jenkins-backups

readonly output_compose=docker-compose-main-generated.yml
readonly template_compose=docker-compose-main.template

success() {
  printf '%b\n' ""
  printf '%b\n' "\033[1;32m[SUCCESS] $@\033[0m"
  printf '%b\n' ""
}

err() {
  printf '%b\n' ""
  printf '%b\n' "\033[1;31m[ERROR] $@\033[0m"
  printf '%b\n' ""
  exit 1
} >&2

# Generate docker compose from template.
envsubst < ${template_compose} > ${output_compose}

# Check if jenkins containers are running.
docker compose -f ${output_compose} ps --services --filter "status=running" | grep "${JENKINS_MASTER_SERVICE_NAME}\|${JENKINS_AGENT_NAME}"> /dev/null
if [[ "$?" -ne 1 ]]; then
    docker compose -f ${output_compose} stop
    success "Jenkins containers stopped."
else 
  echo "Jenkins containers are not running..."
fi

# Start Jenkins containers.
echo "Starting Jenkins containers..."
docker compose -f ${output_compose} up --build -d
docker compose -f ${output_compose} ps --services --filter "status=running" | grep "${JENKINS_MASTER_SERVICE_NAME}\|${JENKINS_AGENT_NAME}"> /dev/null
if [[ "$?" -ne 1 ]]; then
    success "Jenkins containers started successful. Server available by ${JENKINS_PUBLIC_HOSTNAME}:${JENKINS_MASTER_PORT}"
else
    err "Could not start Jenkins containers"
fi