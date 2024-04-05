#!/usr/bin/env groovy

def call() {
    env.TMP_DIR = pwd(tmp:true)

    env.PRJ_ROOT = pwd()
    env.PRJ_DATA_HOME = "${env.TMP_DIR}/.data"
    env.PRJ_CACHE_HOME = "${env.TMP_DIR}/.cache"
    env.PRJ_CONFIG_HOME = "${env.TMP_DIR}/.config"
    env.PRJ_RUNTIME_DIR = "${env.TMP_DIR}/.run"
    env.PRJ_PATH = "${env.TMP_DIR}/.bin"
    env.TERM = "xterm-256color"
    env.EVALSTORE_IMPORT = "${env.TMP_DIR}/eval-store"
    env.DRV_IMPORT_FROM_DISCOVERY = false

    env.SKIP_DRV_EXPORT = false;
    env.EVALSTORE_EXPORT = "${env.TMP_DIR}/eval-store"
    env.GITHUB_OUTPUT = "${env.TMP_DIR}/outputs"
    env.flake_url = "."

    loadResourceScript "eval.sh"
    sh "bash ./eval.sh"
    sh "grep ^json $GITHUB_OUTPUT | cut -d "=" -f2 > $TMP_DIR/outputs.json"
}
