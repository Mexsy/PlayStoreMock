package youth.electicsynery.com.managment.Objects;

/**
 * Created by Emeka on 10/25/2017.
 */

public class PhonePrefix {
    private String prefix;
    private String phone_number;
    private String network;

    /**
     * Current networks as of 2017
     * Not taking into account porting of numbers from one network to another*/

    //All MTN numbers prefix;
    private String MTN = "MTN";
    private String MTN1 = "0803";
    private String MTN2 = "0806";
    private String MTN3 = "0814";
    private String MTN4 = "0810";
    private String MTN5 = "0813";
    private String MTN6 = "0814";
    private String MTN7 = "0816";
    private String MTN8 = "0703";
    private String MTN9 = "0706";
    private String MTN10 = "0903";

    //All 9Mobile/Etisalat numbers prefix;
    private String ETI = "9 mobile";
    private String ETI1 = "0809";
    private String ETI2 = "0817";
    private String ETI3 = "0818";
    private String ETI4 = "0908";
    private String ETI5 = "0909";

    //All Airtel numbers prefix;
    private String AIR1 = "0802";
    private String AIR2 = "0808";
    private String AIR3 = "0812";
    private String AIR4 = "0708";
    private String AIR5 = "0902";

    //All GLO numbers prefix;
    private String GLO1 = "0805";
    private String GLO2 = "0807";
    private String GLO3 = "0811";
    private String GLO4 = "0815";
    private String GLO5 = "0705";
    private String GLO6 = "0905";

    //All NTEL numbers prefix;
    private String NTEL = "0804";

    //All MULTILINKS numbers prefix;
    private String MULTILINKS = "0709";

    //All VISAFONE numbers prefix;
    private String VISAFONE = "07025";


    public PhonePrefix(String number){
        this.phone_number = number;
    }

    public boolean isValid(){
        prefix = firstFour(phone_number);
        if (prefix.trim().equals(MTN1.trim()) || prefix.trim().equals(MTN2.trim()) || prefix.trim().equals(MTN3.trim()) || prefix.trim().equals(MTN4.trim()) || prefix.trim().equals(MTN5.trim())
                || prefix.trim().equals(MTN6.trim()) || prefix.trim().equals(MTN7.trim()) || prefix.trim().equals(MTN8.trim()) || prefix.trim().equals(MTN9.trim()) || prefix.trim().equals(MTN10.trim()) )
        {
            this.network = MTN;
            return true;
        }
        else if (prefix.trim().equals(ETI1.trim()) || prefix.trim().equals(ETI2.trim()) || prefix.trim().equals(ETI3.trim()) || prefix.trim().equals(ETI4.trim()) || prefix.trim().equals(ETI5.trim()) ){
            this.network = ETI;
            return true;
        }
        return false;
    }


    public String firstFour(String str) {
        return str.length() < 4 ? str : str.substring(0, 4);
    }

    public String getNetwork() {
        return network;
    }
}
