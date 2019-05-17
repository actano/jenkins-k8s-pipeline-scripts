def withAuth(String registry = "registry.npmjs.org", String secretPath = "secret/rplanx/npmjs.com/rplan-ci", String secretKey = "publish-token", Closure body) {
  def secrets = [
    [$class: 'VaultSecret', path: secretPath, secretValues: [
      [$class: 'VaultSecretValue', envVar: 'NPM_TOKEN', vaultKey: secretKey]
    ]]
  ]

  wrap([$class: 'VaultBuildWrapper', vaultSecrets: secrets]) {
    body()
  }
}

def publish(String registry = "registry.npmjs.org", String secretPath = "secret/rplanx/npmjs.com/rplan-ci", String secretKey = "publish-token") {
  withAuth(registry, secretPath, secretKey) {
    sh "npm publish"
  }
}
