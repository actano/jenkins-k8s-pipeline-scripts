def info(message) {
    echo "INFO: ${message}"
}

def warning(message) {
    echo "WARNING: ${message}"
}
def imageRef(name) {
  String imageName = "${env.DOCKER_REGISTRY}/${env.GOOGLE_PROJECT}/${name}"
  String tag

  if (env.BRANCH_NAME == "master") {
    tag = "1.0.0-${env.BUILD_NUMBER}"
  } else {
    tag = "${env.BRANCH_NAME}-${env.GIT_COMMIT}"
  }

  return "${imageName}:${tag}"
}
