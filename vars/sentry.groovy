/**
 * Creates a Sentry release and uploads the sourcemaps
 */
def createRelease(name) {
    // TODO Integrate after test was successful
    def secrets = [
        [$class: 'VaultSecret', engineVersion: 1, path: 'secret/rplanx/sentry', secretValues: [
            [$class: 'VaultSecretValue', envVar: 'V_SENTRY_API_KEY', vaultKey: 'token'],
            [$class: 'VaultSecretValue', envVar: 'V_SENTRY_ORG', vaultKey: 'organization'],
            [$class: 'VaultSecretValue', envVar: 'V_SENTRY_PROJECT', vaultKey: 'project']
        ]]
    ]
    wrap([$class: 'VaultBuildWrapper', vaultSecrets: secrets]) {
        def TEST_V_SENTRY_API_KEY="${V_SENTRY_API_KEY}"
        def TEST_V_SENTRY_ORG="${V_SENTRY_ORG}"
        def TEST_V_SENTRY_PROJECT="${V_SENTRY_PROJECT}"

        sh "echo ${TEST_V_SENTRY_API_KEY}"
        sh "echo ${TEST_V_SENTRY_ORG}"
        sh "echo ${TEST_V_SENTRY_PROJECT}"
    }

    // vault
    // todo get from vault
    def SENTRY_API_KEY="4e6e8ebddbe94465989b3f4efe801a355db70615472549399446e52807b759c5"
    def SENTRY_ORG="actano-gmbh"
    def SENTRY_PROJECT="allex-webapp"

    // local
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
    sh "docker rm sourcemaps_data"

    // create release
    sh "cd npm-sentry && npx sentry-cli --auth-token=${SENTRY_API_KEY} releases --org=${SENTRY_ORG} --project=${SENTRY_PROJECT} new ${SENTRY_RELEASE}"

    // upload sourcemaps
    sh "cd npm-sentry && npx sentry-cli --auth-token=${SENTRY_API_KEY} releases --org=${SENTRY_ORG} --project=${SENTRY_PROJECT} files ${SENTRY_RELEASE} upload-sourcemaps ../sourcemaps-files --rewrite"
}
