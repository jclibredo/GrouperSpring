package phil.grouper.csvbinder;

import com.opencsv.bean.CsvBindByName;

import lombok.Data;

@Data
public class ccex {

    @CsvBindByName(column = "sdx")
    private String sdx;

    @CsvBindByName(column = "pdx")
    private String pdx;
}
