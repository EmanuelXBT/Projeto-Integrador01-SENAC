package br.com.qawler.scanner;

import org.openqa.selenium.WebDriver;

/**
 * Interface for scanner modules that analyze web pages for specific types of issues.
 * Each implementation handles a different domain: HTTP errors, JavaScript errors, broken images, etc.
 */
public interface ScannerModule {

    /**
     * Executes the scan on the given page and returns the results.
     *
     * @param driver the Selenium WebDriver instance positioned at the target page
     * @param url    the URL being scanned
     * @return a {@link ScanResult} containing any issues found
     */
    ScanResult scan(WebDriver driver, String url);

    /**
     * Human-readable name of this scanner module (e.g., "HTTP Scanner").
     */
    String getName();
}
