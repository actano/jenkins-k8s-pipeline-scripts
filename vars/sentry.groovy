
def createRelease(name) {

    // vault
    // todo get from vault
    def SENTRY_API_KEY="4e6e8ebddbe94465989b3f4efe801a355db70615472549399446e52807b759c5"
    def SENTRY_ORG="actano-gmbh"
    def SENTRY_PROJECT="allex-webapp"

    // local
    def SENTRY_RELEASE="${env.GIT_COMMIT}"
    def IMAGE_NAME="${gitops.imageRef(name)}"

    // install sentry
    sh "npm init -y"
    sh "npm i @sentry/cli"

    // create container and get sourcemaps
    sh "docker create --name sourcemaps_data ${IMAGE_NAME}"
    sh "docker cp sourcemaps_data:/opt/actano/rplan/build/client/index.js.map ./index.js.map"
    sh "docker rm sourcemaps_data"

    // create release
    sh "npx sentry-cli --auth-token=${SENTRY_API_KEY} releases --org=${SENTRY_ORG} --project=${SENTRY_PROJECT} new ${SENTRY_RELEASE}"

    // upload sourcemaps
    sh "npx sentry-cli --auth-token=${SENTRY_API_KEY} releases --org=${SENTRY_ORG} --project=${SENTRY_PROJECT} files ${SENTRY_RELEASE} upload-sourcemaps ./index.js.map"

}
