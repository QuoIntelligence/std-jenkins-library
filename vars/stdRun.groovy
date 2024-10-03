#!/usr/bin/env groovy

def call(Map params = [:]) {
    // Ensure that the function is called within a script block
    script {
        loadResourceScript "execute.sh"

        def block = params.block
        def action = params.action
        def envConfig = params.get('envConfig', [:])
        def useParallel = params.get('parallel', true) // New parameter with default true

        // Set environment variables
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

        // Read the JSON file
        def hits = readJSON(file: "${env.TMP_DIR}/outputs.json")

        // Check if the specified block and action exist
        if (hits[block]?.containsKey(action)) {
            def actions = hits[block][action]

            if (useParallel) {
                // Parallel Execution
                echo "Executing tasks in parallel..."
                Map parallelTasks = [:]

                actions.each { task ->
                    def jobName = task["jobName"]
                    def target = task["name"]
                    def additionalEnvVars = envConfig[target] ?: []

                    // Define each parallel task as a closure
                    parallelTasks[jobName] = {
                        // Set environment variables for the task
                        withEnv([
                            "action=${task['action']}",
                            "target=${target}",
                            "cell=${task['cell']}",
                            "block=${task['block']}",
                            "actionDrv=${task['actionDrv']}"
                        ] + additionalEnvVars) {
                            stage(jobName) {
                                echo "Starting job: ${jobName}"
                                sh "bash ./execute.sh"
                                echo "Completed job: ${jobName}"
                            }
                        }
                    }
                }

                // Execute all tasks in parallel
                parallel(parallelTasks)
            } else {
                // Sequential Execution
                echo "Executing tasks sequentially..."
                for (def task : actions) {
                    def jobName = task["jobName"]
                    def target = task["name"]
                    def additionalEnvVars = envConfig[target] ?: []

                    // Set environment variables for the task
                    withEnv([
                        "action=${task['action']}",
                        "target=${target}",
                        "cell=${task['cell']}",
                        "block=${task['block']}",
                        "actionDrv=${task['actionDrv']}"
                    ] + additionalEnvVars) {
                        stage(jobName) {
                            echo "Starting job: ${jobName}"
                            sh "bash ./execute.sh"
                            echo "Completed job: ${jobName}"
                        }
                    }
                }
            }
        } else {
            error "No actions found for block: '${block}' and action: '${action}'"
        }
    }
}
