package phil.grouper.csvbinder;

import com.opencsv.bean.CsvBindByName;

import lombok.Data;

@Data
public class prpdc {

    @CsvBindByName(column = "code")
    private String code;

    @CsvBindByName(column = "mdc")
    private String mdc;

    @CsvBindByName(column = "pdc")
    private String pdc;
}
