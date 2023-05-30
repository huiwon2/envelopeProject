import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class SecretKey {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String fname;
        KeyGenerator keyGenerator;
//        key 생성
        try {
            keyGenerator = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        keyGenerator.init(128);
        Key secretKey = keyGenerator.generateKey();

        System.out.print("비밀키를 저장할 파일이름 : ");
        fname = sc.next();

        Cipher c1;
        try {
            c1 = Cipher.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        }
        try {
            c1.init(Cipher.ENCRYPT_MODE, secretKey);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }
        try(FileOutputStream bos = new FileOutputStream(fname);
            CipherOutputStream cos = new CipherOutputStream(bos, c1)){
            cos.write(secretKey.getEncoded());
            cos.flush();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
