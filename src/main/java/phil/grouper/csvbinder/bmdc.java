package phil.grouper.csvbinder;

import com.opencsv.bean.CsvBindByName;

import lombok.Data;

@Data
public class bmdc {

    @CsvBindByName(column = "code")
    private String code;

    @CsvBindByName(column = "mdcmale")
    private String mdcmale;

    @CsvBindByName(column = "pdcmale")
    private String pdcmale;

    @CsvBindByName(column = "mdcfem")
    private String mdcfem;

    @CsvBindByName(column = "pdcfem")
    private String pdcfem;

    @CsvBindByName(column = "desc")
    private String desc;

}
