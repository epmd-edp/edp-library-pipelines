/* Copyright 2018 EPAM Systems.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.

 See the License for the specific language governing permissions and
 limitations under the License.*/

package com.epam.edp.buildtool

import com.epam.edp.Nexus
import com.epam.edp.Job
import groovy.json.*

class Dotnet implements BuildTool {
    Script script
    Nexus nexus
    Job job

    def sln_filename
    def scripts = [:]
    def nugetApiKey
    def snapshotsPath
    def releasesPath

    def init() {
        this.snapshotsPath = job.getParameterValue("ARTIFACTS_SNAPSHOTS_PATH", "edp-dotnet-snapshots")
        this.releasesPath = job.getParameterValue("ARTIFACTS_RELEASES_PATH", "edp-dotnet-releases")
        this.scripts = ['get-nuget-token': ['scriptPath': this.script.libraryResource("nexus/get-nuget-token.groovy")]]
        this.sln_filename = null
        this.nugetApiKey = getNugetToken("get-nuget-token")
    }

    private def getNugetToken(scriptName) {
        def result
        script.writeFile file: "${scriptName}.groovy", text: this.scripts["${scriptName}"].scriptPath
        nexus.uploadGroovyScriptToNexus(scriptName, "${scriptName}.groovy")
        script.withCredentials([script.usernamePassword(credentialsId: "${nexus.credentialsId}",
                passwordVariable: 'PASSWORD', usernameVariable: 'USERNAME')]) {
            result = nexus.runNexusGroovyScript(scriptName, "{\"name\": \"${script.USERNAME}\"}")
        }
        def response = new JsonSlurperClassic().parseText(result.content)
        return new JsonSlurperClassic().parseText(response.result).nuGetApiKey
    }

    def getNexusRepositoryUrl(isRelease) {
        return isRelease
                ? "${this.nexus.repositoriesUrl}/${this.releasesPath}"
                : "${this.nexus.repositoriesUrl}/${this.snapshotsPath}"
    }
}