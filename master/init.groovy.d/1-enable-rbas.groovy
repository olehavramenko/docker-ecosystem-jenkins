import hudson.*
import hudson.model.*
import hudson.security.*
import jenkins.*
import jenkins.model.*
import java.util.*
import com.michelin.cio.hudson.plugins.rolestrategy.*
import com.synopsys.arc.jenkins.plugins.rolestrategy.RoleType;
import java.lang.reflect.*
import java.util.logging.*
import groovy.json.*

def env = System.getenv()

// Roles
// def globalRoleRead = "read"
def globalRoleAdmin = "admin"
def globalRoleRemoteAgent = "remote_agent"

def jenkinsInstance = Jenkins.getInstance()
def currentAuthenticationStrategy = Hudson.instance.getAuthorizationStrategy()

Thread.start {
    sleep 15000
    if (currentAuthenticationStrategy instanceof RoleBasedAuthorizationStrategy) {
      println "Role based authorisation already enabled."
      println "Exiting script..."
      return
    } else {
      println "Enabling role based authorisation strategy..."
    }

    // Set new authentication strategy
    RoleBasedAuthorizationStrategy roleBasedAuthenticationStrategy = new RoleBasedAuthorizationStrategy()
    jenkinsInstance.setAuthorizationStrategy(roleBasedAuthenticationStrategy)

    Constructor[] constrs = Role.class.getConstructors();
    for (Constructor<?> c : constrs) {
      c.setAccessible(true);
    }

    // Create admin set of permissions
    Set<Permission> adminPermissions = new HashSet<Permission>();
    adminPermissions.add(Permission.fromId("hudson.model.View.Delete"));
    adminPermissions.add(Permission.fromId("hudson.model.Computer.Connect"));
    adminPermissions.add(Permission.fromId("hudson.model.Run.Delete"));
    adminPermissions.add(Permission.fromId("hudson.model.Hudson.UploadPlugins"));
    adminPermissions.add(Permission.fromId("com.cloudbees.plugins.credentials.CredentialsProvider.ManageDomains"));
    adminPermissions.add(Permission.fromId("hudson.model.Computer.Create"));
    adminPermissions.add(Permission.fromId("hudson.model.View.Configure"));
    adminPermissions.add(Permission.fromId("hudson.model.Hudson.ConfigureUpdateCenter"));
    adminPermissions.add(Permission.fromId("hudson.model.Computer.Build"));
    adminPermissions.add(Permission.fromId("hudson.model.Item.Configure"));
    adminPermissions.add(Permission.fromId("hudson.model.Hudson.Administer"));
    adminPermissions.add(Permission.fromId("hudson.model.Item.Cancel"));
    adminPermissions.add(Permission.fromId("hudson.model.Item.Read"));
    adminPermissions.add(Permission.fromId("com.cloudbees.plugins.credentials.CredentialsProvider.View"));
    adminPermissions.add(Permission.fromId("hudson.model.Computer.Delete"));
    adminPermissions.add(Permission.fromId("hudson.model.Item.Build"));
    adminPermissions.add(Permission.fromId("hudson.scm.SCM.Tag"));
    adminPermissions.add(Permission.fromId("hudson.model.Item.Discover"));
    adminPermissions.add(Permission.fromId("hudson.model.Hudson.Read"));
    adminPermissions.add(Permission.fromId("com.cloudbees.plugins.credentials.CredentialsProvider.Update"));
    adminPermissions.add(Permission.fromId("hudson.model.Item.Create"));
    adminPermissions.add(Permission.fromId("hudson.model.Item.Move"));
    adminPermissions.add(Permission.fromId("hudson.model.Item.Workspace"));
    adminPermissions.add(Permission.fromId("com.cloudbees.plugins.credentials.CredentialsProvider.Delete"));
    adminPermissions.add(Permission.fromId("hudson.model.View.Read"));
    adminPermissions.add(Permission.fromId("hudson.model.Hudson.RunScripts"));
    adminPermissions.add(Permission.fromId("hudson.model.View.Create"));
    adminPermissions.add(Permission.fromId("hudson.model.Item.Delete"));
    adminPermissions.add(Permission.fromId("hudson.model.Computer.Configure"));
    adminPermissions.add(Permission.fromId("com.cloudbees.plugins.credentials.CredentialsProvider.Create"));
    adminPermissions.add(Permission.fromId("hudson.model.Computer.Disconnect"));
    adminPermissions.add(Permission.fromId("hudson.model.Run.Update"));

    // Create the admin Role
    Role adminRole = new Role(globalRoleAdmin, adminPermissions);
    roleBasedAuthenticationStrategy.addRole(RoleType.fromString(RoleBasedAuthorizationStrategy.GLOBAL), adminRole);

    //------------------------------------------------------------------------------------------

    // Create set of permissions for remote_agent
    Set<Permission> remoteAgentPermissions = new HashSet<Permission>();
    remoteAgentPermissions.add(Permission.fromId("hudson.model.Hudson.Read"));
    remoteAgentPermissions.add(Permission.fromId("hudson.model.Computer.Connect"));
    remoteAgentPermissions.add(Permission.fromId("hudson.model.Computer.Create"));
    remoteAgentPermissions.add(Permission.fromId("hudson.model.Computer.Build"));
    remoteAgentPermissions.add(Permission.fromId("hudson.model.Computer.Delete"));
    remoteAgentPermissions.add(Permission.fromId("hudson.model.Computer.Configure"));
    remoteAgentPermissions.add(Permission.fromId("hudson.model.Computer.Disconnect"));

    // Create the Role for remote_agent
    Role remoteAgentRole = new Role(globalRoleRemoteAgent, remoteAgentPermissions);
    roleBasedAuthenticationStrategy.addRole(RoleType.fromString(RoleBasedAuthorizationStrategy.GLOBAL), remoteAgentRole);

    // --------------------------------------------------------------------------------------------------------

    // Creating admin user
    def hudsonRealm = new HudsonPrivateSecurityRealm(false)
    hudsonRealm.createAccount(env.JENKINS_ADMINISTRATOR_USERNAME, env.JENKINS_ADMINISTRATOR_PASSWORD)
    jenkinsInstance.setSecurityRealm(hudsonRealm)

    // Assign the role
    roleBasedAuthenticationStrategy.assignRole(RoleType.fromString(RoleBasedAuthorizationStrategy.GLOBAL), adminRole, env.JENKINS_ADMINISTRATOR_USERNAME);
    println "Admin role created...OK"

    // --------------------------------------------------------------------------------------------------------

    // Creating remote_agent user
    def hudsonRealm_2 = new HudsonPrivateSecurityRealm(false)
    hudsonRealm_2.createAccount(env.JENKINS_AGENT_USERNAME, env.JENKINS_AGENT_PASSWORD)
    jenkinsInstance.setSecurityRealm(hudsonRealm_2)

    // Assign the role
    roleBasedAuthenticationStrategy.assignRole(RoleType.fromString(RoleBasedAuthorizationStrategy.GLOBAL), remoteAgentRole, env.JENKINS_AGENT_USERNAME);
    println "Remote Agent role created...OK"


    // Save the state
    println "Saving changes."
    jenkinsInstance.save()
}