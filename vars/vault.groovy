def withVaultSecrets(secretPath = "secret/rplanx/jenkins", Closure body) {
  def secrets = [
    [$class: 'VaultSecret', engineVersion: 1, path: secretPath, secretValues: [
      [$class: 'VaultSecretValue', envVar: 'VAULT_ADDR', vaultKey: 'vault-address'],
      [$class: 'VaultSecretValue', envVar: 'VAULT_TOKEN', vaultKey: 'vault-token'],
    ]]
  ]

  wrap([$class: 'VaultBuildWrapper', vaultSecrets: secrets]) {
    body()
  }
}
