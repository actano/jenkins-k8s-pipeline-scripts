def call(imageName) {
    String img = gitops.imageRef(imageName)
    String tag = gitops.imageTag()
    String scanResultFile = "scan-${poServiceImageName}-${tag}.txt"
    sh "grype \"${img}\" 2>&1 | tee \"${scanResultFile}\""
    sh "gsutil cp \"${scanResultFile}\" \"gs://allex-image-scan-results/${imageName}/${scanResultFile}\""
}

def archive() {
    archiveArtifacts artifacts: 'scan-*.txt'
}
