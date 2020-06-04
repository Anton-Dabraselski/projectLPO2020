package projectLPO.parser;

public class SeasonTypeConvertor {
    public static int toInt(String str){
        switch (str)
        {
            case "Winter":
                return 0;
            case "Spring":
                return 1;
            case "Summer":
                return 2;
            case "Fall":
                return 3;
        }
        throw new IllegalArgumentException("Tale Seanson non esiste...");
    }
    public static String toString(int num){
        switch (num)
        {
            case 0:
                return "Winter";
            case 1:
                return "Spring";
            case 2:
                return "Summer";
            case 3:
                return "Fall";
        }
        throw new IllegalArgumentException("Numero del season sbagliato...");
    }
}
