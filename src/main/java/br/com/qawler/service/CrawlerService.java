package br.com.qawler.service;

import br.com.qawler.enums.ModoCrawler;
import br.com.qawler.scanner.ScanResult;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Crawls a target site, visiting URLs up to a configured depth, and runs scanners on each page.
 *
 * Expected entity: {@code br.com.qawler.model.CrawlerConfig} with fields:
 *   id (Long), sistemaId (Long), modo (ModoCrawler), maxDepth (int), includePatterns (String), excludePatterns (String)
 */
@Service
public class CrawlerService {

    private static final Logger log = LoggerFactory.getLogger(CrawlerService.class);

    private final ScannerService scannerService;
    private final int maxDepth;
    private final Duration timeout;
    private final String userAgent;
    private final boolean headless;
    private final String chromiumPath;

    public CrawlerService(ScannerService scannerService,
                          @Value("${crawler.max-depth:2}") int maxDepth,
                          @Value("${crawler.timeout-seconds:30}") int timeoutSeconds,
                          @Value("${crawler.user-agent:QAwler/1.0}") String userAgent,
                          @Value("${crawler.headless:true}") boolean headless,
                          @Value("${crawler.chromium-path:/usr/bin/chromium}") String chromiumPath) {
        this.scannerService = scannerService;
        this.maxDepth = maxDepth;
        this.timeout = Duration.ofSeconds(timeoutSeconds);
        this.userAgent = userAgent;
        this.headless = headless;
        this.chromiumPath = chromiumPath;
    }

    /**
     * Crawls a site starting from the base URL with the given mode.
     *
     * @param baseUrl   starting URL
     * @param modo      FULL (interact with pages) or READ_ONLY (just visit and scan)
     * @return list of all scan results collected during the crawl
     */
    public List<ScanResult> crawl(String baseUrl, ModoCrawler modo, Set<String> allowedHosts) {
        log.info("Starting crawl — baseUrl={}, modo={}, maxDepth={}", baseUrl, modo, maxDepth);

        List<ScanResult> allResults = new ArrayList<>();
        WebDriver driver = createDriver();

        try {
            Queue<CrawlTask> queue = new ConcurrentLinkedQueue<>();
            Set<String> visited = Collections.synchronizedSet(new HashSet<>());
            queue.add(new CrawlTask(baseUrl, 0));

            while (!queue.isEmpty()) {
                CrawlTask task = queue.poll();
                if (task == null) continue;
                if (visited.contains(task.url)) continue;
                if (task.depth > maxDepth) continue;
                if (!isAllowedHost(task.url, allowedHosts)) continue;

                visited.add(task.url);
                log.info("Crawling [{}/{}] {}", task.depth, maxDepth, task.url);

                try {
                    driver.get(task.url);
                    // Allow JS to settle
                    Thread.sleep(1000);

                    // Run scanners on this page
                    List<ScanResult> pageResults = scannerService.scanAll(driver, task.url);
                    allResults.addAll(pageResults);

                    // Collect links for the next depth level (only in FULL mode)
                    if (modo == ModoCrawler.FULL && task.depth < maxDepth) {
                        List<String> links = driver.findElements(org.openqa.selenium.By.tagName("a"))
                                .stream()
                                .map(el -> el.getAttribute("href"))
                                .filter(href -> href != null && !href.isBlank())
                                .filter(href -> href.startsWith("http"))
                                .distinct()
                                .toList();

                        for (String link : links) {
                            if (!visited.contains(link)) {
                                queue.add(new CrawlTask(link, task.depth + 1));
                            }
                        }
                    }

                } catch (Exception e) {
                    log.error("Error crawling {}: {}", task.url, e.getMessage());
                }
            }

        } catch (Exception e) {
            log.error("Crawl aborted: {}", e.getMessage(), e);
        } finally {
            driver.quit();
        }

        log.info("Crawl finished — {} pages visited, {} total issues",
                allResults.stream().map(ScanResult::getUrl).distinct().count(),
                allResults.stream().mapToInt(ScanResult::issueCount).sum());

        return allResults;
    }

    // ── Helpers ──────────────────────────────────────────

    private WebDriver createDriver() {
        ChromeOptions options = new ChromeOptions();
        if (headless) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--user-agent=" + userAgent);
        options.setBinary(chromiumPath);

        // Enable browser logging for JS scanner
        options.setExperimentalOption("goog:loggingPrefs",
                Map.of("browser", "ALL"));

        ChromeDriver driver = new ChromeDriver(options);
        driver.manage().timeouts().pageLoadTimeout(timeout);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        return driver;
    }

    private boolean isAllowedHost(String url, Set<String> allowedHosts) {
        if (allowedHosts == null || allowedHosts.isEmpty()) return true;
        try {
            String host = new java.net.URI(url).getHost();
            return host != null && allowedHosts.contains(host);
        } catch (Exception e) {
            return false;
        }
    }

    // ── Inner task record ────────────────────────────────

    private record CrawlTask(String url, int depth) {}
}
