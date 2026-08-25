package phil.grouper.csvbinder;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;


@Data
public class ax {

    @CsvBindByName(column = "ax")
    private String ax;

    @CsvBindByName(column = "code")
    private String code;
}
