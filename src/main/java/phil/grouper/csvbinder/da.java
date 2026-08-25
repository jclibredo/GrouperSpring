package phil.grouper.csvbinder;

import com.opencsv.bean.CsvBindByName;

import lombok.Data;

@Data
public class da {

    @CsvBindByName(column = "dagger")
    private String dagger;

    @CsvBindByName(column = "asterisk")
    private String asterisk;

    @CsvBindByName(column = "pdx")
    private String pdx;

    @CsvBindByName(column = "sdx")
    private String sdx;
}
