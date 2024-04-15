#!/usr/bin/env groovy

def call(Map params = [:]) {
    loadResourceScript "execute.sh"

    def block = params.block
    def action = params.action

    env.PRJ_ROOT = pwd()
    env.PRJ_DATA_HOME = "${env.TMP_DIR}/.data"
    env.PRJ_CACHE_HOME = "${env.TMP_DIR}/.cache"
    env.PRJ_CONFIG_HOME = "${env.TMP_DIR}/.config"
    env.PRJ_RUNTIME_DIR = "${env.TMP_DIR}/.run"
    env.PRJ_PATH = "${env.TMP_DIR}/.bin"
    env.TERM = "xterm-256color"

    env.DRV_IMPORT_FROM_DISCOVERY = false
    env.EVALSTORE_IMPORT = "${env.TMP_DIR}/eval-store"

    def hits = readJSON file: "${env.TMP_DIR}/outputs.json"
    if (hits) {
        if (hits[block]) {
            hits[block][action].each {
                writeJSON json: it, file: "${env.TMP_DIR}/each.json", pretty: 2
                sh """
                   declare action target cell block actionDrv
                   eval \"\$(jq -r '@sh "action=\\(.action) target=\\(.name) cell=\\(.cell) block=\\(.block) actionDrv=\\(.actionDrv)\"' < \$TMP_DIR/each.json )\"
                   export action=\$action
                   export target=\$target
                   export cell=\$cell
                   export block=\$block
                   export actionDrv=\$actionDrv
                   bash ./execute.sh
                """
            }
        } else {
            echo "no ${block} in hits"
        }
    } else {
        echo "hits is null"
    }
}
