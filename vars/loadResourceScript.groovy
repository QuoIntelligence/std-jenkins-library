#!/usr/bin/env groovy

def call(String path) {
    def content = libraryResource path
    writeFile file: path, text: content
    sh "chmod a+x ./${path}"
}
