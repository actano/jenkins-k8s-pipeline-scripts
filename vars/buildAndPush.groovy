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

def buildImage(name) {
  sh "docker build -t ${imageRef(name)} ."
}

def pushImage(name) {
  sh "docker push ${imageRef(name)}"
}

def call(name) {
  buildImage(name)
  pushImage(name)
}
