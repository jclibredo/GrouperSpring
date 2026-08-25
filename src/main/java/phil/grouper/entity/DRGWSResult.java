package phil.grouper.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Generates Getters, Setters, toString, etc.
@Builder // Enables fluent builder pattern
@NoArgsConstructor // Mandatory for some frameworks
@AllArgsConstructor
public class DRGWSResult {
    private String result;
    private String message;
    private boolean success;
}
