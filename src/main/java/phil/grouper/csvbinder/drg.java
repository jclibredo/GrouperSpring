package phil.grouper.csvbinder;

import com.opencsv.bean.CsvBindByName;

import lombok.Data;

@Data
public class drg {

    @CsvBindByName(column = "mdc")
    private String mdc;

    @CsvBindByName(column = "dc")
    private String dc;

    @CsvBindByName(column = "drg")
    private String drg;

    @CsvBindByName(column = "rw")
    private String rw;

    @CsvBindByName(column = "wtlos")
    private String wtlos;

    @CsvBindByName(column = "ot")
    private String ot;

    @CsvBindByName(column = "mdf")
    private String mdf;

    @CsvBindByName(column = "drgname")
    private String drgname;
}
