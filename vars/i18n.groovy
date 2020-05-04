def deployi18nScripts(webclientName, folderPath="./static/i18n", bucketPath="gs://allex-cdn/i18n") {
  def fetchVersionBash = "\$(yarn info --version)"
  def fullPath = "${bucketPath}/${webclientName}/${fetchVersionBash}"
  def versionExits = sh script:"gsutil ls ${fullPath}", returnStatus:true
  if (versionExits != 0) {
    sh "gsutil cp -r ${folderPath} ${fullPath}"
  } else {
    error("Build failed as the translations already exist in the cdn bucket")
  }
}
