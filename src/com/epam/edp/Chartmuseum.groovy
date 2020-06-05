/* Copyright 2020 EPAM Systems.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.

 See the License for the specific language governing permissions and
 limitations under the License.*/

package com.epam.edp

import com.epam.edp.platform.Platform

class Chartmuseum {
    Script script
    Job job
    Platform platform

    def route

    Chartmuseum(job, platform, script) {
        this.script = script
        this.job = job
        this.platform = platform
    }

    def init() {
        if (platform.checkObjectExists("route", "chartmuseum"))
            this.route = platform.getJsonPathValue("route", "chartmuseum", ".spec.host")
        else
            script.println("[WARNING] Chartmuseum route not exist")
    }
}