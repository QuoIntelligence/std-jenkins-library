#!/usr/bin/env groovy

def call(Map params = [:]) {
    loadResourceScript "execute.sh"

    def block = params.block
    def action = params.action
    def envConfig = params.get('envConfig', [:])

    env.PRJ_ROOT = pwd()
    env.PRJ_DATA_HOME = "${env.TMP_DIR}/.data"
    env.PRJ_CACHE_HOME = "${env.TMP_DIR}/.cache"
    env.PRJ_CONFIG_HOME = "${env.TMP_DIR}/.config"
    env.PRJ_RUNTIME_DIR = "${env.TMP_DIR}/.run"
    env.PRJ_PATH = "${env.TMP_DIR}/.bin"
    env.TERM = "xterm-256color"

    env.DRV_IMPORT_FROM_DISCOVERY = false
    env.REMOTE_STORE = false
    env.EVALSTORE_IMPORT = "${env.TMP_DIR}/eval-store"

    Map tasks = [failFast: false]

    def hits = readJSON(file: "${env.TMP_DIR}/outputs.json")

    if (hits[block]?.containsKey(action)) {
      hits[block][action].each { it ->
        def jobName = it["jobName"]
        def target = it["name"]
        def additionalEnvVars = envConfig[target] ?: []

        tasks[jobName] = {
          withEnv([
            "action=${it['action']}",
            "target=${target}",
            "cell=${it['cell']}",
            "block=${it['block']}",
            "actionDrv=${it['actionDrv']}"
          ] + additionalEnvVars) {
            stage(jobName) {
              sh "bash ./execute.sh"
            }
          }
        }
      }
    }
  
    parallel(tasks)
}
