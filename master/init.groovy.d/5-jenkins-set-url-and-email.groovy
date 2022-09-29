#!groovy

// imports
import jenkins.model.Jenkins
import jenkins.model.JenkinsLocationConfiguration

final String email = System.getenv('JENKINS_ADMINISTRATOR_EMAIL')
final String url = System.getenv('JENKINS_URL')

// get Jenkins location configuration
def jenkinsLocationConfiguration = JenkinsLocationConfiguration.get()

// set Jenkins URL
jenkinsLocationConfiguration.setUrl(url)

// set Jenkins admin email address
jenkinsLocationConfiguration.setAdminAddress(email)

// save current Jenkins state to disk
jenkinsLocationConfiguration.save()