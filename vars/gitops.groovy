def imageRef(name, prefix="${env.BRANCH_NAME}") {
  String imageName = "${env.DOCKER_REGISTRY}/${env.GOOGLE_PROJECT}/${name}"
  String tag = "${prefix}-${env.GIT_COMMIT}"
  return "${imageName}:${tag}"
}

def buildImage(name, buildArgs = "") {
  sh "docker build -t ${imageRef(name)} --build-arg GIT_COMMIT=${env.GIT_COMMIT} ${buildArgs} ."
}

def pushImage(name) {
  sh "docker push ${imageRef(name)}"
}
