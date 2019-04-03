/**
 * 
 */
package org.apps.common;

import com.mettl.common.ColorFormatConverter;
import com.mettl.common.WebBrowser;

/**
 * Super class for all Pages
 * 
 * @author Ketan
 */
public abstract class MettlWebPage {
    protected static WebBrowser webBrowser;
    protected ColorFormatConverter colorFormatConverter = new ColorFormatConverter();
    public abstract boolean isValid();
    public void setBrowser(WebBrowser webBrowser) {
        this.webBrowser = webBrowser;
    }
}
