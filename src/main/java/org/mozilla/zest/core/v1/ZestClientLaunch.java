/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.mozilla.zest.core.v1;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.safari.SafariDriver;

import com.machinepublishers.jbrowserdriver.JBrowserDriver;
import com.opera.core.systems.OperaDriver;

/**
 * Launch a new client (browser)
 * @author simon
 *
 */
public class ZestClientLaunch extends ZestClient {

	private static final String HEADLESS_ARG = "--headless";

	private String windowHandle = null;
	private String browserType = null;
	private String url = null;
	private String capabilities = null;
	private boolean headless = true;

	public ZestClientLaunch(String windowHandle, String browserType, String url) {
		this(windowHandle, browserType, url, null);
	}

	public ZestClientLaunch(String windowHandle, String browserType, String url, boolean headless) {
		this(windowHandle, browserType, url, null, headless);
	}

	public ZestClientLaunch(String windowHandle, String browserType, String url, String capabilities) {
		this(windowHandle, browserType, url, capabilities, true);
	}

	public ZestClientLaunch(String windowHandle, String browserType, String url, String capabilities, boolean headless) {
		super();
		this.windowHandle = windowHandle;
		this.browserType = browserType;
		this.url = url;
		this.capabilities = capabilities;
		this.headless = headless;
	}

	public ZestClientLaunch() {
		super();
	}

	public String getWindowHandle() {
		return windowHandle;
	}

	public void setWindowHandle(String windowHandle) {
		this.windowHandle = windowHandle;
	}

	public String getBrowserType() {
		return browserType;
	}

	public void setBrowserType(String browserType) {
		this.browserType = browserType;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getCapabilities() {
		return capabilities;
	}

	public void setCapabilities(String capabilities) {
		this.capabilities = capabilities;
	}

	public boolean isHeadless() {
		return headless;
	}

	public void setHeadless(boolean headless) {
		this.headless = headless;
	}

	@Override
	public ZestStatement deepCopy() {
		ZestClientLaunch copy = new ZestClientLaunch(this.getWindowHandle(), this.getBrowserType(), this.getUrl());
		copy.setEnabled(this.isEnabled());
		return copy;
	}

	@Override
	public boolean isPassive() {
		return false;
	}

	@Override
	public String invoke(ZestRuntime runtime) throws ZestClientFailException {

		try {
			WebDriver driver = null;
			DesiredCapabilities cap = new DesiredCapabilities();
			cap.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
			// W3C capability
			cap.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);

			String httpProxy = runtime.getProxy();
			if (httpProxy.length() > 0) {
				Proxy proxy = new Proxy();
				proxy.setHttpProxy(httpProxy);
				proxy.setSslProxy(httpProxy);
				cap.setCapability(CapabilityType.PROXY, proxy);
			}
			if (capabilities != null) {
				for (String capability : capabilities.split("\n")) {
					if (capability != null && capability.trim().length() > 0) {
						String [] typeValue = capability.split("=");
						if (typeValue.length != 2) {
							throw new ZestClientFailException(this, "Invalid capability, expected type=value : " + capability);
						}
						cap.setCapability(typeValue[0], typeValue[1]);
					}
				}
			}

			if ("Firefox".equalsIgnoreCase(this.browserType)) {
				FirefoxOptions firefoxOptions = new FirefoxOptions();
				if (isHeadless()) {
					firefoxOptions.addArguments(HEADLESS_ARG);
				}

				if (!httpProxy.isEmpty()) {
					String[] proxyData = httpProxy.split(":");
					String proxyAddress = proxyData[0];
					int proxyPort = Integer.parseInt(proxyData[1]);

					// Some issues prevent the PROXY capability from being properly applied:
					// https://bugzilla.mozilla.org/show_bug.cgi?id=1282873
					// https://bugzilla.mozilla.org/show_bug.cgi?id=1369827
					// For now set the preferences manually:
					firefoxOptions.addPreference("network.proxy.type", 1);
					firefoxOptions.addPreference("network.proxy.http", proxyAddress);
					firefoxOptions.addPreference("network.proxy.http_port", proxyPort);
					firefoxOptions.addPreference("network.proxy.ssl", proxyAddress);
					firefoxOptions.addPreference("network.proxy.ssl_port", proxyPort);
					firefoxOptions.addPreference("network.proxy.share_proxy_settings", true);
					firefoxOptions.addPreference("network.proxy.no_proxies_on", "");
					// And remove the PROXY capability:
					cap.setCapability(CapabilityType.PROXY, (Object) null);
				}
				firefoxOptions.addTo(cap);

				driver = new FirefoxDriver(cap);
			} else if ("Chrome".equalsIgnoreCase(this.browserType)) {
				// XXX Do not support headless until the following issue is fixed:
				// https://bugs.chromium.org/p/chromium/issues/detail?id=721739
				// (it does not accept insecure certs when in headless)
				// if (isHeadless()) {
				// 	ChromeOptions chromeOptions = new ChromeOptions();
				// 	chromeOptions.addArguments(HEADLESS_ARG);
				// 	cap.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
				// }

				driver = new ChromeDriver(cap); 
			} else if ("HtmlUnit".equalsIgnoreCase(this.browserType)) {
				driver = new HtmlUnitDriver(DesiredCapabilities.htmlUnit().merge(cap)); 
			} else if ("InternetExplorer".equalsIgnoreCase(this.browserType)) {
				driver = new InternetExplorerDriver(cap); 
			} else if ("JBD".equalsIgnoreCase(this.browserType)) {
				cap.setCapability("jbd.headless", isHeadless());
				cap.setCapability("jbd.ssl", "trustanything");
				driver = new JBrowserDriver(cap);
			} else if ("Opera".equalsIgnoreCase(this.browserType)) {
				driver = new OperaDriver(cap);
			} else if ("PhantomJS".equalsIgnoreCase(this.browserType)) {
				ArrayList<String> cliArgs = new ArrayList<>(2);
				cliArgs.add("--ssl-protocol=any");
				cliArgs.add("--ignore-ssl-errors=yes");
				cap.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, cliArgs);
				driver = new PhantomJSDriver(cap);
			} else if ("Safari".equalsIgnoreCase(this.browserType)) {
				driver = new SafariDriver(cap);
			} else {
				// See if its a class name
				try {
					Class<?> browserClass = this.getClass().getClassLoader().loadClass(this.browserType);
					Constructor<?> cons = browserClass.getConstructor(Capabilities.class);
					if (cons != null) {
						driver = (WebDriver) cons.newInstance(cap);
					} else {
						throw new ZestClientFailException(this, "Unsupported browser type: " + this.getBrowserType());
					}
				} catch (ClassNotFoundException e) {
					throw new ZestClientFailException(this, "Unsupported browser type: " + this.getBrowserType());
				}
			}
			
			runtime.addWebDriver(getWindowHandle(), driver);
			
			if (this.url != null) {
				driver.get(runtime.replaceVariablesInString(this.url, true));
			}
			
			return getWindowHandle();
			
		} catch (Exception e) {
			throw new ZestClientFailException(this, e);
		}
	}

}
