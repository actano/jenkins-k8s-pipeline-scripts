def call(name, directory=".", buildArgs = "") {
  gitops.buildImage(name, directory, buildArgs)
  gitops.pushImage(name)
}
