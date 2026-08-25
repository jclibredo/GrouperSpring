package phil.grouper.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GrouperParameter {

    public GrouperParameter() {
    }

    @JsonProperty("claimseries")
    private String claimseries;

    @JsonProperty("pdx")
    private String Pdx;

    @JsonProperty("sdx")
    private String Sdx;

    @JsonProperty("proc")
    private String Proc;

    @JsonProperty("birthDate")
    private String BirthDate;

    @JsonProperty("admissionDate")
    private String AdmissionDate;

    @JsonProperty("pdx")
    private String DischargeDate;

    @JsonProperty("pdx")
    private String Gender;

    @JsonProperty("pdx")
    private String TimeAdmission;

    @JsonProperty("pdx")
    private String TimeDischarge;

    @JsonProperty("pdx")
    private String DischargeType;

    @JsonProperty("pdx")
    private String ExpireTime;

    @JsonProperty("pdx")
    private String ExpiredDate;

    @JsonProperty("pdx")
    private String TimeOfBirth;

    @JsonProperty("pdx")
    private String AdmissionWeight;

    private String result_id;
    private String prepccl;
    private String finalpccl;
    private String warningerror;
    private String idseries;

}
