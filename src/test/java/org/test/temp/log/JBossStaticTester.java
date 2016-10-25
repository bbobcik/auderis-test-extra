/*
 * Copyright 2016 Boleslav Bobcik - Auderis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.test.temp.log;

import org.jboss.logging.Logger;

public class JBossStaticTester {

    private static final Logger LOG = Logger.getLogger(JBossStaticTester.class);

    public void doWork() {
        LOG.trace("Work 1");
        LOG.debug("Work 2");
        LOG.info("Work 3");
        LOG.warn("Work 4");
        LOG.error("Work 5");
        LOG.fatal("Work 6");
    }

}
