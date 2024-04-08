<!--
SPDX-FileCopyrightText: 2022 The Standard Authors
SPDX-FileCopyrightText: 2022 Kevin Amado <kamadorueda@gmail.com>

SPDX-License-Identifier: Unlicense
-->

<div align="center">
    <img src="https://github.com/divnix/std/raw/main/artwork/logo.png" width="250" />
    <img src="https://cdn6.aptoide.com/imgs/3/4/4/34411789a9d8fd5941153dd26588621c_icon.png" width="120" />
    <h1>Jenkins Shared library for Standard</h1>
</div>

<!--
_By [Kevin Amado](https://github.com/kamadorueda),
with contributions from [David Arnold](https://github.com/blaggacao),
[Timothy DeHerrera](https://github.com/nrdxp)
and many more amazing people (see end of file for a full list)._
-->
---
A Jenkins port of [GitHub Action for _Standard_](https://github.com/divnix/std-action). Use Jenkins Standard Library to automatically detect CI targets that need re-doing.

Standard is a nifty DevOps framework that enables an efficient Software Development Life Cycle (SDLC) with the power of Nix via Flakes.

### Usage

```groovy
library 'std-jenkins-library'

pipeline {
  agent any

  stages {
    stage('Discover') {
      steps {
        script {
          stdDiscover()
        }
      }
    }

    stage('Build') {
      steps {
        script {
          stdRun(block: 'packages', action:'publish')
        }
      }
    }

    stage('Publish') {
      steps {
        script {
          stdRun(block: 'oci-images', action:'publish')
        }
      }
    }
  }
}
```
