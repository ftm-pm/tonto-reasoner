package pm.ftm.tonto.ontology;

/**
 * Tag of ontology models types
 */
public class Tag {

    private Tag() {
    }

    public final static int
            NULL = 1000,
            POINT = 1001,
            NUM = 1002,
            LEXEME = 1003,

    OBJECT = 1100,
            CLASS = 1101,
            INDIVIDUAL = 1102,
            OBJECT_PROPERTY = 1103,
            DATA_PROPERTY = 1104,
            SELF = 1105,
            VARIABLE = 1106,

    AND = 2000,
            AND_OR = 2001,
            OR = 2002,
            EITHER = 2002,
            EQ = 2003,
            NE = 2004,
            LT = 2005,
            LE = 2006,
            GT = 2007,
            GE = 2008,

    PLUS = 2100,
            MINUS = 2101,
            TRUE = 2102,
            FALSE = 2103,
            TEMP = 2104,
            AMPERSAND = 2105,
            VERTICAL_BAR = 2106,
            EQUAL = 2107,
            NEGATION = 2108,
            SEMICOLON = 2109,
            COMMA = 2110,
            MULTI = 2124,
            SLASH = 2125,
            COLON = 2126,

    IS = 3000,
            RELATION = 3001,
            PROPERTY = 3002,
            TOO_THAT = 3003,
            SUBJECT_THAT = 3004,
            OBJECT_THAT = 3005,
            OBJECT_ONE_OF = 3006,
            OBJECT_SOME_VALUES_FROM = 3007,
            OBJECT_ALL_VALUES_FROM = 3008,
            OBJECT_COMPLEMENT_OF = 3009,
            ONLY_IF = 3010,
            SOMETHING = 3011,
            THAT = 3012,
            NOTHING_BUT = 3013,
            SUBCLASS_OF = 3014,
            EQUIVALENT = 3015,
            INTERSECTION_OF = 3016,
            UNION_OF = 3017,
            PERHAPS = 3018,
            WHO_WHAT = 3019,
            ALL = 3020,
            SUPERCLASS_OF = 3021,
            INSTANCE = 3022,

    DL = 3100,
            DL_BODY = 3101,
            DL_HEAD = 3102,
            INTERSECTION_OF_DL = 3103,
            CLASS_ATOM = 3104,
            OBJECT_PROPERTY_ATOM = 3105,

    IF = 3200,
            THEN = 3201;
}
