#!/usr/bin/env groovy


def call() {
      loadResourceScript "eval.sh"

      env.TMP_DIR = pwd(tmp:true)

      env.EVALSTORE_EXPORT = "${env.TMP_DIR}/eval-store"
      env.GITHUB_OUTPUT = "${env.TMP_DIR}/outputs"
      env.flake_url = "."

      sh "bash ./eval.sh"
      sh "grep ^json $GITHUB_OUTPUT | cut -d \"=\" -f2 > \$TMP_DIR/outputs.json"
}
