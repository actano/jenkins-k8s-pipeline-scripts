def call(imageName) {
    String img = gitops.imageRef(imageName)
    String tag = gitops.imageTag()
    String ts = new Date().format("yyyyMMddHHmm")
    String scanResultFile = "scan-${imageName}-${tag}-${ts}.txt"
    sh "grype \"${img}\" 2>&1 | ([ ! -f \"./grype-blacklist.txt\" ] && cat || grep -v -f grype-blacklist.txt) | tee \"${scanResultFile}\""
    sh "egrep -o \"Critical|High\" \"${scanResultFile}\" | sort | uniq -c | tee -a \"${scanResultFile}\""
    sh "gsutil cp \"${scanResultFile}\" \"gs://allex-image-scan-results/${imageName}/${scanResultFile}\""
}

def archive() {
    archiveArtifacts artifacts: 'scan-*.txt', allowEmptyArchive: true, onlyIfSuccessful: true
}
