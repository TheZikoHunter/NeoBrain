package insea.neobrain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestBCrypt {
    public static void main(String[] args) {
        String hash = "$2a$12$Q0yqPxRmmr.8U8v8BMnlg.zX2xO.ac9RYLJ0P73Tah6yWrBIOIT2C"; // your hash
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println(encoder.matches("admin", hash)); // should print true
    }
}
