package br.com.qawler.scanner;

import br.com.qawler.enums.Severidade;
import br.com.qawler.enums.TipoBug;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Scanner that captures browser JavaScript errors via the Selenium Logging API.
 * Requires the WebDriver to have "browser" logging enabled (e.g., {@code --enable-logging} for Chrome).
 */
public class JavaScriptScanner implements ScannerModule {

    private static final Logger log = LoggerFactory.getLogger(JavaScriptScanner.class);

    @Override
    public ScanResult scan(WebDriver driver, String url) {
        ScanResult result = new ScanResult(getName(), url);
        log.info("JavaScriptScanner starting on {}", url);

        try {
            // Force an onerror listener to catch runtime JS errors
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("window.__qawlerJsErrors = [];" +
                    "window.addEventListener('error', function(e) {" +
                    "  window.__qawlerJsErrors.push({message: e.message, source: e.filename, line: e.lineno, col: e.colno});" +
                    "});");

            // Collect browser console SEVERE entries (errors)
            LogEntries logEntries = driver.manage().logs().get(LogType.BROWSER);
            for (LogEntry entry : logEntries) {
                if (entry.getLevel().intValue() >= java.util.logging.Level.SEVERE.intValue()) {
                    result.addIssue(TipoBug.JS_ERROR, Severidade.HIGH,
                            "Browser console SEVERE — " + entry.getMessage(),
                            url);
                } else if (entry.getLevel().intValue() >= java.util.logging.Level.WARNING.intValue()) {
                    result.addIssue(TipoBug.JS_ERROR, Severidade.MEDIUM,
                            "Browser console WARNING — " + entry.getMessage(),
                            url);
                }
            }

            // Collect captured runtime errors via __qawlerJsErrors
            @SuppressWarnings("unchecked")
            var capturedErrors = (java.util.List<java.util.Map<String, Object>>)
                    js.executeScript("return window.__qawlerJsErrors || [];");
            for (var err : capturedErrors) {
                String msg = String.valueOf(err.getOrDefault("message", "Unknown error"));
                String src  = String.valueOf(err.getOrDefault("source", ""));
                Object line = err.get("line");
                result.addIssue(TipoBug.JS_ERROR, Severidade.HIGH,
                        "Runtime JS error — " + msg,
                        src + ":" + line);
            }

        } catch (Exception e) {
            log.error("JavaScriptScanner error on {}: {}", url, e.getMessage(), e);
            result.addIssue(TipoBug.JS_ERROR, Severidade.HIGH,
                    "Scanner failure", "JavaScriptScanner threw: " + e.getMessage());
        }

        log.info("JavaScriptScanner finished on {} — {} issues", url, result.issueCount());
        return result;
    }

    @Override
    public String getName() {
        return "JavaScript Scanner";
    }
}
