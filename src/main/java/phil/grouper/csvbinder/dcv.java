package phil.grouper.csvbinder;

import com.opencsv.bean.CsvBindByName;

import lombok.Data;

@Data
public class dcv {

    @CsvBindByName(column = "pdrg")
    private String pdrg;

    @CsvBindByName(column = "fdrg")
    private String fdrg;

}
