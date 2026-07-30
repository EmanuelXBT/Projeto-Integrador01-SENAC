package br.com.qawler.scanner;

import br.com.qawler.enums.Severidade;
import br.com.qawler.enums.TipoBug;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Scanner that detects broken images on a page.
 * Checks every {@code <img>} tag — both its {@code naturalWidth} via JavaScript and a HEAD request fallback.
 */
public class ImageScanner implements ScannerModule {

    private static final Logger log = LoggerFactory.getLogger(ImageScanner.class);

    @Override
    public ScanResult scan(WebDriver driver, String url) {
        ScanResult result = new ScanResult(getName(), url);
        log.info("ImageScanner starting on {}", url);

        try {
            List<WebElement> images = driver.findElements(By.tagName("img"));

            for (WebElement img : images) {
                String src = img.getAttribute("src");
                if (src == null || src.isBlank()) {
                    result.addIssue(TipoBug.BROKEN_IMAGE, Severidade.MEDIUM,
                            "<img> tag without src attribute",
                            url);
                    continue;
                }

                // Check via JavaScript if the image loaded correctly
                JavascriptExecutor js = (JavascriptExecutor) driver;
                Object complete  = js.executeScript("return arguments[0].complete;", img);
                Object naturalW  = js.executeScript("return arguments[0].naturalWidth;", img);

                if (Boolean.TRUE.equals(complete) && (naturalW instanceof Number nw && nw.intValue() == 0)) {
                    result.addIssue(TipoBug.BROKEN_IMAGE, Severidade.HIGH,
                            "Broken image — failed to load",
                            src);
                }
            }
        } catch (Exception e) {
            log.error("ImageScanner error on {}: {}", url, e.getMessage(), e);
            result.addIssue(TipoBug.BROKEN_IMAGE, Severidade.HIGH,
                    "Scanner failure", "ImageScanner threw: " + e.getMessage());
        }

        log.info("ImageScanner finished on {} — {} issues", url, result.issueCount());
        return result;
    }

    @Override
    public String getName() {
        return "Image Scanner";
    }
}
