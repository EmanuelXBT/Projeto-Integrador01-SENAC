package br.com.qawler.scanner;

import br.com.qawler.enums.Severidade;
import br.com.qawler.enums.TipoBug;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Scanner that finds broken links (HTTP 4xx / 5xx) on a page.
 * It collects all {@code <a href>} anchors and validates each one with a HEAD/GET request.
 */
public class HttpScanner implements ScannerModule {

    private static final Logger log = LoggerFactory.getLogger(HttpScanner.class);

    private static final int CONNECT_TIMEOUT_MS = 10_000;
    private static final int READ_TIMEOUT_MS = 10_000;

    @Override
    public ScanResult scan(WebDriver driver, String url) {
        ScanResult result = new ScanResult(getName(), url);
        log.info("HttpScanner starting on {}", url);

        try {
            List<String> links = driver.findElements(org.openqa.selenium.By.tagName("a"))
                    .stream()
                    .map(el -> el.getAttribute("href"))
                    .filter(href -> href != null && !href.isBlank() && (href.startsWith("http://") || href.startsWith("https://")))
                    .distinct()
                    .toList();

            Set<String> checked = new HashSet<>();
            for (String link : links) {
                if (checked.contains(link)) continue;
                checked.add(link);
                checkLink(link, result);
            }
        } catch (Exception e) {
            log.error("HttpScanner error on {}: {}", url, e.getMessage(), e);
            result.addIssue(TipoBug.HTTP_ERROR, Severidade.HIGH,
                    "Scanner failure", "HttpScanner threw: " + e.getMessage());
        }

        log.info("HttpScanner finished on {} — {} issues", url, result.issueCount());
        return result;
    }

    private void checkLink(String link, ScanResult result) {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URI(link).toURL().openConnection();
            conn.setRequestMethod("HEAD");
            conn.setConnectTimeout(CONNECT_TIMEOUT_MS);
            conn.setReadTimeout(READ_TIMEOUT_MS);
            conn.setInstanceFollowRedirects(true);

            int status = conn.getResponseCode();
            if (status >= 400) {
                Severidade sev = status >= 500 ? Severidade.HIGH : Severidade.MEDIUM;
                result.addIssue(TipoBug.HTTP_ERROR, sev,
                        "Broken link — HTTP " + status,
                        link);
            }
            conn.disconnect();
        } catch (Exception e) {
            result.addIssue(TipoBug.HTTP_ERROR, Severidade.CRITICAL,
                    "Unreachable link",
                    link + " — " + e.getMessage());
        }
    }

    @Override
    public String getName() {
        return "HTTP Scanner";
    }
}
