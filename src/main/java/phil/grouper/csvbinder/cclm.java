package phil.grouper.csvbinder;

import com.opencsv.bean.CsvBindByName;

import lombok.Data;

@Data
public class cclm {

    @CsvBindByName(column = "ccrow")
    private String ccrow;

    @CsvBindByName(column = "ccl")
    private String ccl;
}
