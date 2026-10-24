package phil.grouper.csvbinder;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

@Data
public class proc {

    @CsvBindByName(column = "code")
    private String code;

    @CsvBindByName(column = "orp")
    private String orp;

    @CsvBindByName(column = "orptype")
    private String orptype;

    @CsvBindByName(column = "sex")
    private String sex;

    @CsvBindByName(column = "site")
    private String site;

    @CsvBindByName(column = "procgr")
    private String procgr;

    @CsvBindByName(column = "pcpart")
    private String pcpart;

    @CsvBindByName(column = "extlev")
    private String extlev;

    @CsvBindByName(column = "desc")
    private String desc;

    @CsvBindByName(column = "drguse")
    private String drguse;

    @CsvBindByName(column = "mayun")
    private String mayun;

}
