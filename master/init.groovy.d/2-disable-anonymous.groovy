import jenkins.model.*
import hudson.security.*

def instance = Jenkins.getInstance()

// Set Auth to Full Control Once Logged In and prevent read-only access
def strategy = new FullControlOnceLoggedInAuthorizationStrategy()
strategy.setAllowAnonymousRead(false)
instance.setAuthorizationStrategy(strategy)

instance.save()