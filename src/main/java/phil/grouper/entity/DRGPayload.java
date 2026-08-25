package phil.grouper.entity;

import java.util.Date;

import lombok.Data;

@Data
public class DRGPayload {
    public DRGPayload() {
    }

    private String Code1;
    private String Code2;
    private String Code3;
    private Date exp;
}
