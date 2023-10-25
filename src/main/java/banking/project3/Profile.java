package banking.project3;

/**
 * Represents a Profile with first name, last name, and date of birth.
 * @author Zain Zulfiqar
 * @author Nicholas Yim
 */
public class Profile implements Comparable<Profile>{
private String fname;
private String lname;
private Date dob;
private static final int EQUAL_CONDITION = 0;
private static final int FIRST_INDEX_OF_STRING = 0;
private static final int SUBSTRING_CONSTANT = 1;

    /**
     * Constructs a new Profile.
     * @param fName First name of the profile.
     * @param lName Last name of the profile.
     * @param dob Date of birth of the profile.
     */
    public Profile(String fName, String lName, Date dob) {
        this.fname = fName.toLowerCase();
        this.lname = lName.toLowerCase();
        this.dob = dob;
    }

    /**
     * Gets the capitalized first name.
     * @return Capitalized first name.
     */
    public String getFname(){
        return capitalize(fname);
    }

    /**
     * Gets the capitalized last name.
     * @return Capitalized last name.
     */
    public String getLname() {
        return capitalize(lname);
    }

    /**
     * Gets the date of birth.
     * @return Date of birth.
     */
    public Date getDob() {
        return dob;
    }

    /**
     * Capitalizes the first letter of a string.
     * @param str The string to capitalize.
     * @return Capitalized string.
     */
    private String capitalize(String str) {
        return Character.toUpperCase(str.charAt(FIRST_INDEX_OF_STRING))
                + str.substring(SUBSTRING_CONSTANT);
    }

    /**
     * Checks if this profile is equal to another object.
     * @param obj The object to compare.
     * @return true if equal, false otherwise.
     */
    @Override
    public boolean equals(Object obj){
        if(obj instanceof Profile){
            Profile o = (Profile) obj;
            return fname.equals(o.fname) && lname.equals(o.lname)
                    && dob.compareTo(o.dob) == EQUAL_CONDITION;
        }
        return false;

    }

    /**
     * Returns a string representation of the profile.
     * @return String representation.
     */
    @Override
    public String toString(){
        return fname + " " + lname + " " + dob.dateString();
    }

    /**
     * Compares this profile to another profile.
     * @param o The profile to compare.
     * @return -1, 0, or 1 as this profile is less than, equal to, or greater than the specified profile.
     */
    @Override
    public int compareTo(Profile o) {
        if(this.lname.compareTo(o.lname) < 0){
            return -1;
        }
        if(this.lname.compareTo(o.lname) > 0){
            return 1;
        }
        if(this.fname.compareTo(o.fname) < 0){
            return -1;
        }
        if(this.fname.compareTo(o.fname) > 0){
            return 1;
        }
        if(this.dob.compareTo(o.dob) < 0){
            return -1;
        }
        if (this.dob.compareTo(o.dob) > 0) {
            return 1;
        }
        return 0;
    }
}
