/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package org.zaproxy.zest.core.v1;

import java.util.List;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import org.openqa.selenium.WebDriver;

/** The Interface ZestRuntime. */
public interface ZestRuntime extends ZestOutputWriter {

    /**
     * Get the current value of the specified variable.
     *
     * @param name the name
     * @return the current value of the specified variable
     */
    String getVariable(String name);

    /**
     * Set the value of the specified variable.
     *
     * @param name
     * @param value
     */
    void setVariable(String name, String value);

    /**
     * Gets a global variable.
     *
     * <p>The global variables should outlive and be available to all scripts. How and where the
     * variables are persisted and for how long is left as an implementation detail.
     *
     * @param name the name of the variable.
     * @return the value of the variable, might be {@code null} if no value was previously set.
     * @since 0.14.0
     * @see #setGlobalVariable(String, String)
     */
    default String getGlobalVariable(String name) {
        return null;
    }

    /**
     * Sets or removes a global variable.
     *
     * <p>The variable is removed when the {@code value} is {@code null}.
     *
     * @param name the name of the variable.
     * @param value the value of the variable.
     * @since 0.14.0
     * @see #getGlobalVariable(String)
     */
    default void setGlobalVariable(String name, String value) {
        // Nothing to do.
    }

    /**
     * Gets a {@code ScriptEngine} for the given extension.
     *
     * <p>If no engine is returned it is used a default engine, if available.
     *
     * @param extension the extension of the script.
     * @return the {@code ScriptEngine}, or {@code null}.
     * @since 0.22.0
     */
    default ScriptEngine getScriptEngine(String extension) {
        return null;
    }

    /**
     * Get the last response.
     *
     * @return the last response
     */
    ZestResponse getLastResponse();

    /**
     * Get the last request.
     *
     * @return the last request
     */
    ZestRequest getLastRequest();

    /**
     * Replace any variables in the supplied string
     *
     * @param str
     * @param urlEncode
     * @return the string with the variables replaces
     */
    String replaceVariablesInString(String str, boolean urlEncode);

    ScriptEngineFactory getScriptEngineFactory();

    /**
     * Sets the standard variables for a request.
     *
     * @param request the new standard variables
     */
    void setStandardVariables(ZestRequest request);

    /**
     * Sets the standard variables for a response.
     *
     * @param response the new standard variables
     */
    void setStandardVariables(ZestResponse response);

    /**
     * Sets the proxy.
     *
     * @param host the host
     * @param port the port
     */
    void setProxy(String host, int port);

    /** Gets the proxy, as host:port */
    String getProxy();

    /**
     * Add a handle associated with a WebDriver
     *
     * @param handle
     * @param wd
     */
    void addWebDriver(String handle, WebDriver wd);

    /**
     * Remove a handle associated with a WebDriver
     *
     * @param handle
     */
    void removeWebDriver(String handle);

    /**
     * Get the WebDriver assicated with the handle
     *
     * @param handle
     * @return the {@code WebDriver}.
     */
    WebDriver getWebDriver(String handle);

    /**
     * Return all of the WebDrivers
     *
     * @return all the {@code WebDriver}s.
     */
    List<WebDriver> getWebDrivers();
}
