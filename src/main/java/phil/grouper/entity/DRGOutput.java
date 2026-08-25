package phil.grouper.entity;

import lombok.Data;

@Data
public class DRGOutput {
    public DRGOutput() {
    }

    private String resultid;
    private String RW;
    private String WTLOS;
    private String OT;
    private String MDF;
    private String DRGName;
    private String DRG;
    private String MDC;
    private String DC;
    private String PDC;
    private String CC;
    private String prepccl;
    private String finalpccl;
    private String warningerror;
    private String SDXFINDER;
    private String claimseries;
}
