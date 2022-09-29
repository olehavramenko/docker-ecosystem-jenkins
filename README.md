## Deployment

### Setup

#### Environment variables

The following environment variables are required for this project:

- JENKINS_ADMINISTRATOR_USERNAME: The Jenkins administrator username
- JENKINS_ADMINISTRATOR_PASSWORD: The Jenkins administrator password

For convenience, an `.env.template` file has been included in the repository with the required placeholders.

Copy the template:

```bash
cp .env.template .env
```

And populate the variables with appropriate values. Docker Compose will use that file to ensure the container has the required variables.

#### Docker

This project is deployed with Docker. The easiest way to setup a Docker environment is by installing the [Docker Toolbox](https://www.docker.com/docker-toolbox).

### Running

```bash
export $(cat .env | xargs) && docker-compose up
```
