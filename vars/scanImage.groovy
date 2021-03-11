def call(imageName) {
    String imageRef = gitops.imageRef(imageName)
    String tag = gitops.imageTag()
    String scanId = "${imageName}-${tag}"
    byTag(imageRef, imageName, scanId)
}

def byTag(imageRef, imageName, scanId) {
    String ts = new Date().format("yyyyMMddHHmm")
    String scanResultFile = "scan-${scanId}-${ts}.txt"
    sh "grype \"${imageRef}\" 2>&1 | ([ ! -f \"./grype-blacklist.txt\" ] && cat || grep -v -f grype-blacklist.txt) | tee \"${scanResultFile}\""
    sh "egrep -o \"Critical|High\" \"${scanResultFile}\" | sort | uniq -c | tee -a \"${scanResultFile}\""
    sh "gsutil cp \"${scanResultFile}\" \"gs://allex-image-scan-results/${imageName}/${scanResultFile}\""
}

def archive() {
    archiveArtifacts artifacts: 'scan-*.txt', allowEmptyArchive: true, onlyIfSuccessful: true
}
