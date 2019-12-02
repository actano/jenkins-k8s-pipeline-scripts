def withPactBrokerCredentials(secretPath = "secret/rplanx/gke-dev/pact-broker", Closure body) {
  def secrets = [
    [$class: 'VaultSecret', engineVersion: 1, path: secretPath, secretValues: [
      [$class: 'VaultSecretValue', envVar: 'PACT_BROKER_URL', vaultKey: 'url'],
      [$class: 'VaultSecretValue', envVar: 'PACT_BROKER_USERNAME', vaultKey: 'username'],
      [$class: 'VaultSecretValue', envVar: 'PACT_BROKER_PASSWORD', vaultKey: 'password']
    ]]
  ]

  wrap([$class: 'VaultBuildWrapper', vaultSecrets: secrets]) {
    body()
  }
}

def withJenkinsCredentials(secretPath = "secret/rplanx/jenkins/pact-broker", Closure body) {
  def secrets = [
    [$class: 'VaultSecret', engineVersion: 1, path: secretPath, secretValues: [
      [$class: 'VaultSecretValue', envVar: 'JENKINS_USERNAME', vaultKey: 'username'],
      [$class: 'VaultSecretValue', envVar: 'JENKINS_TOKEN', vaultKey: 'token']
    ]]
  ]

  wrap([$class: 'VaultBuildWrapper', vaultSecrets: secrets]) {
    body()
  }
}
