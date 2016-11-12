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

package cz.auderis.test.logging.jboss;

import org.jboss.logging.Logger;
import org.jboss.logging.LoggerProvider;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class JBossLoggerProvider implements LoggerProvider {

    static final JBossLoggerProvider INSTANCE = new JBossLoggerProvider();
    private static final ConcurrentMap<String, JBossLoggerAdapter> LOGGERS = new ConcurrentHashMap<String, JBossLoggerAdapter>(16);

    final Map<String, Object> mdc;
    final Deque<String> ndc;

    private JBossLoggerProvider() {
        mdc = new HashMap<String, Object>(4);
        ndc = new LinkedList<String>();
    }

    @Override
    public Logger getLogger(String name) {
        JBossLoggerAdapter logger = LOGGERS.get(name);
        if (null == logger) {
            final JBossLoggerAdapter newLogger = new JBossLoggerAdapter(name);
            final JBossLoggerAdapter origLogger = LOGGERS.putIfAbsent(name, newLogger);
            logger = (null != origLogger) ? origLogger : newLogger;
        }
        return logger;
    }

    @Override
    public void clearMdc() {
        mdc.clear();
    }

    @Override
    public Object putMdc(String key, Object value) {
        return mdc.put(key, value);
    }

    @Override
    public Object getMdc(String key) {
        return mdc.get(key);
    }

    @Override
    public void removeMdc(String key) {
        mdc.remove(key);
    }

    @Override
    public Map<String, Object> getMdcMap() {
        return new HashMap<>(mdc);
    }

    @Override
    public void clearNdc() {
        ndc.clear();
    }

    @Override
    public String getNdc() {
        return ndc.peek();
    }

    @Override
    public int getNdcDepth() {
        return ndc.size();
    }

    @Override
    public String popNdc() {
        return ndc.poll();
    }

    @Override
    public String peekNdc() {
        final String topValue = ndc.peek();
        return (null != topValue) ? topValue : "";
    }

    @Override
    public void pushNdc(String message) {
        ndc.addFirst(message);
    }

    @Override
    public void setNdcMaxDepth(int maxDepth) {
        // ignored
    }

}
