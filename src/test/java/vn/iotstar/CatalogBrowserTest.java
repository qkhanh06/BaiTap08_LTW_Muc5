package vn.iotstar;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@EnabledIfSystemProperty(named = "catalog.browser-test", matches = "true")
class CatalogBrowserTest {
    @LocalServerPort int port;

    @Test void ajaxCrudSearchPaginationAndHomeInChrome() throws Exception {
        Path chrome = Path.of("C:/Program Files/Google/Chrome/Application/chrome.exe");
        assumeTrue(Files.isRegularFile(chrome), "Chrome is not installed at the default Windows path");
        Path target = Path.of("target").toAbsolutePath();
        Path output = target.resolve("browser-smoke.html");
        Path errors = target.resolve("browser-smoke.err");
        Path profile = Files.createTempDirectory(target, "chrome-test-");
        Process browser = new ProcessBuilder(chrome.toString(), "--headless=new",
                "--disable-gpu", "--no-first-run", "--disable-background-networking",
                "--user-data-dir=" + profile, "--virtual-time-budget=20000", "--timeout=60000",
                "--dump-dom", "http://localhost:" + port + "/catalog-smoke.html")
                .redirectOutput(output.toFile()).redirectError(errors.toFile()).start();
        try {
            assertThat(browser.waitFor(60, TimeUnit.SECONDS)).as("Chrome completed within 60 seconds").isTrue();
            String html = Files.readString(output, StandardCharsets.UTF_8);
            var match = Pattern.compile("<pre id=\"result\">(.*?)</pre>", Pattern.DOTALL).matcher(html);
            assertThat(match.find()).as("Browser result exists; see target/browser-smoke.html and .err").isTrue();
            assertThat(match.group(1)).startsWith("PASS");
        } finally {
            if (browser.isAlive()) browser.destroyForcibly();
        }
    }
}

