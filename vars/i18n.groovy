def deployi18nScripts(webclientName, version="\$(cat ./package.json | jq -r '.version')", folderPath="./static/i18n", bucketPath="gs://allex-cdn/i18n") {
  def fullPath = "${bucketPath}/${webclientName}/${version}"
  def versionExits = sh script:"gsutil ls ${fullPath}", returnStatus:true
  if (versionExits != 0) {
    sh "gsutil cp -r ${folderPath} ${fullPath}"
  } else {
    error("Build failed as the translations for this already exist in the cdn bucket")
  }
}
