package companyA;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * Created by evanpthompson on 11/1/2016.
 * Dates are contained as Strings to ensure proper serialization and deserialization
 * of the ISODate object as stored in the mongodb because jackson does not support the format.
 */

@XmlRootElement(name = "user")
public class User implements Comparable<User> {

    /*  Seek to next implement list of prior passwords used in prior set amount of time. (18 months)  */

    private int id;
    private Object _id;
    private String givenName;
    private String userName;
    private String password;
    private String emailAddress;
    private String passwordExpiration;
    private boolean activeUser;

    public User() {  }

    public User(String givenName, String password) {

        this.givenName = givenName;
        this.userName = generateUserName(givenName);
        this.password = password;
        this.emailAddress = generateEmailAddress(userName);
        this.passwordExpiration = passwordExpiration();

    }

    @XmlElement
    public Object get_id() {
        return _id;
    }

    public void set_id(Object _id) {
        this._id = _id;
    }

    @XmlElement
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @XmlElement
    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    @XmlElement
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @XmlElement
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        this.setPasswordExpiration(passwordExpiration());
    }

    @XmlElement
    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    @XmlElement
    public String getPasswordExpiration() {
        return passwordExpiration;
    }

    public void setPasswordExpiration(String passwordExpiration) {
        this.passwordExpiration = passwordExpiration;
    }

    public String passwordExpiration() {
        // Create a ISODateTimeFormat string for insertion into the db. For timestamp, format "yyyy-mm-ddThh:mm:ss.nnnZ"
        DateTime now = new DateTime(DateTimeZone.UTC);
        DateTimeFormatter formatter = ISODateTimeFormat.dateTime();
        String timestamp = formatter.print(now);

        Timestamp ts = new Timestamp(timestamp);

        // test of password expiration set

        return ts.setDaysAhead(60);
    }

    public boolean isPasswordExpired(String passwordExpiration) {

        // Create a ISODateTimeFormat string for insertion into the db. For timestamp, format "yyyy-mm-ddThh:mm:ss.nnnZ"
        DateTime now = new DateTime(DateTimeZone.UTC);
        DateTimeFormatter formatter = ISODateTimeFormat.dateTime();
        String timestamp = formatter.print(now);

        // passwordExpiration has not been set and needs to be
        if (passwordExpiration == null) {
            return true;
        }

        Timestamp ts = new Timestamp(timestamp);
        Timestamp pe = new Timestamp(passwordExpiration);

        if (pe.compareTo(ts) > 0) {
            return true;
        }
        else {
            return false;
        }

    }

    @XmlElement
    public boolean isActiveUser() {
        return activeUser;
    }

    public void setActiveUser(boolean activeUser) {
        this.activeUser = activeUser;
    }

    public int compareTo(User other) {
        return this.id - other.id;
    }


    /* User Data Requirement 1.3.0 username is comprised of first 2 letters of the first
    * and first 6 of the last if available.
    * 1.4.0 To ensure unique username generation a 4 digit alphanumeric string will be appended.
    */
    public String generateUserName(String givenName) {
        String[] names = givenName.split(" ");  // separate out the names, last name defined as last word in string
        String user = "";
        int minLength = 8;

		/*  to implement unique ids a query to the db will have to be run  */

        // acount for names that are too short

        // if the given name is not 8 letters in length, add "a" to the end
        if (givenName.length() < 8) {
            user = givenName;
            while (user.length() < 8) {
                user += "a";
            }



            return user;
        }

        // if the first and last name are insufficient given name is userName
        if ((names[0].length() + names[names.length-1].length()) < 8 ) {
            user += names[0] + names[1] + names[2];

            return user;
        }

        // if first name is less than 2 add more last name
        if (names[0].length() < 2) {

            int len = names[0].length();  // get length of last name

            user += names[0] + names[names.length-1].substring(0, (8-len));

            return user;
        }

        // if last name is less than 6 add more first name
        if (names[names.length-1].length() < 7) {
            int len = names[names.length-1].length();  // get length of last name

            user += names[0].substring(0, (8-len)) + names[names.length-1];

            return user;
        }

        user += names[0].substring(0, 2) + names[names.length-1].substring(0, 6);

        return user.toLowerCase();
    }

    /*  method to generate an email address from userName  */
    public static String generateEmailAddress(String userName) {
        String user = userName + "@companyA.com";

        return user.toLowerCase();
    }



    // method will return a timestamp as a string a set number of days from now.
    // used for passwordExpiration 1.2.0
    public String setDaysAhead(int number) {
        DateTime now = new DateTime(DateTimeZone.UTC);
        DateTimeFormatter formatter = ISODateTimeFormat.dateTime();
        String timestamp = formatter.print(now.plusDays(number));
        return timestamp;
    }

    // method will return a timestamp as a string a set number of minutes from now. used for sessionAuth expiration
    public String setMinutesAhead(int number) {
        DateTime now = new DateTime(DateTimeZone.UTC);
        DateTimeFormatter formatter = ISODateTimeFormat.dateTime();
        String timestamp = formatter.print(now.plusMinutes(number));
        return timestamp;
    }

    @Override
    public String toString() {
        return "User : {" +
                "id : " + id +
                ", givenName : " + givenName +
                ", userName : " + userName +
                ", password : " + password +
                ", emailAddress : " + emailAddress +
                ", passwordExpiration : " + passwordExpiration +
                ", activeUser=" + activeUser +
                " }";
    }
}