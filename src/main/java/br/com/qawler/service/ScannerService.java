package br.com.qawler.service;

import br.com.qawler.scanner.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Orchestrates scanner modules: runs all registered scanners against a URL.
 */
@Service
public class ScannerService {

    private static final Logger log = LoggerFactory.getLogger(ScannerService.class);

    private final List<ScannerModule> modules;

    public ScannerService(List<ScannerModule> modules) {
        this.modules = modules;
        log.info("ScannerService initialized with {} modules", modules.size());
    }

    /**
     * Runs every registered scanner module against the given URL using the provided WebDriver.
     *
     * @param driver Selenium WebDriver instance
     * @param url    target URL
     * @return aggregated list of scan results from all modules
     */
    public List<ScanResult> scanAll(org.openqa.selenium.WebDriver driver, String url) {
        log.info("Running all scanners on {}", url);
        List<ScanResult> results = new ArrayList<>();

        for (ScannerModule module : modules) {
            log.info("  ── Running {} ──", module.getName());
            try {
                ScanResult result = module.scan(driver, url);
                results.add(result);
                log.info("  {} completed — {} issues found", module.getName(), result.issueCount());
            } catch (Exception e) {
                log.error("  {} threw unhandled exception: {}", module.getName(), e.getMessage(), e);
                ScanResult errorResult = new ScanResult(module.getName(), url);
                errorResult.addIssue(br.com.qawler.enums.TipoBug.HTTP_ERROR,
                        br.com.qawler.enums.Severidade.CRITICAL,
                        "Module crashed: " + module.getName(),
                        e.getMessage());
                results.add(errorResult);
            }
        }

        long totalIssues = results.stream().mapToInt(ScanResult::issueCount).sum();
        log.info("All scanners finished on {} — {} total issues across {} modules",
                url, totalIssues, modules.size());
        return results;
    }

    /**
     * Runs a single named scanner module.
     *
     * @param moduleName the {@link ScannerModule#getName()} value
     */
    public ScanResult scanOne(org.openqa.selenium.WebDriver driver, String url, String moduleName) {
        return modules.stream()
                .filter(m -> m.getName().equalsIgnoreCase(moduleName))
                .findFirst()
                .map(m -> m.scan(driver, url))
                .orElseThrow(() -> new IllegalArgumentException("Scanner module not found: " + moduleName));
    }

    public List<ScannerModule> getModules() {
        return List.copyOf(modules);
    }
}
