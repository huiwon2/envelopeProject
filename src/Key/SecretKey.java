package Key;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
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
            keyGenerator = KeyGenerator.getInstance("DES");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        keyGenerator.init(56);
        Key secretKey = keyGenerator.generateKey();

        System.out.print("비밀키를 저장할 파일이름 : ");
        fname = sc.next();

        try (FileOutputStream fos = new FileOutputStream(fname);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(secretKey);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

