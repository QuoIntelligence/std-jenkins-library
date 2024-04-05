#!/usr/bin/env groovy

def call(params = [:]) {
    def block = params.block
    def action = params.action
    
    loadResourceScript "execute.sh"

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
