FROM debian:buster-20220912

COPY ./install-docker-compose.sh .
COPY ./install-docker.sh .
COPY ./install-envsubst.sh .
COPY ./install-java.sh .

ARG DEBIAN_FRONTEND=noninteractive

RUN apt-get update -y && \
  apt-get install -y apt-utils && \
  ./install-docker.sh && \
  ./install-docker-compose.sh && \
  ./install-envsubst.sh && \
  ./install-java.sh && \
  useradd -ms /bin/bash jenkins-agent && \
  usermod -aG docker jenkins-agent && \
  apt-get clean && \
  rm -rf /var/lib/apt/lists/*

WORKDIR /jenkins-agent

COPY ./node.xml.template .
COPY ./wait-for-it.sh .
COPY ./entrypoint.sh .

RUN chown -R jenkins-agent:jenkins-agent /jenkins-agent

USER jenkins-agent

ENTRYPOINT ["./entrypoint.sh"]