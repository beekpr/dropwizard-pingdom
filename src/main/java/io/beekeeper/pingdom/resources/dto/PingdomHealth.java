package io.beekeeper.pingdom.resources.dto;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Data Object for pingdom
 */
@XmlRootElement(name = "pingdom_http_custom_check")
public class PingdomHealth {

    private static DecimalFormat FORMAT = new DecimalFormat("########.###", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
    private static final double MAX_RESPONSE_TIME = 99999999.999;

    /**
     * The OK String. Marks a Status as OK.
     */
    public static String HEALTH_OK = "OK";

    /**
     * The health Status. Use {@link PingdomHealth#HEALTH_OK} to indicate an OK
     * status. Any status other than OK will make the check count as down.
     */
    @XmlElement
    public String status;

    /**
     * The response time. The response time number has to be a positive number
     * and is limited to 8 digits in the integer part and 3 in the fractional
     * part, i.e. the maximum value is 99,999,999.999
     */
    @XmlElement(name = "response_time")
    public String responseTime;

    public PingdomHealth() {}

    public PingdomHealth(String status, String responseTime) {
        this.status = status;
        this.responseTime = responseTime;
    }

    public PingdomHealth(String status, Double responseTime) {
        this.status = status;
        this.responseTime = FORMAT.format(Math.min(Math.max(0, responseTime), MAX_RESPONSE_TIME));
    }
}
