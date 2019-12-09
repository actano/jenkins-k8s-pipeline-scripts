/**
 * Creates a Sentry release and uploads the sourcemaps
 */
def createRelease(name) {
    def secrets = [
        [$class: 'VaultSecret', engineVersion: 1, path: 'secret/rplanx/sentry', secretValues: [
            [$class: 'VaultSecretValue', envVar: 'SENTRY_AUTH_TOKEN', vaultKey: 'token'],
            [$class: 'VaultSecretValue', envVar: 'SENTRY_ORG', vaultKey: 'organization'],
            [$class: 'VaultSecretValue', envVar: 'SENTRY_PROJECT', vaultKey: 'project']
        ]]
    ]
    wrap([$class: 'VaultBuildWrapper', vaultSecrets: secrets]) {
        def SENTRY_RELEASE="${env.GIT_COMMIT}"
        def IMAGE_NAME="${gitops.imageRef(name)}"

        // install sentry
        sh "mkdir -p npm-sentry"
        sh "cd npm-sentry && npm init -y"
        sh "cd npm-sentry && npm i @sentry/cli"

        // create container and get sourcemaps
        sh "mkdir -p sourcemaps-files"
        sh "docker create --name sourcemaps_data ${IMAGE_NAME}"
        sh "docker cp sourcemaps_data:/opt/actano/rplan/build/client/index.js.map ./sourcemaps-files/index.js.map"
        sh "docker cp sourcemaps_data:/opt/actano/rplan/build/client/index.js ./sourcemaps-files/index.js"
        sh "docker rm sourcemaps_data"

        // create release
        sh "cd npm-sentry && npx sentry-cli --auth-token=${SENTRY_AUTH_TOKEN} releases --org=${SENTRY_ORG} --project=${SENTRY_PROJECT} new ${SENTRY_RELEASE}"

        // upload sourcemaps
        sh "cd npm-sentry && npx sentry-cli --auth-token=${SENTRY_AUTH_TOKEN} releases --org=${SENTRY_ORG} --project=${SENTRY_PROJECT} files ${SENTRY_RELEASE} upload-sourcemaps ../sourcemaps-files --rewrite"

        // associate release with commits from the GitHub repository
        sh "cd npm-sentry && npx sentry-cli --auth-token=${SENTRY_AUTH_TOKEN} releases --org=${SENTRY_ORG} --project=${SENTRY_PROJECT} set-commits ${SENTRY_RELEASE} --auto"
    }
}
