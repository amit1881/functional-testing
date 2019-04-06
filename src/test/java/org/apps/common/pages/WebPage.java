/**
 * 
 */
package org.apps.common.pages;

/**
 * Super class for all Pages
 * 
 */
public abstract class WebPage {
    protected static WebBrowser webBrowser;
    protected final int WEB_ELEMENT_WAIT_TIMEOUT = 10;
    public abstract boolean isValid();
    public void setBrowser(WebBrowser webBrowser) {
        this.webBrowser = webBrowser;
    }
}
