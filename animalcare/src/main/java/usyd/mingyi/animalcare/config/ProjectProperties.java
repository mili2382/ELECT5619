package usyd.mingyi.animalcare.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "project")
public class ProjectProperties {
    private String filePath;
}
