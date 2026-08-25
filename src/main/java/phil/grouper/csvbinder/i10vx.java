package phil.grouper.csvbinder;

import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class i10vx {

    @CsvBindByName(column = "code")
    private String code;

    @CsvBindByName(column = "validcode")
    private String validCode;
    
    @CsvBindByName(column = "desc")
    private String desc;
}
