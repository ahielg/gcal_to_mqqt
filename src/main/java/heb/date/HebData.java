package heb.date;

/**
 * @author Ahielg
 * @date 01/08/2018
 */
public class HebData {
    private final String parasha;
    private final String parashaHeb;
    private final String knisatShabat;
    private final String havdala;
    private final String holiday;
    private final String omer;
    private final String hebDate;
    private final String hebDateEng;


    public HebData(String parashaHeb, String parasha, String knisatShabat, String havdala, String holiday, String omer, String hebDate, String hebDateEng) {
        this.parashaHeb = parashaHeb;
        this.parasha = parasha;
        this.knisatShabat = knisatShabat;
        this.havdala = havdala;
        this.holiday = holiday;
        this.omer = omer;
        this.hebDate = hebDate;
        this.hebDateEng = hebDateEng;
    }

    public String getHavdala() {
        return havdala;
    }

    public String getHebDate() {
        return hebDate;
    }

    public String getParasha() {
        return parasha;
    }

    public String getParashaHeb() {
        return parashaHeb;
    }

    public String getKnisatShabat() {
        return knisatShabat;
    }

    public String getHoliday() {
        return holiday;
    }

    public String getOmer() {
        return omer;
    }

    public String getHebDateEng() {
        return hebDateEng;
    }

    @Override
    public String toString() {
        return "HebData{" +
                "parasha='" + parasha + '\'' +
                ", parashaHeb='" + parashaHeb + '\'' +
                ", knisatShabat='" + knisatShabat + '\'' +
                ", havdala='" + havdala + '\'' +
                ", holiday='" + holiday + '\'' +
                ", omer='" + omer + '\'' +
                ", hebDate='" + hebDate + '\'' +
                ", hebDateEng='" + hebDateEng + '\'' +
                '}';
    }
}
