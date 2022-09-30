## Deployment

## Environment variables

The following environment variables are required for this project:

- JENKINS_ADMINISTRATOR_USERNAME: The Jenkins administrator username
- JENKINS_ADMINISTRATOR_PASSWORD: The Jenkins administrator password
- JENKINS_AGENT_USERNAME: The Jenkins user username for agents
- JENKINS_AGENT_PASSWORD: The Jenkins user pasword for agents

For convenience, an `.env.template` file has been included in the repository with the required placeholders.

Copy the template:

```bash
cp .env.template .env
```

And populate the variables with appropriate values. Docker Compose will use that file to ensure the container has the required variables.

## Docker

This project is deployed with Docker. The easiest way to setup a Docker environment is by installing the [Docker Toolbox](https://www.docker.com/docker-toolbox).

## Running

```bash
./script/start.sh
```

## Revocery from backup
**<TO DO: automate recovery process...

*Steps:*
1. Stop and remove docker containers.
    ```
    docker compose stop -f docker-compose-main.yml
    ```
2. Remove Jenkins master docker volume.
    ```
    docker volume list
    docker volume rm <volume_name>
    ```
3. Untar the backup you want to restore.
    ```
    tar -C /path xvf /path_to_local_backup/backup-<date>.tar.gz
    ```
4. Using a temporary once-off container, mount the volume (the example assumes it's named `<volume_name>`) and copy over the backup. Make sure you copy the correct path level (this depends on how you mount your volume into the backup container), you might need to strip some leading elements.
- Create temp container.
    ```
    docker run -d --name temp_restore_container -v <volume_name>:/var/jenkins_home alpine sleep 100000
    docker cp /path/backup/jenkins-data/ temp_restore_container:/var/jenkins_home
    docker exec -it temp_restore_container sh
    ```
- Run within container.
    ```
    cd /var/jenkins-home/jenkins-data/
    mv /var/jenkins-home/jenkins-data/ /var/jenkins-home
    rm -rf /var/jenkins-home/jenkins-data
    chown -R 1000:1000 /var/jenkins_home
    exit
    ```
- Remove temp container.
    ```
    docker stop temp_restore_container
    docker rm temp_restore_container
    ```
5. Start Jenkins containers.
    ```
    ./scripts/start.sh
    ```