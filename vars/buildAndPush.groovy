def call(name, buildArgs = "") {
  gitops.buildImage(name, buildArgs)
  gitops.pushImage(name)
}
