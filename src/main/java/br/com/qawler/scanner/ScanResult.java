package br.com.qawler.scanner;

import br.com.qawler.enums.Severidade;
import br.com.qawler.enums.TipoBug;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates the result of a single scanner module run.
 */
public class ScanResult {

    private final String scannerName;
    private final String url;
    private final LocalDateTime scannedAt;
    private final List<ScanIssue> issues = new ArrayList<>();
    private boolean passed = true;

    public ScanResult(String scannerName, String url) {
        this.scannerName = scannerName;
        this.url = url;
        this.scannedAt = LocalDateTime.now();
    }

    public void addIssue(TipoBug tipo, Severidade severidade, String message, String detail) {
        this.issues.add(new ScanIssue(tipo, severidade, message, detail));
        this.passed = false;
    }

    // ── Getters ──────────────────────────────────────────

    public String getScannerName() { return scannerName; }
    public String getUrl() { return url; }
    public LocalDateTime getScannedAt() { return scannedAt; }
    public List<ScanIssue> getIssues() { return List.copyOf(issues); }
    public boolean isPassed() { return passed; }
    public int issueCount() { return issues.size(); }

    // ── Nested issue record ──────────────────────────────

    public record ScanIssue(
            TipoBug tipo,
            Severidade severidade,
            String message,
            String detail
    ) {}
}
