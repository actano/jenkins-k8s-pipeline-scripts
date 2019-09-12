/**
 * Creates a Sentry release and uploads the sourcemaps
 */
def createRelease(name) {
    // TODO Integrate after test was successful
    def secrets = [
        [$class: 'VaultSecret', engineVersion: 1, path: 'secret/rplanx/npmjs.com/rplan-ci', secretValues: [
            [$class: 'VaultSecretValue', envVar: 'V_USERNAME', vaultKey: 'username']
        ]]
    ]
    wrap([$class: 'VaultBuildWrapper', vaultSecrets: secrets]) {
        def TEST_USERNAME_1="${env.V_USERNAME}"
        def TEST_USERNAME_2="${V_USERNAME}"

        sh "echo ${TEST_USERNAME_1}"
        sh "echo ${TEST_USERNAME_2}"
        sh "echo \${TEST_USERNAME_2}"
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
