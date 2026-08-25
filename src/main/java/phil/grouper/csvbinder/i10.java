package phil.grouper.csvbinder;

import com.opencsv.bean.CsvBindByName;

import lombok.Data;

@Data
public class i10 {
    @CsvBindByName(column = "code")
    private String code;

    @CsvBindByName(column = "mdc")
    private String mdc;

    @CsvBindByName(column = "pdc")
    private String pdc;

    @CsvBindByName(column = "cc")
    private String cc;

    @CsvBindByName(column = "maincc")
    private String maincc;

    @CsvBindByName(column = "ccrow")
    private String ccrow;

    @CsvBindByName(column = "hiv_ax")
    private String hiv_ax;

    @CsvBindByName(column = "trauma")
    private String trauma;

    @CsvBindByName(column = "sex")
    private String sex;

    @CsvBindByName(column = "accpdx")
    private String accpdx;

    @CsvBindByName(column = "ageduse")
    private String ageduse;

    @CsvBindByName(column = "agemin")
    private String agemin;

    @CsvBindByName(column = "agemax")
    private String agemax;

    @CsvBindByName(column = "agedmin")
    private String agedmin;
}
