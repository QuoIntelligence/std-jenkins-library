#!/usr/bin/env groovy


def call() {
      loadResourceScript "eval.sh"

      env.TMP_DIR = pwd(tmp:true)

      env.EVALSTORE_EXPORT = "${env.TMP_DIR}/eval-store"
      env.GITHUB_OUTPUT = "${env.TMP_DIR}/outputs"
      env.SKIP_DRV_EXPORT = false
      env.flake_url = "."

      sh "rm -rf \$EVALSTORE_EXPORT \$GITHUB_OUTPUT"
      sh "bash ./eval.sh"
      sh "grep ^json $GITHUB_OUTPUT | cut -d \"=\" -f2 > \$TMP_DIR/outputs.json"

      // Publish minimal discovery context for cross-node runs without touching the workspace
      env.STD_OUTPUTS_JSON = readFile(file: "${env.TMP_DIR}/outputs.json").trim()
      env.STD_DISCOVERY_NODE = env.NODE_NAME
}
