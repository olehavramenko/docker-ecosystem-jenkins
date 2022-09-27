FROM jenkins/jenkins:2.370-jdk11

ENV JAVA_OPTS -Djenkins.install.runSetupWizard=false -Dorg.jenkinsci.plugins.durabletask.BourneShellScript.LAUNCH_DIAGNOSTICS=true

COPY ./plugins.txt /usr/share/jenkins/plugins.txt
RUN cat /usr/share/jenkins/plugins.txt | xargs jenkins-plugin-cli --plugins

COPY --chown=jenkins:jenkins ./init.groovy.d/ /usr/share/jenkins/ref/init.groovy.d/
