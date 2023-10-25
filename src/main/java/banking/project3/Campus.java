package banking.project3;

/**
 * This enum represents the different campuses of a university.
 * Each campus is associated with a specific code.
 * @author Nicholas Yim
 */
public enum Campus {
    // Enum constants representing the different campuses
    NEW_BRUNSWICK("0"),
    NEWARK("1"),
    CAMDEN("2");

    private final String code;

    /**
     * Private constructor to initialize the code for each campus.
     * @param code The code associated with the campus.
     */
    private Campus(String code) {
        this.code = code;
    }

    /**
     * Getter method to retrieve the code associated with the campus.
     * @return The code of the campus.
     */
    public String getCode() {
        return code;
    }

    /**
     * Static method to get the Campus enum constant from a given code.
     * @param code The code to search for.
     * @return The Campus enum constant if found, otherwise null.
     */
    public static Campus fromCode(String code) {
        for (Campus campus : Campus.values()) {
            if (campus.getCode().equals(code)) {
                return campus;
            }
        }
        return null;

    }

}
