#!/usr/bin/env groovy

def call() {
    stage('Discover') {
      env.TMP_DIR = pwd(tmp:true)

      env.SKIP_DRV_EXPORT = false;
      env.EVALSTORE_EXPORT = "${env.TMP_DIR}/eval-store"
      env.GITHUB_OUTPUT = "${env.TMP_DIR}/outputs"
      env.flake_url = "."

      loadResourceScript "eval.sh"
      sh "bash ./eval.sh"
      sh "grep ^json $GITHUB_OUTPUT | cut -d \"=\" -f2 > \$TMP_DIR/outputs.json"
  }
}
