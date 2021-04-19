def imageTag(prefix="${env.BRANCH_NAME}") {
  return "${prefix}-${env.GIT_COMMIT}"
}

def imageRef(name, prefix="${env.BRANCH_NAME}") {
    String imageName = "${env.DOCKER_REGISTRY}/${env.GOOGLE_PROJECT}/${name}"
    String tag = imageTag(prefix)
    return "${imageName}:${tag}"
}

def imageRefArtifactRepo(name, prefix="${env.BRANCH_NAME}") {
  String imageName = "europe-west3-docker.pkg.dev/allex-artifacts/allex-artifacts-docker/${name}"
  String tag = imageTag(prefix)
  return "${imageName}:${tag}"
}

def buildImage(name, directory=".", buildArgs = "") {
  sh "gcloud config set artifacts/location europe-west3"
  sh "gcloud config get-value artifacts/location"
  sh "gcloud auth configure-docker europe-west3-docker.pkg.dev"
  sh "gcloud config list"
  //sh "docker build -t ${imageRef(name)} --build-arg GIT_COMMIT=${env.GIT_COMMIT} ${buildArgs} ${directory}"
  sh "docker build -t ${imageRefArtifactRepo(name)} --build-arg GIT_COMMIT=${env.GIT_COMMIT} ${buildArgs} ${directory}"
}

def pushImage(name) {
  sh "docker push ${imageRef(name)}"
  sh "docker tag ${imageRef(name)} ${imageRefArtifactRepo(name)}"
  sh "docker push ${imageRefArtifactRepo(name)}"
}
