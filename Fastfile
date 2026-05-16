default_platform(:android)

platform :android do
  desc "Distribute to internal testers via Firebase App Distribution"
  lane :distribute_internal do
    firebase_app_distribution(
      app: ENV["FIREBASE_APP_ID"],
      service_credentials_file: ENV["FIREBASE_CREDENTIALS_FILE"],
      groups: "internal-testers, qa-team",
      release_notes: File.read("CHANGELOG.md"),
      android_artifact_type: "AAB",
      android_artifact_path: "app/build/outputs/bundle/productionRelease/app-production-release.aab"
    )
  end

  desc "Deploy to Play Store internal track"
  lane :deploy_internal do
    supply(
      aab: "app/build/outputs/bundle/productionRelease/app-production-release.aab",
      track: "internal",
      skip_upload_apk: true,
      skip_upload_metadata: true,
      skip_upload_images: true,
      skip_upload_screenshots: true
    )
  end
end
