package phil.grouper.csvbinder;

import com.opencsv.bean.CsvBindByName;

import lombok.Data;

@Data
public class dc {

    @CsvBindByName(column = "mdc")
    private String mdc;
    @CsvBindByName(column = "dc")
    private String dc;
    @CsvBindByName(column = "dccol")
    private String dccol;
    @CsvBindByName(column = "drgx")
    private String drgx;
    @CsvBindByName(column = "cname")
    private String cname;
    @CsvBindByName(column = "drgx2")
    private String drgx2;
}
